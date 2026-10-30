// El servidor acumula el tiempo; el navegador sólo informa si la vista está abierta y visible.
export function startActivityTracking(onUpdate) {
  const clientId=crypto.randomUUID();
  let leaving=false, inFlight=false;
  async function heartbeat(){
    if(leaving || document.hidden || inFlight)return;
    inFlight=true;
    try{
      const response=await fetch('/api/presence',{
        method:'POST',body:new URLSearchParams({clientId,active:'true'}),keepalive:true
      });
      if(response.ok)onUpdate(await response.json());
    }catch{/* Sin conexión, el servidor deja vencer la actividad y conserva el último guardado. */}
    finally{inFlight=false;}
  }
  function pause(){
    const body=new URLSearchParams({clientId,active:'false'});
    if(!navigator.sendBeacon('/api/presence',body))
      fetch('/api/presence',{method:'POST',body,keepalive:true}).catch(()=>{});
  }
  document.addEventListener('visibilitychange',()=>document.hidden?pause():heartbeat());
  window.addEventListener('pagehide',()=>{leaving=true;pause();});
  window.addEventListener('pageshow',()=>{leaving=false;heartbeat();});
  setInterval(heartbeat,5000);
  heartbeat();
}
