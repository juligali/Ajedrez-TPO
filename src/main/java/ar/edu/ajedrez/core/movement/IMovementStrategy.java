package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.pieces.PieceColor;

public interface IMovementStrategy {
    boolean canMove(Board board, Position from, Position to, PieceColor color);
    default boolean canAttack(Board board, Position from, Position to, PieceColor color) {
        return canMove(board, from, to, color);
    }
}
