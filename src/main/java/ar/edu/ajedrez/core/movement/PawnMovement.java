package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;

/**
 * Movimiento del peón. Su dirección y su fila inicial entran por constructor,
 * así que la estrategia no asume el tamaño del tablero ni el color de la pieza.
 * No tiene estado mutable: las piezas de un mismo color pueden compartir una instancia.
 *
 * Reglas:
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
        int rowDifference = to.row() - from.row();
        int columnDifference = to.column() - from.column();

        if (columnDifference == 0) {
            if (!board.isEmpty(to)) {
                return false;
            }
            if (rowDifference == direction) {
                return true;
            }
            return from.row() == startRow
                    && rowDifference == 2 * direction
                    && board.isEmpty(new Position(from.row() + direction, from.column()));
        }

        if (!canAttack(board, from, to)) {
            return false;
        }
        return !board.isEmpty(to)
                && board.pieceAt(to).color() != board.pieceAt(from).color();
    }

    // El peon ataca en diagonal aunque la casilla este vacia.
    @Override
    public boolean canAttack(Board board, Position from, Position target) {
        return target.row() - from.row() == direction
                && Math.abs(target.column() - from.column()) == 1;
    }
}
