/*
 * Prueba opcional de integración HTTP con Node.js 18+.
 * Usar un servidor de prueba: reinicia su partida.
 */
import assert from 'node:assert/strict';
const base=process.env.CHESS_URL||'http://127.0.0.1:8080';
const get=async path=>{const r=await fetch(base+path);return {status:r.status,data:await r.json()};};
const post=async(path,params,headers={})=>{
  const r=await fetch(base+path,{method:'POST',body:new URLSearchParams(params),headers});
  return {status:r.status,data:await r.json()};
};
let current=(await get('/api/state')).data;
current=(await post('/api/new',{revision:current.revision})).data.state;
assert.equal(current.board.length,32);
assert.deepEqual((await get('/api/legal?from=e2')).data.moves.sort(),['e3','e4']);
let result=await post('/api/move',{from:'e2',to:'e4',revision:current.revision});
assert.equal(result.data.accepted,true);
assert.equal(result.data.state.turn,'BLACK');
let stale=await post('/api/undo',{revision:current.revision});
assert.equal(stale.status,409);
current=result.data.state;
result=await post('/api/move',{from:'a1',to:'a3',revision:current.revision});
assert.equal(result.data.accepted,false);
const {elapsedMillis: ignoredBefore,...stableBefore}=current;
const {elapsedMillis: ignoredAfter,...stableAfter}=result.data.state;
assert.deepEqual(stableAfter,stableBefore);
assert.equal((await post('/api/move',{from:'z9',to:'e5',revision:current.revision})).status,400);
assert.equal((await post('/api/new',{revision:current.revision},{Origin:'https://example.invalid'})).status,403);
assert.equal((await post('/api/new',{revision:current.revision,padding:'x'.repeat(2100)})).status,413);
result=await post('/api/undo',{revision:current.revision});
assert.equal(result.data.state.turn,'WHITE');
assert.equal(result.data.state.canRedo,true);
current=result.data.state;
result=await post('/api/redo',{revision:current.revision});
assert.equal(result.data.state.turn,'BLACK');
assert.equal(result.data.state.history.length,1);
await post('/api/new',{revision:result.data.state.revision});
assert.equal((await fetch(base+'/styles.css')).status,200);
assert.equal((await fetch(base+'/nonexistent')).status,404);
console.log('API OK: estado, destinos, movimiento, rechazo sin mutación, revisión, formato, origen, tamaño, undo/redo y recursos.');

