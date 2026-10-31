/* Automated backup/restore drill. Uses only build/backup-drill, port 18087.
   JAVA_EXE may point to Java 21. Never uses data/current-match.properties. */
const {spawn}=require('node:child_process');
const fs=require('node:fs/promises');const path=require('node:path');
const assert=require('node:assert/strict');const {createHash}=require('node:crypto');
const base='http://127.0.0.1:18087';let child;
const dir=path.resolve('build/backup-drill');const save=path.join(dir,'match.properties');
const state=async()=>{const r=await fetch(base+'/api/state');assert.equal(r.status,200);return r.json();};
const start=async()=>{child=spawn(process.env.JAVA_EXE||'java',['-cp','target/classes','ar.edu.ajedrez.app.WebServer','18087',save],{windowsHide:true,stdio:'ignore'});child.on('error',e=>{throw e;});for(let i=0;i<100;i++){if(child.exitCode!==null)throw Error('Test server exited');try{return await state();}catch{await new Promise(r=>setTimeout(r,100));}}throw Error('Server timeout');};
const stop=async()=>{if(child&&child.exitCode===null){const done=new Promise(r=>child.once('exit',r));child.kill();await done;}child=null;};
const post=async(route,params={})=>{const s=await state();const r=await fetch(base+'/api/'+route,{method:'POST',body:new URLSearchParams({...params,revision:s.revision})});assert.equal(r.status,200);const data=await r.json();assert.equal(data.accepted,true);return data.state;};
const comparable=s=>{const {revision,...rest}=s;return rest;};
(async()=>{
 try{await fetch(base+'/api/state');throw Error('Port 18087 is in use; refuse to alter it');}catch(e){if(e.message.includes('in use'))throw e;}
 await fs.mkdir(dir,{recursive:true});
 try {
 await start();await post('new');
 for(const [from,to]of [['e2','e4'],['d7','d5'],['e4','d5']])await post('move',{from,to});
 await post('undo');const before=await post('save');assert.equal(before.canRedo,true);
 const wrongOrigin=await fetch(base+'/api/new',{method:'POST',headers:{Origin:'https://example.invalid'},body:new URLSearchParams({revision:before.revision})});assert.equal(wrongOrigin.status,403);
 const stale=await fetch(base+'/api/new',{method:'POST',body:new URLSearchParams({revision:-1})});assert.equal(stale.status,409);
 await stop();
 const bytes=await fs.readFile(save),hash=createHash('sha256').update(bytes).digest('hex');await fs.copyFile(save,path.join(dir,'backup.properties'));
 await start();await post('new');await stop();
 await fs.copyFile(path.join(dir,'backup.properties'),save);
 assert.equal(createHash('sha256').update(await fs.readFile(save)).digest('hex'),hash);
 const after=await start();assert.deepEqual(comparable(after),comparable(before));
 const redone=await post('redo');assert.equal(redone.whitePoints,1);assert.equal(redone.history.length,3);
 await stop();
 const report={result:'PASS',automated:true,personal_game_used:false,backup_sha256:hash,checks:['server stopped during copies','backup bytes restored exactly','board, turn, mode, difficulty, history, captures, points, redo, elapsed time and status restored','redo restores capture and one point','cross-origin POST rejected 403','stale revision rejected 409'],before,after};
 await fs.writeFile(path.join(dir,'result.json'),JSON.stringify(report,null,2));console.log('Backup/restore and HTTP checks PASS');
 }finally{await stop();}
})().catch(e=>{console.error(e);process.exitCode=1;});
