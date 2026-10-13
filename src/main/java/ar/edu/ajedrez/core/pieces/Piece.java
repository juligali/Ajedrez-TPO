package ar.edu.ajedrez.core.pieces;

import ar.edu.ajedrez.core.movement.IMovementStrategy;
import ar.edu.ajedrez.core.board.*;

/** Una pieza tiene un comportamiento. No necesitamos seis subclases de Piece. */
public final class Piece {
    private final PieceType type;
    private final PieceColor color;
    private final IMovementStrategy movement;
    private final boolean moved;
    public Piece(PieceType type, PieceColor color, IMovementStrategy movement) {
        this(type, color, movement, false);
    }
    private Piece(PieceType type, PieceColor color, IMovementStrategy movement, boolean moved) {
        this.type = type; this.color = color; this.movement = movement; this.moved = moved;
    }
    public PieceType getType() { return type; }
    public PieceColor getColor() { return color; }
    public boolean hasMoved() { return moved; }
    public Piece movedCopy() { return new Piece(type, color, movement, true); }
    public boolean canMove(Board board, Position from, Position to) { return movement.canMove(board, from, to, color); }
    public boolean canAttack(Board board, Position from, Position to) { return movement.canAttack(board, from, to, color); }
}
