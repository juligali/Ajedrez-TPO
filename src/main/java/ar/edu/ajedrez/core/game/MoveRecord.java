package ar.edu.ajedrez.core.game;

import ar.edu.ajedrez.core.pieces.Piece;

/** Vista inmutable del historial: los adaptadores no reciben comandos ejecutables. */
public final class MoveRecord {
    private final Move move;
    private final Piece captured;
    private final String label;
    public MoveRecord(Move move, Piece captured, String label) {
        this.move = move; this.captured = captured; this.label = label;
    }
    public Move getMove() { return move; }
    public Piece getCaptured() { return captured; }
    public String getLabel() { return label; }
}
