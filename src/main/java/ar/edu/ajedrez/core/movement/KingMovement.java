package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;

/**
 * Movimiento del rey de una casilla en cualquier dirección.
 */
public class KingMovement implements IMovementStrategy {
    @Override
    public boolean canMove(Board board, Position from, Position to) {
        throw new UnsupportedOperationException("Pendiente: se implementa con TDD");
    }

}
