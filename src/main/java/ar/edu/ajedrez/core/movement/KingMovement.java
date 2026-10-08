package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;

/**
 * Movimiento del rey de una casilla en cualquier dirección.
 */
public class KingMovement implements IMovementStrategy {
    @Override
    public boolean canMove(Board board, Position from, Position to) {
        int rowDifference = Math.abs(to.row() - from.row());
        int columnDifference = Math.abs(to.column() - from.column());
        // La seguridad del rey y el destino con pieza propia los valida MoveValidator.
        return Math.max(rowDifference, columnDifference) == 1;
    }

}
