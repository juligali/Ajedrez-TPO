package ar.edu.ajedrez.core.validation;

import java.util.Objects;
import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;
import ar.edu.ajedrez.core.pieces.PieceColor;

/**
 * Localiza al rey con Piece.isKing() y consulta canAttack de las piezas enemigas.
 * No modifica el tablero ni simula movimientos. Una pieza clavada sigue
 * amenazando las casillas de su patron de ataque.
 */
public class CheckDetector {
    /**
     * @throws IllegalStateException si el color consultado no tiene exactamente un rey
     */
    public boolean isInCheck(Board board, PieceColor color) {
        Objects.requireNonNull(board, "board");
        Objects.requireNonNull(color, "color");
        Position kingPosition = null;
        for (Position position : board.positionsOf(color)) {
            if (board.pieceAt(position).isKing()) {
                if (kingPosition != null) {
                    throw new IllegalStateException("Multiple kings for " + color);
                }
                kingPosition = position;
            }
        }
        if (kingPosition == null) {
            throw new IllegalStateException("Missing king for " + color);
        }

        PieceColor enemy = color == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;
        for (Position position : board.positionsOf(enemy)) {
            if (board.pieceAt(position).canAttack(board, position, kingPosition)) {
                return true;
            }
        }
        return false;
    }
}
