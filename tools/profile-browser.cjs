/* Dedicated test server only: this scenario REPLACES its saved game.
   CHESS_URL=http://127.0.0.1:18086 node tools/profile-browser.cjs */
const {chromium}=require('playwright');
const fs=require('node:fs');
const assert=require('node:assert/strict');
(async()=>{
 const base=process.env.CHESS_URL;
 if(!base || !/^http:\/\/127\.0\.0\.1:\d+$/.test(base)) throw Error('Set CHESS_URL to an isolated localhost test server');
 fs.mkdirSync('build/profile',{recursive:true});
 const browser=await chromium.launch({channel:'chrome',headless:true});
 try {
 const page=await browser.newPage({viewport:{width:1440,height:1100}});
 const errors=[];page.on('pageerror',e=>errors.push(e.message));page.on('console',m=>{if(m.type()==='error')errors.push(m.text());});
 await page.goto(base);await page.waitForSelector('#board img');
 if(await page.locator('#tutorial-dialog').isVisible()){await page.locator('#tutorial-done').click();await page.locator('#confirm-new').click();await page.waitForSelector('#new-dialog',{state:'hidden'});}
 const cdp=await page.context().newCDPSession(page);await cdp.send('Performance.enable');await cdp.send('HeapProfiler.enable');
 const metrics=async()=>Object.fromEntries((await cdp.send('Performance.getMetrics')).metrics.map(x=>[x.name,x.value]));
 const ready=async n=>{await page.waitForFunction(n=>document.querySelector('#history-count').textContent===n+' MOV.',n);await page.waitForFunction(()=>!document.querySelector('#new-button').disabled);};
 const reset=async()=>{await page.locator('#new-button').click();await page.locator('#tutorial-done').click();await page.locator('input[value="TWO_PLAYERS"]').check();await page.locator('#confirm-new').click();await ready(0);};
 const move=async(f,t,n)=>{await page.locator('[data-square="'+f+'"]').click();await page.waitForSelector('[data-square="'+t+'"].legal');await page.locator('[data-square="'+t+'"]').click();await ready(n);};
 await reset();
 const samples=[];
 for(let i=-9;i<=30;i++){
   const before=await metrics(),start=performance.now();
   await page.locator('[data-square="e2"]').click();await page.waitForSelector('[data-square="e4"].legal');
   const elapsed=performance.now()-start,after=await metrics();
   if(i>0)samples.push({sample:i,wall_ms:elapsed,browser_task_ms:(after.TaskDuration-before.TaskDuration)*1000});
   await page.locator('[data-square="e2"]').click();await page.waitForSelector('[data-square="e4"].legal',{state:'detached'});
 }
 const cycle=async()=>{
   for(const [i,[f,t]]of [['e2','e4'],['e7','e5'],['g1','f3'],['b8','c6'],['f1','b5'],['a7','a6']].entries())await move(f,t,i+1);
   await page.locator('#undo-button').click();await ready(5);await page.locator('#redo-button').click();await ready(6);await reset();
 };
 for(let i=0;i<5;i++)await cycle();
 const memory=[];
 const snapshot=async n=>{await cdp.send('HeapProfiler.collectGarbage');const m=await metrics();const dom=await cdp.send('Memory.getDOMCounters');memory.push({completed_cycles:n,JSHeapUsedSize:m.JSHeapUsedSize,JSHeapTotalSize:m.JSHeapTotalSize,...dom});};
 await snapshot(0);
 const before=await metrics();
 for(let i=1;i<=15;i++){await cycle();if(i%5===0)await snapshot(i);}
 const after=await metrics();
 // Basic keyboard path: focus a board button and activate it with Enter.
 await page.locator('[data-square="e2"]').focus();await page.keyboard.press('Enter');await page.waitForSelector('[data-square="e4"].legal');
 await page.locator('[data-square="e4"]').focus();await page.keyboard.press('Enter');await ready(1);
 await page.screenshot({path:'build/profile/profile-desktop.png',fullPage:true});
 await page.setViewportSize({width:390,height:844});assert(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth));
 await page.screenshot({path:'build/profile/profile-mobile.png',fullPage:true});
 assert.deepEqual(errors,[]);
 fs.writeFileSync('build/profile/browser-results.json',JSON.stringify({browser:await browser.version(),viewport:{width:1440,height:1100},warmup_queries:10,warmup_cycles:5,legal_query_samples:samples,memory,measured_cycles:15,cycle_browser_task_ms:(after.TaskDuration-before.TaskDuration)*1000,keyboard_enter_passed:true,mobile_no_horizontal_overflow:true,errors,limitations:'Wall time includes Playwright scheduling, HTTP and rendering. Browser TaskDuration excludes Java CPU. Forced GC is diagnostic. No network throttling or CPU throttling. Not a heap retainers audit.'},null,2));
 console.log('Browser profile OK: 30 legal-query samples, 15 cycles, desktop/mobile, Enter keyboard, no console errors.');
 }finally{await browser.close();}
})().catch(e=>{console.error(e);process.exitCode=1;});
