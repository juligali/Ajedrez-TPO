package ar.edu.ajedrez.core.session;

import java.time.Clock;
import java.util.function.Supplier;
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

    public MatchSession(Supplier<IGameService> gameFactory, IGameRepository repository, IChessAi ai, Clock clock) {
        this.gameFactory=gameFactory; this.repository=repository; this.ai=ai; this.clock=clock;
        game=gameFactory.get();
        repository.load().ifPresent(this::restore);
    }
    private void restore(SavedMatch saved) {
        IGameService restored=gameFactory.get();
        for(Move move:saved.moves)
            if(!restored.move(move).isAccepted()) throw new IllegalArgumentException("El archivo contiene una jugada ilegal.");
        for(int i=saved.moves.size();i>saved.cursor;i--) restored.undo();
        game=restored; mode=saved.mode; difficulty=saved.difficulty;
        startedAt=saved.startedAt; endedAt=saved.endedAt; configured=saved.configured;
    }
    public SavedMatch snapshot() {
        return new SavedMatch(game.getTimeline(),game.getHistory().size(),mode,difficulty,startedAt,endedAt,configured);
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
    public MoveResult newGame(GameMode mode, Difficulty difficulty) {
        if(mode==null || difficulty==null) throw new IllegalArgumentException("Elegí modo y nivel.");
        SavedMatch before=snapshot();
        game=gameFactory.get(); this.mode=mode; this.difficulty=difficulty;
        configured=true; startedAt=clock.millis(); endedAt=0;
        return persist(before,new MoveResult(true,"Partida creada y guardada."));
    }
    public MoveResult move(Move move) {
        if(!configured) return new MoveResult(false,"Elegí el modo de partida antes de empezar.");
        if(isAiTurn()) return new MoveResult(false,"Ahora le toca a la IA.");
        SavedMatch before=snapshot();
        return persist(before,game.move(move));
    }
    public MoveResult playAi() {
        if(!isAiTurn()) return new MoveResult(false,"No es el turno de la IA.");
        Move move=ai.chooseMove(game.getBoard(),game.getTurn(),difficulty);
        if(move==null) return new MoveResult(false,"La IA no encontró una jugada.");
        SavedMatch before=snapshot();
        return persist(before,game.move(move));
    }
    public MoveResult undo() {
        SavedMatch before=snapshot();
        MoveResult result=game.undo();
        // En solo, volver a la decisión anterior del humano, no deshacer sólo la respuesta.
        if(result.isAccepted() && mode==GameMode.SOLO && game.getTurn()==PieceColor.BLACK && !game.getHistory().isEmpty()) game.undo();
        return persist(before,result);
    }
    public MoveResult redo() {
        SavedMatch before=snapshot();
        MoveResult result=game.redo();
        if(result.isAccepted() && mode==GameMode.SOLO && game.getTurn()==PieceColor.BLACK && game.canRedo()) game.redo();
        return persist(before,result);
    }
    public MoveResult save() {
        try { repository.save(snapshot()); return new MoveResult(true,"Partida guardada en este equipo."); }
        catch(PersistenceException error) { return new MoveResult(false,"No se pudo guardar. Revisá permisos o espacio en disco."); }
    }
    public IGameService getGame() { return game; }
    public boolean isAiTurn() { return configured && mode==GameMode.SOLO && game.getTurn()==PieceColor.BLACK && !game.isFinished(); }
    public boolean isConfigured() { return configured; }
    public GameMode getMode() { return mode; }
    public Difficulty getDifficulty() { return difficulty; }
    public long getElapsedMillis() { return startedAt==0 ? 0 : Math.max(0,(endedAt==0 ? clock.millis() : endedAt)-startedAt); }
}
