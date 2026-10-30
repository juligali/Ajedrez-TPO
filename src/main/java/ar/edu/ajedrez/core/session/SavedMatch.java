package ar.edu.ajedrez.core.session;

import java.util.*;
import ar.edu.ajedrez.core.game.Move;
import ar.edu.ajedrez.core.ai.Difficulty;

/** Datos suficientes para reconstruir tablero, derechos, capturas, undo y redo. */
public final class SavedMatch {
    public final List<Move> moves;
    public final int cursor;
    public final GameMode mode;
    public final Difficulty difficulty;
    public final long startedAt, endedAt;
    public final long elapsedMillis;
    public final boolean configured;
    public SavedMatch(List<Move> moves, int cursor, GameMode mode, Difficulty difficulty,
                      long startedAt, long endedAt, boolean configured) {
        this(moves,cursor,mode,difficulty,startedAt,endedAt,configured,
                endedAt==0 ? 0 : Math.max(0,endedAt-startedAt));
    }
    public SavedMatch(List<Move> moves, int cursor, GameMode mode, Difficulty difficulty,
                      long startedAt, long endedAt, boolean configured, long elapsedMillis) {
        if(cursor<0 || cursor>moves.size() || mode==null || difficulty==null || startedAt<0 || endedAt<0
                || elapsedMillis<0 || (configured && startedAt==0) || (endedAt!=0 && endedAt<startedAt))
            throw new IllegalArgumentException("Datos de partida inválidos.");
        this.moves=Collections.unmodifiableList(new ArrayList<>(moves)); this.cursor=cursor;
        this.mode=mode; this.difficulty=difficulty; this.startedAt=startedAt; this.endedAt=endedAt; this.configured=configured;
        this.elapsedMillis=elapsedMillis;
    }
}
