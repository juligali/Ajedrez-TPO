package ar.edu.ajedrez.core.pieces;

import java.util.Objects;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;
import ar.edu.ajedrez.core.movement.IMovementStrategy;

/**
 * Una pieza concreta de la partida. Sus colaboradores entran por constructor.
 * Es inmutable (siempre que su estrategia también lo sea): por eso las copias
 * del tablero pueden compartir piezas sin riesgo. Su igualdad es por identidad.
 * Delega las reglas de movimiento y ataque en su estrategia, sin exponerla.
 */
public final class Piece {
    private final String name;              // solo para mostrar la pieza: ninguna regla depende de él
    private final PieceColor color;
    private final IMovementStrategy movement;
    private final boolean king;             // identifica al rey para detectar jaque

    public Piece(String name, PieceColor color, IMovementStrategy movement, boolean king) {
        this.name = Objects.requireNonNull(name, "name");
        this.color = Objects.requireNonNull(color, "color");
        this.movement = Objects.requireNonNull(movement, "movement");
        this.king = king;
    }

    public String name() {
        return name;
    }

    public PieceColor color() {
        return color;
    }

    public boolean isKing() {
        return king;
    }

    /** Ver IMovementStrategy.canMove: esta pieza debe estar en "from". */
    public boolean canMove(Board board, Position from, Position to) {
        return movement.canMove(board, from, to);
    }

    /** Ver IMovementStrategy.canAttack: esta pieza debe estar en "from". */
    public boolean canAttack(Board board, Position from, Position target) {
        return movement.canAttack(board, from, target);
    }
}
