import './layout.js';
import {sprite, pieces} from './pieces.js';
import {createTutorial} from './tutorial.js';

const $=id=>document.getElementById(id);
let state=null, selected=null, destinations=[], flipped=false, busy=false, sound=false;
let selectionRequest=0, toastTimer, audioContext, pendingPromotion=null;
const squares=new Map();
let aiBusy=false, aiPaused=false, stateReceivedAt=performance.now();
const levels={SUPER_EASY:'Súper fácil',EASY:'Fácil',MEDIUM:'Medio',HARD:'Difícil',EXTREME:'Hiper mega difícil'};
function acceptState(value){state=value;stateReceivedAt=performance.now();}
function openSetup(){
  $('new-warning').textContent=state?.configured?'Una partida nueva reemplaza el guardado actual y reinicia los puntos y el cronómetro.':'Elegí un modo para empezar. Tu partida se guardará en este equipo.';
  $('new-dialog').showModal();
}
const tutorial=createTutorial(openSetup);
function tickClock(){
  if(!state)return;
  let ms=state.elapsedMillis+(state.configured&&!state.finished?performance.now()-stateReceivedAt:0);
  let seconds=Math.floor(ms/1000),hours=Math.floor(seconds/3600),minutes=Math.floor(seconds%3600/60);
  $('elapsed-time').textContent=(hours?String(hours).padStart(2,'0')+':':'')+String(minutes).padStart(2,'0')+':'+String(seconds%60).padStart(2,'0');
}
setInterval(tickClock,1000);
async function maybeAi(){
  if(!state?.aiTurn||busy||aiBusy||aiPaused)return;
  aiBusy=true;render();
  await action('ai');
  aiBusy=false;if(state)render();
}


