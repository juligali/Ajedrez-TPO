const {chromium}=require('playwright');
const assert=require('node:assert/strict');
(async()=>{
 const browser=await chromium.launch({channel:'chrome',headless:true});
 try{
  const page=await browser.newPage({viewport:{width:1440,height:1100}});
  const base=process.env.CHESS_URL||'http://127.0.0.1:8082';
  const errors=[];page.on('pageerror',e=>errors.push(e.message));
  await page.goto(base);await page.waitForSelector('#board img');
  // Primer ingreso: tutorial antes de configurar la partida.
  if(await page.locator('#tutorial-dialog').isVisible()){
   await page.screenshot({path:'build/tutorial-desktop.png',fullPage:true});
   for(const name of ['Peón','Caballo','Alfil','Torre','Reina','Rey']){
    await page.locator('#tutorial-tabs button').filter({hasText:new RegExp('^'+name+'$')}).click();
    assert((await page.locator('#tutorial-board .destination').count())>0);
   }
   await page.locator('#tutorial-board .destination').first().click();
   assert((await page.locator('#tutorial-example').textContent()).includes('→'));
   await page.locator('#tutorial-done').click();
   await page.locator('#confirm-new').click();
   await page.waitForSelector('#new-dialog',{state:'hidden'});
  }
  const newGame=async(mode='TWO_PLAYERS',difficulty='MEDIUM')=>{
   await page.locator('#new-button').click();await page.locator('#tutorial-done').click();
   await page.locator('input[value="'+mode+'"]').check();
   if(mode==='SOLO')await page.locator('#difficulty').selectOption(difficulty);
   await page.locator('#confirm-new').click();
   await page.waitForFunction(()=>document.querySelector('#history-count').textContent==='0 MOV.');
  };
  let count=0;
  const move=async(f,t)=>{
   await page.locator('[data-square="'+f+'"]').click();
   await page.waitForSelector('[data-square="'+t+'"].legal');
   await page.locator('[data-square="'+t+'"]').click();count++;
   await page.waitForFunction(n=>parseInt(document.querySelector('#history-count').textContent)>=n,count);
  };
  await newGame();count=0;
  await move('e2','e4');await move('d7','d5');await move('e4','d5');
  assert.equal(await page.locator('#bottom-player .player-score').textContent(),'1 pts');
  await page.locator('#undo-button').click();
  await page.waitForFunction(()=>document.querySelector('#bottom-player .player-score').textContent==='0 pts');
  await page.locator('#redo-button').click();
  await page.waitForFunction(()=>document.querySelector('#bottom-player .player-score').textContent==='1 pts');
  await page.locator('#save-button').click();
  await page.waitForFunction(()=>!document.querySelector('#save-button').disabled);
  await page.reload();await page.waitForSelector('#board img');
  assert.equal(await page.locator('#history-count').textContent(),'3 MOV.');
  assert.equal(await page.locator('#bottom-player .player-score').textContent(),'1 pts');
  assert.equal(await page.locator('#tutorial-dialog').isVisible(),false);
  await newGame();count=0;
  for(const [f,t]of [['f2','f3'],['e7','e5'],['g2','g4'],['d8','h4']])await move(f,t);
  assert.equal(await page.locator('.winner-crown').count(),1);
  assert(await page.locator('#top-player').evaluate(el=>el.classList.contains('winner')));
  await page.screenshot({path:'build/winner-desktop.png',fullPage:true});
  const stopped=await page.locator('#elapsed-time').textContent();
  await page.waitForTimeout(1100);
  assert.equal(await page.locator('#elapsed-time').textContent(),stopped);
  await page.locator('#undo-button').click();await page.waitForSelector('.winner-crown',{state:'detached'});
  for(const level of ['SUPER_EASY','EASY','MEDIUM','HARD','EXTREME']){
   await newGame('SOLO',level);count=0;
   await move('e2','e4');
   await page.waitForFunction(()=>document.querySelector('#history-count').textContent==='2 MOV.',{},{timeout:12000});
   await page.waitForFunction(()=>!document.querySelector('#undo-button').disabled);
   assert.equal((await (await page.request.get(base+'/api/state')).json()).turn,'WHITE');
   await page.locator('#undo-button').click();
   await page.waitForFunction(()=>document.querySelector('#history-count').textContent==='0 MOV.');
   await page.locator('#redo-button').click();
   await page.waitForFunction(()=>document.querySelector('#history-count').textContent==='2 MOV.');
  }
  await page.screenshot({path:'build/solo-desktop.png',fullPage:true});
  await page.setViewportSize({width:390,height:844});
  await page.screenshot({path:'build/new-mobile.png',fullPage:true});
  assert(await page.evaluate(()=>document.documentElement.scrollWidth<=window.innerWidth));
  await page.locator('#help-button').click();await page.screenshot({path:'build/tutorial-mobile.png',fullPage:true});
  assert(await page.locator('#tutorial-dialog').evaluate(el=>el.scrollWidth<=el.clientWidth+1));
  await page.locator('#tutorial-done').click();
  await newGame('TWO_PLAYERS');count=0;await move('e2','e4');
  await page.waitForTimeout(900);
  assert.equal(await page.locator('#history-count').textContent(),'1 MOV.');
  assert.equal((await (await page.request.get(base+'/api/ai')).json()).error,'Método no permitido');
  assert.deepEqual(errors,[]);
  console.log('UI OK: tutorial de seis piezas, puntos, corona, cronómetro, guardado, recarga, cinco niveles, undo/redo solo, IA desactivada en dos y móvil.');
 }finally{await browser.close();}
})().catch(e=>{console.error(e);process.exit(1)});

