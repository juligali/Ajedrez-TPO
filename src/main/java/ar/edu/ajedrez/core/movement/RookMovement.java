package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.pieces.PieceColor;
public class RookMovement implements IMovementStrategy {
    @Override
    public boolean canMove(Board b, Position f, Position t, PieceColor color) {
        return !f.equals(t) && (f.getRow() == t.getRow() || f.getCol() == t.getCol()) && b.pathClear(f, t);
    }
}