function image(type,color,alt=''){
  const img=document.createElement('img');
  img.src=sprite(type,color);img.alt=alt;img.draggable=false;return img;
}
function notify(message,error=false){
  const toast=$('toast');toast.textContent=message;toast.classList.toggle('error',error);toast.hidden=false;
  clearTimeout(toastTimer);toastTimer=setTimeout(()=>toast.hidden=true,4500);
}
async function request(path,params){
  const options=params ? {method:'POST',body:new URLSearchParams({...params,revision:state.revision})} : {};
  const response=await fetch('/api/'+path,options);
  const result=await response.json();
  if(!response.ok)throw new Error(result.error||'No se pudo conectar con la partida.');
  return result;
}
function clearSelection(){
  selected=null;destinations=[];selectionRequest++;
  $('piece-tip').textContent='Seleccioná una pieza para ver sus movimientos posibles.';
}
async function action(path,params={}){
  if(busy||!state)return;
  busy=true;clearSelection();updateControls();
  try{
    const result=await request(path,params);
    acceptState(result.state);render();
    if(!result.accepted){notify(result.message,true);if(path==='ai')aiPaused=true;}
    else {playSound(path==='move' ? (state.check?600:420) : 320);if(path!=='move')notify(result.message);}
  }catch(error){
    if(path==='ai')aiPaused=true;
    notify(error.message,true);
    try{acceptState(await request('state'));render();}catch{showDisconnected();}
  }finally{busy=false;updateControls();if(path!=='ai')maybeAi();}
}
async function selectSquare(square){
  if(busy||aiBusy||!state||state.finished)return;
  if(!state.configured){tutorial.open(true);return;}
  if(state.aiTurn){notify('Ahora le toca a la IA.');return;}
  if(selected===square){clearSelection();renderBoard();return;}
  const piece=state.board.find(p=>p.square===square);
  if(selected&&destinations.includes(square)){
    const moving=state.board.find(p=>p.square===selected);
    if(moving.type==='PAWN' && (square.endsWith('8')||square.endsWith('1'))){
      pendingPromotion={from:selected,to:square};
      $('promotion-options').replaceChildren();
      for(const type of ['QUEEN','ROOK','BISHOP','KNIGHT']){
        const button=document.createElement('button');button.append(image(type,state.turn));
        button.append(document.createTextNode(pieces[type].role));
        button.addEventListener('click',()=>{
          const move=pendingPromotion;
          $('promotion-dialog').close();
          if(move)action('move',{...move,promotion:type});
        });
        $('promotion-options').append(button);
      }
      $('promotion-dialog').showModal();return;
    }
    await action('move',{from:selected,to:square});return;
  }
  if(!piece||piece.color!==state.turn){
    if(selected)notify('Elegí una de las casillas marcadas o seleccioná otra pieza.');
    else if(piece)notify('Es el turno de las '+(state.turn==='WHITE'?'blancas.':'negras.'));
    return;
  }
  selected=square;destinations=[];const token=++selectionRequest;
  $('piece-tip').textContent=pieces[piece.type].tip;
  renderBoard();
  try{
    const result=await request('legal?from='+square);
    if(token!==selectionRequest)return;
    if(result.revision!==state.revision){clearSelection();acceptState(await request('state'));render();return;}
    destinations=result.moves;renderBoard();
  }catch(error){clearSelection();renderBoard();notify(error.message,true);}
}
function buildBoard(){
  squares.clear();$('board').replaceChildren();
  const ranks=flipped?[1,2,3,4,5,6,7,8]:[8,7,6,5,4,3,2,1];
  const files=flipped?'hgfedcba':'abcdefgh';
  $('ranks').replaceChildren(...ranks.map(r=>{const s=document.createElement('span');s.textContent=r;return s;}));
  $('files').replaceChildren(...[...files].map(c=>{const s=document.createElement('span');s.textContent=c;return s;}));
  for(const rank of ranks)for(const file of files){
    const square=file+rank,button=document.createElement('button');
    button.type='button';button.className='square';button.dataset.square=square;
    button.addEventListener('click',()=>selectSquare(square));
    button.addEventListener('keydown',event=>{
      const directions={ArrowLeft:-1,ArrowRight:1,ArrowUp:-8,ArrowDown:8};
      if(event.key==='Escape'){clearSelection();renderBoard();return;}
      if(!(event.key in directions))return;
      event.preventDefault();
      const all=[...squares.values()],i=all.indexOf(button),next=i+directions[event.key];
      if(next>=0&&next<64 && (Math.abs(directions[event.key])===8||Math.floor(i/8)===Math.floor(next/8)))all[next].focus();
    });
    squares.set(square,button);$('board').append(button);
  }
}
function renderBoard(){
  if(!state)return;
  const bySquare=new Map(state.board.map(p=>[p.square,p]));
  const last=state.history.at(-1);
  for(const [square,button]of squares){
    const piece=bySquare.get(square);
    button.className='square';
    button.classList.toggle('dark',(square.charCodeAt(0)-97+Number(square[1]))%2===1);
    button.classList.toggle('selected',selected===square);
    button.classList.toggle('last',!!last&&(last.from===square||last.to===square));
    button.classList.toggle('legal',destinations.includes(square));
    button.classList.toggle('capture',destinations.includes(square)&&!!piece);
    button.classList.toggle('in-check',!!piece&&piece.type==='KING'&&piece.color===state.turn&&state.check);
    button.setAttribute('aria-pressed',String(selected===square));
    button.setAttribute('aria-label',square+(piece?', '+pieces[piece.type].role+' '+(piece.color==='WHITE'?'blanco':'negro')+', '+pieces[piece.type].name:', vacía')+(destinations.includes(square)?', destino permitido':''));
    const signature=piece?piece.type+piece.color:'empty';
    if(button.dataset.piece!==signature){
      button.replaceChildren();
      if(piece)button.append(image(piece.type,piece.color));
      const coord=document.createElement('span');coord.className='coord';coord.textContent=square;button.append(coord);
      button.dataset.piece=signature;
    }
  }
}
function player(container,color){
  container.replaceChildren();
  const avatar=document.createElement('div');avatar.className='avatar';avatar.append(image('KING',color));
  const details=document.createElement('div');
  const name=document.createElement('div');name.className='player-name';name.textContent=state.mode==='SOLO'?(color==='WHITE'?'Vos · Blancas':'IA · '+levels[state.difficulty]):(color==='WHITE'?'Equipo claro':'Equipo oscuro');
  container.classList.toggle('winner',state.winner===color);
  if(state.winner===color){const crown=document.createElement('span');crown.className='winner-crown';crown.textContent='👑';crown.title='Ganador por jaque mate';crown.setAttribute('aria-label','Ganador');name.prepend(crown);}
  if(state.turn===color&&!state.finished){const tag=document.createElement('span');tag.className='active-label';tag.textContent='TU TURNO';name.append(tag);}
  const subtitle=document.createElement('div');subtitle.className='player-detail';subtitle.textContent=(color==='WHITE'?'Blancas':'Negras')+' · '+state.board.filter(p=>p.color===color).length+' piezas';
  details.append(name,subtitle);
  const captures=document.createElement('div');captures.className='player-captures';captures.setAttribute('aria-label','Piezas capturadas por '+(color==='WHITE'?'blancas':'negras'));
  for(const p of state.captures.filter(p=>p.color!==color))captures.append(image(p.type,p.color,pieces[p.type].role+' capturado'));
  const score=document.createElement('div');score.className='player-score';score.textContent=(color==='WHITE'?state.whitePoints:state.blackPoints)+' pts';score.title='Puntos por piezas capturadas';
  container.append(avatar,details,captures,score);
}
function updateControls(){
  $('undo-button').disabled=busy||aiBusy||!state?.history.length;
  $('redo-button').disabled=busy||aiBusy||!state?.canRedo;
  $('new-button').disabled=busy||aiBusy||!state;
  $('save-button').disabled=busy||aiBusy||!state?.configured;
  $('retry-ai').hidden=!aiPaused||!state?.aiTurn;
  $('board').setAttribute('aria-busy',String(busy));
}
function render(){
  renderBoard();
  player($('top-player'),flipped?'WHITE':'BLACK');
  player($('bottom-player'),flipped?'BLACK':'WHITE');
  const white=state.turn==='WHITE';
  const status={
    PLAYING:[white?'Juegan las blancas':'Juegan las negras','Elegí una pieza y construí tu próxima jugada.'],
    CHECK:['¡Jaque!','Tu rey está amenazado. Movelo, bloqueá el ataque o capturá la amenaza.'],
    CHECKMATE:['¡Jaque mate!','Victoria del equipo '+(white?'oscuro.':'claro.')+' ¿Listos para la revancha?'],
    STALEMATE:['Tablas por ahogado','No hay movimientos legales, pero el rey no está en jaque.'],
    MATERIAL:['Tablas','No queda material suficiente para dar jaque mate.'],
    REPETITION:['Tablas por repetición','La misma posición apareció tres veces.'],
    FIFTY_MOVES:['Tablas · 50 jugadas','50 movimientos por bando sin capturas ni avances de peón.']
  }[state.status];
  $('status-title').textContent=status[0];$('status-description').textContent=status[1];
  document.querySelector('.status-kicker').textContent=state.finished?'PARTIDA TERMINADA':state.check?'PROTEGÉ A STEVE':'LA AVENTURA CONTINÚA';
  $('status-card').classList.toggle('alert',state.check);
  $('turn-marker').classList.toggle('black',!white);
  $('turn-label').textContent=state.finished?'Fin de la partida':white?'Turno del equipo claro':'Turno del equipo oscuro';
  $('move-number').textContent='Nº '+(Math.floor(state.history.length/2)+1).toString().padStart(2,'0');
  $('history-count').textContent=state.history.length+' MOV.';
  const history=$('history');history.replaceChildren();
  if(!state.history.length){
    const empty=document.createElement('div');empty.className='history-empty';
    empty.innerHTML='<span aria-hidden="true">♟</span>La historia empieza acá.<small>Hacé el primer movimiento.</small>';history.append(empty);
  }else{
    for(let i=0;i<state.history.length;i+=2){
      const row=document.createElement('div');row.className='history-row';
      for(const text of [String(i/2+1).padStart(2,'0'),state.history[i].label,state.history[i+1]?.label||'—']){
        const cell=document.createElement('span');cell.textContent=text;row.append(cell);
      }
      history.append(row);
    }
    history.scrollTop=history.scrollHeight;
  }
  $('mode-label').textContent=state.mode==='SOLO'?'CONTRA IA · '+levels[state.difficulty].toUpperCase():'DOS JUGADORES · LOCAL';
  $('save-state').textContent=state.configured?'✓ Partida guardada en este equipo':'Elegí un modo para empezar';
  if(!state.configured){$('status-title').textContent='¿Listo para jugar?';$('status-description').textContent='Aprendé los movimientos y elegí uno o dos jugadores.';}
  if(aiBusy&&state.aiTurn){$('status-title').textContent='La IA está pensando…';$('status-description').textContent='Nivel '+levels[state.difficulty]+'. Esperá su movimiento.';}
  if(state.winner)$('status-title').textContent='👑 ¡Jaque mate!';
  tickClock();updateControls();
}
function showDisconnected(){
  $('status-title').textContent='Sin conexión';
  $('status-description').textContent='Abrí Jugar.bat y recargá esta página para conectar con el servidor.';
  state=null;updateControls();
}
function playSound(frequency){
  if(!sound)return;
  try{
    audioContext ||= new AudioContext();
    audioContext.resume();
    const oscillator=audioContext.createOscillator(),gain=audioContext.createGain();
    oscillator.type='triangle';oscillator.frequency.setValueAtTime(frequency,audioContext.currentTime);
    oscillator.frequency.exponentialRampToValueAtTime(frequency*.7,audioContext.currentTime+.10);
    gain.gain.setValueAtTime(.07,audioContext.currentTime);gain.gain.exponentialRampToValueAtTime(.001,audioContext.currentTime+.13);
    oscillator.connect(gain);gain.connect(audioContext.destination);oscillator.start();oscillator.stop(audioContext.currentTime+.14);
    oscillator.onended=()=>{oscillator.disconnect();gain.disconnect();};
  }catch{sound=false;$('sound-button').textContent='♫ Sonido apagado';$('sound-button').setAttribute('aria-pressed','false');}
}
for(const [type,info]of Object.entries(pieces)){
  const row=document.createElement('div');row.className='legend-row';
  row.append(image(type,'WHITE'));
  const text=document.createElement('div');
  text.innerHTML='<span class="piece-role">'+info.role+'</span><strong>'+info.name+'</strong><small>'+info.movement+' · '+(info.points==null?'Sin valor':info.points+' pts')+'</small>';
  row.append(text);$('legend').append(row);
}
$('help-button').addEventListener('click',()=>tutorial.open(!state?.configured));
document.querySelectorAll('[data-close]').forEach(button=>button.addEventListener('click',()=>$(button.dataset.close).close()));
$('promotion-dialog').addEventListener('close',()=>{pendingPromotion=null;});
$('undo-button').addEventListener('click',()=>action('undo'));
$('redo-button').addEventListener('click',()=>action('redo'));
$('new-button').addEventListener('click',()=>tutorial.open(true));
$('confirm-new').addEventListener('click',()=>{
  $('new-dialog').close();aiPaused=false;
  action('new',{mode:document.querySelector('input[name="mode"]:checked').value,difficulty:$('difficulty').value});
});
document.querySelectorAll('input[name="mode"]').forEach(input=>input.addEventListener('change',()=>{$('difficulty-group').hidden=document.querySelector('input[name="mode"]:checked').value!=='SOLO';}));
$('save-button').addEventListener('click',()=>action('save'));
$('retry-ai').addEventListener('click',()=>{aiPaused=false;maybeAi();});
$('flip-button').addEventListener('click',()=>{flipped=!flipped;buildBoard();if(state)render();});
$('sound-button').addEventListener('click',()=>{
  sound=!sound;$('sound-button').textContent=sound?'♫ Sonido encendido':'♫ Sonido apagado';
  $('sound-button').setAttribute('aria-pressed',String(sound));if(sound)playSound(460);
});
document.addEventListener('visibilitychange',async()=>{
  if(document.hidden||busy||!state||document.querySelector('dialog[open]'))return;
  try{
    const current=await request('state');
    if(current.revision!==state.revision){clearSelection();acceptState(current);render();maybeAi();}
  }catch{/* La próxima acción permite reintentar sin perder el estado visible. */}
});
buildBoard();
try{acceptState(await request('state'));render();if(!state.configured)tutorial.open(true);else maybeAi();}catch(error){showDisconnected();notify('No se pudo conectar con Java. Ejecutá Jugar.bat y recargá la página.',true);}
