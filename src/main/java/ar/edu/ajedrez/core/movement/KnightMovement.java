package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.pieces.PieceColor;
public class KnightMovement implements IMovementStrategy {
    public boolean canMove(Board b, Position f, Position t, PieceColor color) {
        int r = Math.abs(f.getRow() - t.getRow()), c = Math.abs(f.getCol() - t.getCol());
        return r * c == 2;
    }
}
