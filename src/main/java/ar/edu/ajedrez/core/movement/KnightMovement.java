package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;

/**
 * Movimiento en L del caballo; puede saltar obstáculos.
 */
public class KnightMovement implements IMovementStrategy {
    @Override
    public boolean canMove(Board board, Position from, Position to) {
        int rowDifference = Math.abs(to.row() - from.row());
        int columnDifference = Math.abs(to.column() - from.column());
        return (rowDifference == 2 && columnDifference == 1)
                || (rowDifference == 1 && columnDifference == 2);
    }
}
