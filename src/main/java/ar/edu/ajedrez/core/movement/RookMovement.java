package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;

/**
 * Movimiento horizontal y vertical de la torre; el camino debe estar libre.
 */
public class RookMovement implements IMovementStrategy {
    @Override
    public boolean canMove(Board board, Position from, Position to) {
        throw new UnsupportedOperationException("Pendiente: se implementa con TDD");
    }
}
