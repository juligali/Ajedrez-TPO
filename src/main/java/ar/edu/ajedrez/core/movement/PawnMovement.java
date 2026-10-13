package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.pieces.*;
public class PawnMovement implements IMovementStrategy {
    @Override
    public boolean canAttack(Board b, Position f, Position t, PieceColor color) {
        return t.getRow() - f.getRow() == color.direction() && Math.abs(t.getCol() - f.getCol()) == 1;
    }
    @Override
    public boolean canMove(Board b, Position f, Position t, PieceColor color) {
        if (canAttack(b, f, t, color)) {
            if (b.get(t) != null) return b.get(t).getColor() != color;
            Piece adjacent = b.get(f.getRow(), t.getCol());
            return t.equals(b.getEnPassant()) && adjacent != null && adjacent.getType() == PieceType.PAWN && adjacent.getColor() != color;
        }
        if (f.getCol() != t.getCol() || b.get(t) != null) return false;
        int step = color.direction();
        if (t.getRow() - f.getRow() == step) return true;
        int start = color == PieceColor.WHITE ? 6 : 1;
        return f.getRow() == start && t.getRow() - f.getRow() == 2 * step && b.get(f.getRow() + step, f.getCol()) == null;
    }
}
