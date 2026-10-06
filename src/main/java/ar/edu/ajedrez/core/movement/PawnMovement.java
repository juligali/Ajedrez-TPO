package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;

/**
 * Movimiento del peón. Su dirección y su fila inicial entran por constructor,
 * así que la estrategia no asume el tamaño del tablero ni el color de la pieza.
 * No tiene estado mutable: las piezas de un mismo color pueden compartir una instancia.
 *
 * Reglas (se implementan con TDD):
 * - Avanza una casilla en su dirección si el destino está vacío.
 * - Desde su fila inicial puede avanzar dos casillas si ambas están vacías.
 * - Captura una casilla en diagonal hacia adelante si hay una pieza enemiga.
 * - Amenaza (ataca) las dos casillas diagonales hacia adelante, estén o no ocupadas.
 */
public class PawnMovement implements IMovementStrategy {
    private final int direction; // +1 o -1: sentido en que crece o decrece la fila al avanzar
    private final int startRow;  // fila inicial: solo desde ahí puede avanzar dos casillas

    public PawnMovement(int direction, int startRow) {
        if (direction != 1 && direction != -1) {
            throw new IllegalArgumentException("direction must be +1 or -1, was " + direction);
        }
        if (startRow < 0) {
            throw new IllegalArgumentException("startRow must not be negative, was " + startRow);
        }
        this.direction = direction;
        this.startRow = startRow;
    }

    @Override
    public boolean canMove(Board board, Position from, Position to) {
        throw new UnsupportedOperationException("Pendiente: se implementa con TDD");
    }

    // Único caso en que atacar difiere de moverse: ataca en diagonal aunque la casilla esté vacía.
    @Override
    public boolean canAttack(Board board, Position from, Position target) {
        throw new UnsupportedOperationException("Pendiente: se implementa con TDD");
    }
}
