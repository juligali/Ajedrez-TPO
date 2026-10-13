package ar.edu.ajedrez.core.validation;

import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.pieces.*;

public class CheckDetector {
    public boolean isAttacked(Board board, Position target, PieceColor byColor) {
        for (int r = 0; r < Board.SIZE; r++) for (int c = 0; c < Board.SIZE; c++) {
            Position from = new Position(r, c);
            Piece p = board.get(from);
            if (p != null && p.getColor() == byColor && p.canAttack(board, from, target)) return true;
        }
        return false;
    }
    public boolean isInCheck(Board board, PieceColor color) {
        for (int r = 0; r < Board.SIZE; r++) for (int c = 0; c < Board.SIZE; c++) {
            Piece p = board.get(r, c);
            if (p != null && p.getColor() == color && p.getType() == PieceType.KING)
                return isAttacked(board, new Position(r, c), color.opposite());
        }
        throw new IllegalStateException("Falta el rey " + color.label());
    }
}
