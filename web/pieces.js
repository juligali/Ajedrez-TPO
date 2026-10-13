/*
 * Modelos voxel originales hechos con cubos. Sólo presentación: no conocen reglas de ajedrez.
 * Se dibujan una vez por tipo/color y se reutilizan como imágenes (12 entradas en caché).
 */
const cache = new Map();
export const pieces = {
  KING:   {points:null, name:'Steve', role:'Rey', movement:'Una casilla', tip:'Steve se mueve una casilla en cualquier dirección. Nunca puede quedar en jaque.'},
  QUEEN:  {points:9, name:'Alex', role:'Reina', movement:'Rectas y diagonales', tip:'Alex recorre filas, columnas y diagonales, sin saltar por encima de otras piezas.'},
  ROOK:   {points:5, name:'Gólem de hierro', role:'Torre', movement:'En línea recta', tip:'El gólem avanza en horizontal o vertical todas las casillas libres que quieras.'},
  BISHOP: {points:3, name:'Bruja', role:'Alfil', movement:'En diagonal', tip:'La bruja se mueve en diagonal y siempre permanece en casillas del mismo color.'},
  KNIGHT: {points:3, name:'Caballo', role:'Caballo', movement:'Salta en L', tip:'El caballo avanza dos casillas y una al costado. Es la única pieza que salta sobre otras.'},
  PAWN:   {points:1, name:'Creeper', role:'Peón', movement:'Avanza y captura', tip:'El creeper avanza una casilla (o dos desde el inicio), pero captura una casilla en diagonal.'},
};
function shade(hex, amount) {
  const value=parseInt(hex.slice(1),16);
  return '#' + [16,8,0].map(shift=>Math.max(0,Math.min(255,((value>>shift)&255)+amount)).toString(16).padStart(2,'0')).join('');
}
function model(type, color) {
  const cubes=[];
  const cube=(x,y,z,w,d,h,c)=>cubes.push({x,y,z,w,d,h,c});
  const white=color==='WHITE';
  cube(-7,-5,0,14,10,1.4,white?'#dddcc5':'#242d30');
  cube(-7,-5,1.4,14,10,.6,white?'#e6bd58':'#b45439');
  cube(-6.5,-4.5,2,13,9,1.1,white?'#f2efda':'#3b4443');
  cube(-5.5,-3.5,3.1,11,7,.4,white?'#c8c5b0':'#252e2b');
  if(type==='PAWN'){
    cube(-3,-2,6,6,4,10,'#60aa39');
    cube(-4,-3,16,8,6,8,'#78c34a');
    for(const x of [-3.3,1])for(const y of [-2.8,.7]){
      cube(x,y,3.5,2.5,2.5,3.5,'#5b9a32');
      cube(x,y+2.5,3.5,2.5,.08,.8,'#314f26');
    }
    const patches=[[-3,18,1.4,2],[-.3,22,1.4,1], [2.2,17,.8,1.1],[-3.5,22,1,1],[-.8,17,1,1],[2,21,1.6,1]];
    for(const [x,z,w,h] of patches)cube(x,3.015,z,w,.045,h,'#8fce59');
    cube(-2.6,3.1,20,2,.1,1.9,'#152d17');cube(.9,3.1,20,2,.1,1.9,'#152d17');
    cube(-.85,3.1,18.7,1.9,.1,1.8,'#152d17');cube(-1.9,3.1,17.3,4,.1,1.9,'#152d17');
    cube(-1.9,3.1,16.5,1,.1,1.5,'#152d17');cube(1.1,3.1,16.5,1,.1,1.5,'#152d17');
    for(const [x,z] of [[-2,9],[1,13],[-1,15],[2,7]])cube(x,2.02,z,1.1,.05,1.4,'#387e32');
    cube(4.015,-1.5,19,.05,1.5,2,'#5a9e36');
  } else if(type==='KING'||type==='QUEEN'){
    const alex=type==='QUEEN',skin=alex?'#e8b185':'#b98560',hair=alex?'#cf661f':'#493125',shirt=alex?'#698c3f':'#22a9ad',pants=alex?'#765338':'#514a88';
    cube(-3.2,-1.8,3.5,2.8,3.6,7,pants);cube(.5,-1.8,3.5,2.8,3.6,7,pants);
    cube(-3.2,-1.8,3.5,2.8,4.2,1.4,'#3d4241');cube(.5,-1.8,3.5,2.8,4.2,1.4,'#3d4241');
    cube(-3.7,-2,10.5,7.4,4,8.6,shirt);
    cube(-6.2,-1.8,10.4,2.5,3.6,5.7,skin);cube(3.7,-1.8,10.4,2.5,3.6,5.7,skin);
    cube(-6.2,-1.8,16.1,2.5,3.6,3,shirt);cube(3.7,-1.8,16.1,2.5,3.6,3,shirt);
    cube(-1,2.03,17,2,.12,2,skin);
    cube(-4,-3,19.1,8,6,7.6,skin);
    cube(-4,-3,24.9,8,6,2,hair);cube(-4,-3,19.1,8,1.2,7.6,hair);
    cube(-4,2.95,23.9,alex?4.2:2.4,.22,1.2,hair);
    cube(2.7,2.96,24,1.3,.15,1.1,hair);
    if(alex){cube(-4.25,-2.5,18,1.3,5.6,7.7,hair);cube(-3.8,2.1,15.4,1.7,.65,3.8,hair);}
    else{cube(-1.9,3.08,19.7,3.8,.1,2.1,hair);cube(-.9,3.2,20.6,1.9,.1,.7,skin);}
    cube(-2.8,3.07,22.3,2.1,.12,1,'#eee7d6');cube(.9,3.07,22.3,2.1,.12,1,'#eee7d6');
    cube(-1.7,3.2,22.3,.85,.1,1,alex?'#54844b':'#5367a0');cube(1.1,3.2,22.3,.85,.1,1,alex?'#54844b':'#5367a0');
    cube(-.65,3.15,21.1,1.4,.35,1,shade(skin,-17));
    cube(-4.3,-3.3,26.6,8.6,6.6,1.7,'#e8b632');
    for(const x of [-4.3,-.8,2.9])cube(x,2.15,28.3,1.4,1.2,2.2,'#f8ce4d');
    for(const x of [-4.3,2.9])cube(x,-3.3,28.3,1.4,1.2,2.2,'#f3c33f');
    cube(-.55,3.4,27,.95,.15,1.2,alex?'#53c4b9':'#c1513b');
    if(!alex){
      for(let i=0;i<8;i++)cube(5+i*.45,2.4,12+i*1.25,1.3,1,1.5,'#175654');
      for(let i=0;i<7;i++)cube(5.4+i*.45,3.4,13+i*1.25,.7,.15,1.4,'#4adfd2');
      cube(4.5,2,11.8,3.6,1.7,.9,'#32796c');cube(5.3,2.35,9.7,.9,1,2.4,'#695339');
    }else{cube(-2,2.07,10.6,4,.1,.8,'#3f522b');}
  } else if(type==='ROOK'){
    cube(-3.8,-2,3.5,3,4,8,'#c2c4b6');cube(.8,-2,3.5,3,4,8,'#d5d6c8');
    cube(-5.2,-2.8,11.5,10.4,5.6,10.5,'#dddccd');
    cube(-8.1,-2.2,7,2.9,4.4,14,'#cccebf');cube(5.2,-2.2,7,2.9,4.4,14,'#dcded0');
    cube(-3.2,-2.2,22,6.4,4.7,6.2,'#dfdfd1');
    cube(-3,2.6,24.5,2.2,.4,.8,'#786f60');cube(.8,2.6,24.5,2.2,.4,.8,'#786f60');
    cube(-2.5,3.01,24.3,1,.1,.55,'#a64030');cube(1,3.01,24.3,1,.1,.55,'#a64030');
    cube(-.8,2.5,21.6,1.6,1.7,3.1,'#aaa896');
    for(const [x,z,w,h]of [[-3.8,18,1.2,4],[-3,15,1,4],[-1.8,14,1.4,2],[-3,12,1,2],[2,17,1,2]]){
      cube(x,2.85,z,w,.13,h,'#617c3d');
    }
    cube(-3.8,3.02,18.5,1.2,.1,.7,'#b3a346');cube(-6.9,2.25,11,1,.1,3,'#718144');
    cube(1.1,2.85,12.5,2.2,.12,.9,'#b9b6a5');cube(-1,2.85,20,3,.12,.5,'#bab6a5');
  } else if(type==='BISHOP'){
    cube(-2.8,-1.8,3.5,2.2,4,3,'#3a3029');cube(.7,-1.8,3.5,2.2,4,3,'#3a3029');
    cube(-4.2,-2.7,6.5,8.4,5.4,10.1,'#51316e');cube(-3.7,-2.4,16.6,7.4,4.8,3.2,'#673889');
    cube(-.6,2.76,6.7,1.2,.1,9.6,'#83509d');
    cube(-4,-3,19.5,8,6,6,'#cba58a');
    cube(-2.7,3.1,22.4,1.7,.1,.6,'#382431');cube(1.1,3.1,22.4,1.7,.1,.6,'#382431');
    cube(-.6,3,20.3,1.5,2,2.4,'#b58c70');cube(.2,5.02,21,.6,.1,.6,'#66563b');
    cube(-5.4,-4,25.5,10.8,8,1.5,'#282834');
    cube(-3.4,-2.5,27,6.8,5,2.3,'#47683d');
    cube(-3.4,-2.5,29.3,6.8,5,1.5,'#302839');
    cube(-2.5,-2.1,30.8,4.5,3.5,3.1,'#2a2531');
    cube(-2.1,-2,33.9,2.7,2.7,2.6,'#383040');
    cube(-5.5,-1,12,2.4,3.4,6,'#533273');cube(3.1,-1,12,2.4,3.4,6,'#533273');
    cube(-4,2.5,12,8,2,2.5,'#96735a');cube(1.5,3.9,10.7,3.2,2.1,3.2,'#aa43c7');
    cube(2.1,4.3,13.9,1.8,1.3,1.1,'#dfbfea');cube(2.4,4.4,15,1.2,1.1,.7,'#8a6546');
    cube(2,6.05,11.4,.7,.1,1.7,'#edb1f5');
  } else if(type==='KNIGHT'){
    for(const x of [-3.5,1.5])for(const y of [-3.6,2.2]){
      cube(x,y,3.5,2,2.1,8,'#8b552e');cube(x,y,3.5,2,2.1,2,'#3a3430');
      cube(x,y,5.5,2,2.1,1.3,'#d5c8aa');
    }
    cube(-3.7,-4.5,11.5,7.4,10,5.8,'#9a5e30');
    cube(-2.5,1.9,16,5,4.4,7.7,'#a46835');
    cube(-2.7,1.6,23.7,5.4,5.5,4.1,'#98592d');
    cube(-2.6,5.1,21.7,5.2,4.4,3.6,'#aa713e');
    cube(-2.6,7.6,21.7,5.2,1.9,1.7,'#815235');
    cube(-2.4,1.9,27.8,1.3,1.9,3.1,'#9c6b44');cube(1.1,1.9,27.8,1.3,1.9,3.1,'#9c6b44');
    cube(-1.4,1.3,23.8,2.8,1.3,4.3,'#493022');cube(-1.4,.9,16.4,2.8,1.1,7.4,'#493022');
    cube(-2.75,5,24.9,.15,1,1.2,'#e3d3b7');cube(2.72,5,24.9,.15,1,1.2,'#e3d3b7');
    cube(2.87,5.2,24.9,.1,.5,.8,'#211d1c');cube(-2.9,5.2,24.9,.1,.5,.8,'#211d1c');
    cube(-.9,7.15,25,1.8,.1,2.7,'#cbb79c');
    cube(-1.1,-6.3,8,2.2,2,8.6,'#493022');
    cube(-3.8,-2,16.9,7.6,4.6,.8,'#63422d');
    cube(-3.8,3.8,12,.2,1,4.9,'#d0bb8a');cube(3.7,3.8,12,.2,1,4.9,'#d0bb8a');
    cube(-2.75,6.5,23.5,5.5,.6,.7,'#d6bf93');
  }
  return cubes;
}
export function sprite(type, color='WHITE') {
  const key=type+color;
  if(cache.has(key))return cache.get(key);
  const canvas=document.createElement('canvas');canvas.width=240;canvas.height=280;
  const ctx=canvas.getContext('2d');
  const scale=6.35, originY=246;
  const project=([x,y,z])=>[120+(x*.94-y*.48)*scale,originY+(x*.24+y*.42-z*.95)*scale];
  const faces=[];
  for(const b of model(type,color)){
    const {x,y,z,w,d,h,c}=b;
    const add=(points,fill)=>{
      const middle=points.reduce((a,p)=>a.map((v,i)=>v+p[i]/4),[0,0,0]);
      faces.push({points,fill,depth:middle[0]*.51+middle[1]+middle[2]*.57});
    };
    add([[x,y,z+h],[x+w,y,z+h],[x+w,y+d,z+h],[x,y+d,z+h]],shade(c,22));
    add([[x+w,y,z],[x+w,y+d,z],[x+w,y+d,z+h],[x+w,y,z+h]],shade(c,-27));
    add([[x,y+d,z],[x+w,y+d,z],[x+w,y+d,z+h],[x,y+d,z+h]],c);
  }
  faces.sort((a,b)=>a.depth-b.depth);
  for(const face of faces){
    ctx.beginPath();
    face.points.forEach((p,i)=>{const [x,y]=project(p);if(i===0)ctx.moveTo(x,y);else ctx.lineTo(x,y);});
    ctx.closePath();ctx.fillStyle=face.fill;ctx.fill();
    ctx.lineWidth=.3;ctx.strokeStyle=face.fill;ctx.stroke();
  }
  const url=canvas.toDataURL('image/png');cache.set(key,url);return url;
}
