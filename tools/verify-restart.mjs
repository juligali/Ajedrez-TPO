import assert from 'node:assert/strict';
import {spawn,spawnSync} from 'node:child_process';
import path from 'node:path';
import {once} from 'node:events';
const base='http://127.0.0.1:8083';
const javaSettings=spawnSync('java',['-XshowSettings:properties','-version'],{encoding:'utf8',windowsHide:true});
const javaRoot=javaSettings.stderr.match(/java.home = (.+)/)?.[1].trim();
if(!javaRoot)throw Error('No se pudo localizar el JDK real.');
const javaExe=path.join(javaRoot,'bin',process.platform==='win32'?'java.exe':'java');
let child;
async function start(){
 child=spawn(javaExe,['-cp','target/classes','ar.edu.ajedrez.app.WebServer','8083','build/restart-test.properties'],{windowsHide:true,stdio:['ignore','pipe','pipe']});
 let output='';child.stdout.on('data',d=>output+=d);child.stderr.on('data',d=>output+=d);
 for(let i=0;i<100;i++){
  if(child.exitCode!==null)throw Error(output);
  if(output.includes('Ajedrez de bloques listo'))return;
  await new Promise(r=>setTimeout(r,100));
 }
 throw Error('El servidor de prueba no inició: '+output);
}
async function stop(){const finished=once(child,'exit');child.kill();await finished;}
async function state(){return (await fetch(base+'/api/state')).json();}
async function action(path,params={}){const s=await state();const r=await fetch(base+'/api/'+path,{method:'POST',body:new URLSearchParams({...params,revision:s.revision})});const result=await r.json();assert(result.accepted,result.message);return result.state;}
try{
 await start();await action('new',{mode:'TWO_PLAYERS',difficulty:'HARD'});
 await action('move',{from:'e2',to:'e4'});await action('move',{from:'d7',to:'d5'});await action('move',{from:'e4',to:'d5'});await action('undo');
 const before=await state();await stop();await start();
 const after=await state();
 assert.deepEqual(after.board,before.board);assert.deepEqual(after.history,before.history);
 assert.equal(after.canRedo,true);assert.equal(after.whitePoints,0);assert.equal(after.turn,'WHITE');
 assert.equal(after.difficulty,'HARD');assert(after.elapsedMillis>=before.elapsedMillis);
 const restored=await action('redo');assert.equal(restored.whitePoints,1);assert.equal(restored.history.length,3);
 console.log('Reinicio real OK: tablero, historial, turno, nivel, tiempo y redo con captura recuperados después de cerrar Java.');
}finally{if(child&&child.exitCode===null)await stop();}

