package ar.edu.ajedrez.core.session;

import java.time.Clock;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.Map;
import ar.edu.ajedrez.core.ai.*;
import ar.edu.ajedrez.core.game.*;
import ar.edu.ajedrez.core.pieces.PieceColor;

/** Casos de uso: modo, IA, cronómetro y persistencia. Las reglas siguen en Game. */
public class MatchSession implements IMatchService {
    private IGameService game;
    private final Supplier<IGameService> gameFactory;
    private final IGameRepository repository;
    private final IChessAi ai;
    private final Clock clock;
    private GameMode mode=GameMode.TWO_PLAYERS;
    private Difficulty difficulty=Difficulty.MEDIUM;
    private long startedAt, endedAt;
    private boolean configured;
    private long elapsedMillis, lastClockUpdate;
    private final Map<String,Long> activeClients=new HashMap<>();
    private static final long ACTIVITY_LEASE_MILLIS=15000;

    public MatchSession(Supplier<IGameService> gameFactory, IGameRepository repository, IChessAi ai, Clock clock) {
        this.gameFactory=gameFactory; this.repository=repository; this.ai=ai; this.clock=clock;
        game=gameFactory.get();
        repository.load().ifPresent(this::restore);
        lastClockUpdate=clock.millis();
    }
    private void restore(SavedMatch saved) {
        IGameService restored=gameFactory.get();
        for(Move move:saved.moves)
            if(!restored.move(move).isAccepted()) throw new IllegalArgumentException("El archivo contiene una jugada ilegal.");
        for(int i=saved.moves.size();i>saved.cursor;i--) restored.undo();
        game=restored; mode=saved.mode; difficulty=saved.difficulty;
        startedAt=saved.startedAt; endedAt=saved.endedAt; configured=saved.configured;
        elapsedMillis=saved.elapsedMillis; lastClockUpdate=clock.millis();
    }
    public SavedMatch snapshot() {
        return new SavedMatch(game.getTimeline(),game.getHistory().size(),mode,difficulty,startedAt,endedAt,configured,getElapsedMillis());
    }
    /** Si falla la escritura, la operación no se confirma y se restaura la sesión anterior. */
    private MoveResult persist(SavedMatch before, MoveResult result) {
        if(!result.isAccepted()) return result;
        endedAt=game.isFinished() ? (endedAt==0 ? clock.millis() : endedAt) : 0;
        try { repository.save(snapshot()); return result; }
        catch(PersistenceException error) {
            restore(before);
            return new MoveResult(false,"No se pudo guardar. No se aplicó el cambio. Revisá permisos o espacio en disco.");
        }
    }
    @Override
    public MoveResult newGame(GameMode mode, Difficulty difficulty) {
        if(mode==null || difficulty==null) throw new IllegalArgumentException("Elegí modo y nivel.");
        SavedMatch before=snapshot();
        game=gameFactory.get(); this.mode=mode; this.difficulty=difficulty;
        configured=true; startedAt=clock.millis(); endedAt=0;
        elapsedMillis=0; lastClockUpdate=clock.millis();
        return persist(before,new MoveResult(true,"Partida creada y guardada."));
    }
    @Override
    public MoveResult move(Move move) {
        if(!configured) return new MoveResult(false,"Elegí el modo de partida antes de empezar.");
        if(isAiTurn()) return new MoveResult(false,"Ahora le toca a la IA.");
        SavedMatch before=snapshot();
        return persist(before,game.move(move));
    }
    @Override
    public MoveResult playAi() {
        if(!isAiTurn()) return new MoveResult(false,"No es el turno de la IA.");
        Move move=ai.chooseMove(game.getBoard(),game.getTurn(),difficulty);
        if(move==null) return new MoveResult(false,"La IA no encontró una jugada.");
        SavedMatch before=snapshot();
        return persist(before,game.move(move));
    }
    @Override
    public MoveResult undo() {
        SavedMatch before=snapshot();
        MoveResult result=game.undo();
        // En solo, volver a la decisión anterior del humano, no deshacer sólo la respuesta.
        if(result.isAccepted() && mode==GameMode.SOLO && game.getTurn()==PieceColor.BLACK && !game.getHistory().isEmpty()) game.undo();
        return persist(before,result);
    }
    @Override
    public MoveResult redo() {
        SavedMatch before=snapshot();
        MoveResult result=game.redo();
        if(result.isAccepted() && mode==GameMode.SOLO && game.getTurn()==PieceColor.BLACK && game.canRedo()) game.redo();
        return persist(before,result);
    }
    @Override
    public MoveResult save() {
        try { repository.save(snapshot()); return new MoveResult(true,"Partida guardada en este equipo."); }
        catch(PersistenceException error) { return new MoveResult(false,"No se pudo guardar. Revisá permisos o espacio en disco."); }
    }
    @Override
    public IGameService getGame() { return game; }
    @Override
    public boolean isAiTurn() { return configured && mode==GameMode.SOLO && game.getTurn()==PieceColor.BLACK && !game.isFinished(); }
    @Override
    public boolean isConfigured() { return configured; }
    @Override
    public GameMode getMode() { return mode; }
    @Override
    public Difficulty getDifficulty() { return difficulty; }
    @Override
    public long getElapsedMillis() {
        long now=clock.millis();
        long activeUntil=activeClients.values().stream().mapToLong(Long::longValue).max().orElse(lastClockUpdate);
        if(configured && !game.isFinished())
            elapsedMillis+=Math.max(0,Math.min(now,activeUntil)-lastClockUpdate);
        lastClockUpdate=now;
        activeClients.values().removeIf(until->until<=now);
        return elapsedMillis;
    }
    @Override
    public boolean isClockRunning() {
        getElapsedMillis();
        return configured && !game.isFinished() && !activeClients.isEmpty();
    }
    @Override
    public MoveResult presence(String clientId, boolean active) {
        if(clientId==null || !clientId.matches("[A-Za-z0-9-]{1,80}"))
            throw new IllegalArgumentException("Identificador de actividad inválido.");
        getElapsedMillis();
        if(active) {
            if(!activeClients.containsKey(clientId) && activeClients.size()>=32)
                throw new IllegalArgumentException("Demasiadas vistas activas.");
            activeClients.put(clientId,clock.millis()+ACTIVITY_LEASE_MILLIS);
        } else activeClients.remove(clientId);
        return save();
    }
}
