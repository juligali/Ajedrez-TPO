import {sprite,pieces} from './pieces.js';

/* Diagramas de enseñanza. No validan ni modifican la partida real. */
const examples=[
 {type:'PAWN',from:'d2',moves:['d3','d4'],captures:['c3','e3'],text:'Avanza una casilla hacia adelante. Desde su casilla inicial puede avanzar dos si el camino está libre. Captura una casilla en diagonal. Nunca retrocede.'},
 {type:'KNIGHT',from:'d4',moves:['b3','b5','c2','c6','e2','e6','f3','f5'],text:'Se mueve en L: dos casillas en una dirección y una hacia el costado. Es la única pieza que puede saltar sobre otras.'},
 {type:'BISHOP',from:'d4',moves:['a1','b2','c3','e5','f6','g7','h8','a7','b6','c5','e3','f2','g1'],text:'Se mueve en diagonal todas las casillas que quiera, siempre que el camino esté libre. Permanece en casillas del mismo color.'},
 {type:'ROOK',from:'d4',moves:['d1','d2','d3','d5','d6','d7','d8','a4','b4','c4','e4','f4','g4','h4'],text:'Se mueve en línea recta, horizontal o verticalmente. Puede recorrer varias casillas, pero no saltar piezas.'},
 {type:'QUEEN',from:'d4',moves:['d1','d2','d3','d5','d6','d7','d8','a4','b4','c4','e4','f4','g4','h4','a1','b2','c3','e5','f6','g7','h8','a7','b6','c5','e3','f2','g1'],text:'Combina los movimientos de torre y alfil: filas, columnas y diagonales. No puede saltar otras piezas.'},
 {type:'KING',from:'d4',moves:['c3','d3','e3','c4','e4','c5','d5','e5'],text:'Se mueve una casilla en cualquier dirección. Nunca puede entrar en una casilla amenazada. Este ejemplo no tiene amenazas rivales.'}
];
export function createTutorial(openSetup){
 const $=id=>document.getElementById(id);
 let index=0,prepare=false;
 function draw(){
   const example=examples[index],info=pieces[example.type];
   $('tutorial-step').textContent='PIEZA '+(index+1)+' DE 6';
   $('tutorial-piece').textContent=info.role+' · '+info.name;
   $('tutorial-value').textContent=info.points==null?'Sin valor numérico':info.points+' '+(info.points===1?'punto':'puntos');
   $('tutorial-text').textContent=example.text;
   $('tutorial-example').textContent='Tocá un destino marcado. Los aros indican capturas.';
   $('tutorial-prev').disabled=index===0;$('tutorial-next').disabled=index===5;
   $('tutorial-tabs').replaceChildren();
   examples.forEach((entry,i)=>{
     const button=document.createElement('button');button.textContent=pieces[entry.type].role;
     button.setAttribute('aria-pressed',String(i===index));
     button.addEventListener('click',()=>{index=i;draw();});
     $('tutorial-tabs').append(button);
   });
   const board=$('tutorial-board');board.replaceChildren();
   for(let rank=8;rank>=1;rank--)for(const file of 'abcdefgh'){
     const square=file+rank,cell=document.createElement('button');
     cell.className='demo-square'+((file.charCodeAt(0)-97+rank)%2?' dark':'');
     cell.dataset.demoSquare=square;
     const allowed=example.moves.includes(square)||(example.captures||[]).includes(square);
     if(allowed)cell.classList.add('destination');
     if((example.captures||[]).includes(square)){cell.classList.add('demo-capture');const enemy=document.createElement('img');enemy.src=sprite('PAWN','BLACK');enemy.alt='Peón rival para capturar';cell.append(enemy);}
     cell.setAttribute('aria-label',square+(allowed?', destino de ejemplo':''));
     if(square===example.from){const img=document.createElement('img');img.src=sprite(example.type);img.alt=info.role;img.dataset.actor='true';cell.append(img);cell.classList.add('demo-origin');}
     const coord=document.createElement('small');coord.textContent=square;cell.append(coord);
     cell.addEventListener('click',()=>{
       if(!allowed)return;
       board.querySelector('img[data-actor]')?.remove();cell.querySelector('img')?.remove();
       const img=document.createElement('img');img.src=sprite(example.type);img.alt=info.role;img.dataset.actor='true';cell.append(img);
       $('tutorial-example').textContent=info.role+': '+example.from+' → '+square+((example.captures||[]).includes(square)?'. Captura en diagonal.':'. Movimiento de ejemplo.');
     });
     board.append(cell);
   }
 }
 function close(){ $('tutorial-dialog').close();if(prepare)openSetup(); }
 $('tutorial-prev').addEventListener('click',()=>{if(index>0){index--;draw();}});
 $('tutorial-next').addEventListener('click',()=>{if(index<5){index++;draw();}});
 $('tutorial-reset').addEventListener('click',draw);
 $('tutorial-done').addEventListener('click',close);
 $('close-tutorial').addEventListener('click',close);
 $('tutorial-dialog').addEventListener('cancel',event=>{event.preventDefault();close();});
 return {open(forNew=false){
   prepare=forNew;index=0;draw();
   $('tutorial-done').textContent=prepare?'Elegir partida →':'Volver a la partida';
   $('tutorial-dialog').showModal();
 }};
}


