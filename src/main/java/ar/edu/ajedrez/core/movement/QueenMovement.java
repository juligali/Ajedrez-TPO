package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.pieces.PieceColor;
public class QueenMovement implements IMovementStrategy {
    private final IMovementStrategy rook;
    private final IMovementStrategy bishop;
    public QueenMovement(IMovementStrategy rook, IMovementStrategy bishop) { this.rook = rook; this.bishop = bishop; }
    public boolean canMove(Board b, Position f, Position t, PieceColor color) {
        return rook.canMove(b, f, t, color) || bishop.canMove(b, f, t, color);
    }
}
