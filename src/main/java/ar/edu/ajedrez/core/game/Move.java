package ar.edu.ajedrez.core.game;

import java.util.Objects;

import ar.edu.ajedrez.core.board.Position;

/**
 * Solicitud de movimiento: de qué casilla a cuál.
 * Es un objeto de datos; no ejecuta ni deshace acciones, así que no es el patrón Command.
 */
public record Move(Position from, Position to) {
    public Move {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
    }
}
