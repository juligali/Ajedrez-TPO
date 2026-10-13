/*
 * Verificación opcional del adaptador visual.
 * Requiere Node.js + Playwright + Chrome instalado y un servidor de PRUEBA activo.
 * Reinicia la partida. CHESS_URL permite elegir otro puerto.
 */
const { chromium } = require('playwright');
(async()=>{
  const browser=await chromium.launch({channel:'chrome',headless:true});
  try {
    const page=await browser.newPage({viewport:{width:1440,height:1100},deviceScaleFactor:1});
    const errors=[];
    page.on('pageerror',e=>errors.push(e.message));
    const base=process.env.CHESS_URL||'http://127.0.0.1:8080';
    const initial=await (await page.request.get(base+'/api/state')).json();
    await page.request.post(base+'/api/new',{form:{revision:String(initial.revision)}});
    await page.goto(base);
    await page.waitForSelector('#board .square img');
    if(await page.locator('#board img').count()!==32)throw Error('Initial pieces');
    let count=0;
    const move=async(from,to)=>{
      await page.locator('[data-square="'+from+'"]').click();
      await page.waitForSelector('[data-square="'+to+'"].legal');
      await page.locator('[data-square="'+to+'"]').click();
      count++;
      await page.waitForFunction(n=>document.querySelector('#history-count').textContent===n+' MOV.',count);
    };
    const reset=async()=>{
      await page.locator('#new-button').click();await page.locator('#tutorial-done').click();
      await page.locator('#confirm-new').click();
      await page.waitForFunction(()=>document.querySelector('#history-count').textContent==='0 MOV.');
      count=0;
    };
    await move('e2','e4');await move('d7','d5');await move('e4','d5');
    if(await page.locator('#board img').count()!==31)throw Error('Capture');
    await page.locator('#undo-button').click();
    await page.waitForFunction(()=>document.querySelectorAll('#board img').length===32);
    await page.locator('#redo-button').click();
    await page.waitForFunction(()=>document.querySelectorAll('#board img').length===31);
    await page.locator('#flip-button').click();
    if(await page.locator('#board button').first().getAttribute('data-square')!=='h1')throw Error('Flip');
    await page.locator('#help-button').click();
    if(!await page.locator('#tutorial-dialog').isVisible())throw Error('Help');
    await page.keyboard.press('Escape');
    await page.locator('#flip-button').click();
    await reset();
    await move('f2','f3');await move('e7','e5');await move('g2','g4');await move('d8','h4');
    if(await page.locator('#status-title').textContent()!=='👑 ¡Jaque mate!')throw Error('Checkmate');
    await page.locator('#undo-button').click();
    await page.waitForFunction(()=>document.querySelector('#status-title').textContent==='Juegan las negras');
    await reset();
    // Un peón recorre el tablero por una secuencia legal hasta promocionar capturando.
    for(const [from,to] of [['a2','a4'],['h7','h5'],['a4','a5'],['h5','h4'],['a5','a6'],['h4','h3'],['a6','b7'],['h3','g2']])
      await move(from,to);
    await page.locator('[data-square="b7"]').click();
    await page.waitForSelector('[data-square="a8"].legal');
    await page.locator('[data-square="a8"]').click();
    await page.locator('#promotion-dialog').waitFor({state:'visible'});
    if(await page.locator('#promotion-options button').count()!==4)throw Error('Promotion options');
    await page.screenshot({path:'build/promotion.png',fullPage:true});
    await page.keyboard.press('Escape');
    if(await page.locator('[data-square="b7"]').getAttribute('data-piece')!=='PAWNWHITE')throw Error('Cancel promotion mutated board');
    await page.locator('[data-square="a8"]').click();
    await page.locator('#promotion-options button').filter({hasText:'Caballo'}).click();
    await page.waitForFunction(()=>document.querySelector('[data-square="a8"]').dataset.piece==='KNIGHTWHITE');
    await page.locator('#undo-button').click();
    await page.waitForFunction(()=>document.querySelector('[data-square="a8"]').dataset.piece==='ROOKBLACK');
    await page.locator('#redo-button').click();
    await page.waitForFunction(()=>document.querySelector('[data-square="a8"]').dataset.piece==='KNIGHTWHITE');
    await reset();
    // Teclado y casillas destacadas.
    await page.locator('[data-square="e2"]').focus();await page.keyboard.press('Enter');
    await page.waitForSelector('[data-square="e4"].legal');
    await page.keyboard.press('ArrowUp');
    if(await page.locator(':focus').getAttribute('data-square')!=='e3')throw Error('Keyboard');
    await page.keyboard.press('Escape');
    await page.locator('#toast').waitFor({state:'hidden'});
    await page.setViewportSize({width:390,height:844});
    await page.screenshot({path:'build/mobile.png',fullPage:true});
    if(await page.evaluate(()=>document.documentElement.scrollWidth>window.innerWidth))throw Error('Horizontal overflow');
    await page.setViewportSize({width:1440,height:1100});
    await page.screenshot({path:'build/desktop.png',fullPage:true});
    const grid=await page.locator('#board').boundingBox();
    const first=await page.locator('#board button').first().boundingBox();
    const last=await page.locator('#board button').last().boundingBox();
    if(last.y+last.height>grid.y+grid.height+1)throw Error('Board last row clipped');
    if(Math.abs(first.width-first.height)>1)throw Error('Non-square cells');
    if(errors.length)throw Error(errors.join(';'));
    console.log('UI OK: 32 piezas, jugadas, captura, undo/redo, giro, ayuda, mate, promoción/cancelación, teclado, 390px y filas sin recorte.');
    console.log('Errores de JavaScript: 0.');
  } finally {await browser.close();}
})().catch(error=>{console.error(error);process.exit(1);});

