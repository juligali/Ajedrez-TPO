package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.pieces.PieceColor;
public class BishopMovement implements IMovementStrategy {
    public boolean canMove(Board b, Position f, Position t, PieceColor color) {
        return !f.equals(t) && Math.abs(f.getRow() - t.getRow()) == Math.abs(f.getCol() - t.getCol()) && b.pathClear(f, t);
    }
}
