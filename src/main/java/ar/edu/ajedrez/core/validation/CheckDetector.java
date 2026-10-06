package ar.edu.ajedrez.core.validation;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.pieces.PieceColor;

/**
 * Determina si el rey de un color está amenazado: lo localiza con Piece.isKing()
 * y consulta canAttack de las piezas enemigas.
 */
public class CheckDetector {
    public boolean isInCheck(Board board, PieceColor color) {
        throw new UnsupportedOperationException("Pendiente: se implementa con TDD");
    }
}
