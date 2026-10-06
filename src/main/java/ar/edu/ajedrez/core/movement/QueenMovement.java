package ar.edu.ajedrez.core.movement;

import java.util.Objects;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;

/**
 * La reina se mueve como una torre o como un alfil: reutiliza esas estrategias
 * por composición en lugar de heredar de ellas.
 */
public class QueenMovement implements IMovementStrategy {
    private final IMovementStrategy rook;
    private final IMovementStrategy bishop;

    public QueenMovement(IMovementStrategy rook, IMovementStrategy bishop) {
        this.rook = Objects.requireNonNull(rook, "rook");
        this.bishop = Objects.requireNonNull(bishop, "bishop");
    }

    @Override
    public boolean canMove(Board board, Position from, Position to) {
        return rook.canMove(board, from, to) || bishop.canMove(board, from, to);
    }

    // Se compone también canAttack: con el método por defecto se ignoraría
    // cualquier diferencia entre mover y atacar de las estrategias componentes.
    @Override
    public boolean canAttack(Board board, Position from, Position target) {
        return rook.canAttack(board, from, target) || bishop.canAttack(board, from, target);
    }
}
