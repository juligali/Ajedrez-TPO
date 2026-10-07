package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;

/**
 * Estrategia de movimiento de la torre.
 *
 * La torre se desplaza horizontal o verticalmente
 * y no puede atravesar otras piezas.
 */
public class RookMovement implements IMovementStrategy {

    @Override
    public boolean canMove(Board board, Position from, Position to) {

        // No existe movimiento si origen y destino son iguales.
        if (from.equals(to)) {
            return false;
        }

        // La torre debe conservar la fila o la columna.
        if (!isStraightMovement(from, to)) {
            return false;
        }

        // La comprobación del recorrido se reutiliza entre piezas deslizantes.
        return PathClearChecker.isPathClear(board, from, to);
    }

    /**
     * Comprueba que el movimiento sea horizontal o vertical.
     */
    private boolean isStraightMovement(Position from, Position to) {
        return from.row() == to.row()
                || from.column() == to.column();
    }
}
