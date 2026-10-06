package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;

/**
 * Movimiento en L del caballo; puede saltar obstáculos.
 */
public class KnightMovement implements IMovementStrategy {
    @Override
    public boolean canMove(Board board, Position from, Position to) {
        throw new UnsupportedOperationException("Pendiente: se implementa con TDD");
    }
}
