package ar.edu.ajedrez.core.game;

import ar.edu.ajedrez.core.board.Position;
import ar.edu.ajedrez.core.pieces.PieceType;

public final class Move {
    private final Position from;
    private final Position to;
    private final PieceType promotion;
    public Move(Position from, Position to) { this(from, to, PieceType.QUEEN); }
    public Move(Position from, Position to, PieceType promotion) { this.from = from; this.to = to; this.promotion = promotion; }
    public Position getFrom() { return from; }
    public Position getTo() { return to; }
    public PieceType getPromotion() { return promotion; }
}
