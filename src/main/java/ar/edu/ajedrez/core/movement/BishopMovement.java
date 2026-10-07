package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;

/**
 * Estrategia de movimiento del alfil.
 *
 * El alfil se desplaza exclusivamente en diagonal
 * y no puede atravesar otras piezas.
 */
public class BishopMovement implements IMovementStrategy {

    @Override
    public boolean canMove(Board board, Position from, Position to) {

        // No existe movimiento si origen y destino son iguales.
        if (from.equals(to)) {
            return false;
        }

        // El alfil solamente puede desplazarse diagonalmente.
        if (!isDiagonalMovement(from, to)) {
            return false;
        }

        // Reutilizamos la comprobación común del recorrido.
        return PathClearChecker.isPathClear(board, from, to);
    }

    /**
     * En una diagonal, la diferencia absoluta de filas y columnas
     * debe ser exactamente la misma.
     */
    private boolean isDiagonalMovement(Position from, Position to) {

        int rowDifference =
                Math.abs(to.row() - from.row());

        int columnDifference =
                Math.abs(to.column() - from.column());

        return rowDifference == columnDifference;
    }
}