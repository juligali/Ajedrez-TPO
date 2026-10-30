// Mover los mismos controles conserva sus eventos y el estado de la partida.
const compactView=matchMedia('(max-width:1050px), (max-height:620px)');
const panelMoves=[
  [document.getElementById('legend-panel'),document.getElementById('piece-details-content')],
  [document.getElementById('match-panel'),document.getElementById('match-details-content')],
  [document.querySelector('.match-clock'),document.getElementById('compact-clock')],
  [document.querySelector('.undo-controls'),document.getElementById('compact-undo')]
].map(([element,target])=>{
  const anchor=document.createComment('Ubicación original del panel');
  element.before(anchor);
  return {element,target,anchor};
});
function adaptLayout(){
  if(compactView.matches){
    for(const {element,target} of panelMoves)target.append(element);
  }else{
    for(const id of ['match-details','piece-details'])document.getElementById(id).close();
    for(const {element,anchor} of panelMoves)anchor.after(element);
  }
}
compactView.addEventListener('change',adaptLayout);
adaptLayout();
for(const [button,dialog] of [['open-match','match-details'],['open-legend','piece-details']]){
  document.getElementById(button).addEventListener('click',()=>document.getElementById(dialog).showModal());
}
document.getElementById('compact-new').addEventListener('click',()=>document.getElementById('new-button').click());
// Cerrar el panel antes de abrir el tutorial o la configuración de una partida.
document.getElementById('new-button').addEventListener('click',()=>document.getElementById('match-details').close(),true);
const compactStatus=document.createElement('span');
compactStatus.className='compact-status';
compactStatus.setAttribute('role','status');
document.querySelector('.board-toolbar').prepend(compactStatus);
const statusTitle=document.getElementById('status-title');
const syncStatus=()=>{compactStatus.textContent=statusTitle.textContent;};
new MutationObserver(syncStatus).observe(statusTitle,{childList:true,subtree:true,characterData:true});
syncStatus();
