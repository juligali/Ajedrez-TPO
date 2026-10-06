package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;


public interface IMovementStrategy {
    /** La pieza puede realizar este movimiento según sus reglas. */
    boolean canMove(Board board, Position from, Position to);

    /**
     * La pieza amenaza la casilla, esté ocupada o no.
     * Por defecto amenaza las casillas a las que puede moverse; el peón la sobreescribe
     * porque avanza hacia adelante pero ataca en diagonal.
     */
    default boolean canAttack(Board board, Position from, Position target) {
        return canMove(board, from, target);
    }
}
