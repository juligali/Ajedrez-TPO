package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;

/**
 * Estrategia responsable de validar el movimiento propio de una torre.
 *
 * La torre:
 * - se mueve horizontal o verticalmente;
 * - no puede permanecer en la misma casilla;
 * - no puede atravesar otras piezas.
 *
 * Las reglas generales de la partida, como turnos, capturas propias
 * o jaque, son responsabilidad de MoveValidator.
 */
public class RookMovement implements IMovementStrategy {

    /**
     * Determina si el movimiento propuesto es válido para una torre.
     *
     * @param board tablero actual
     * @param from posición de origen
     * @param to posición de destino
     * @return true si el movimiento cumple las reglas de la torre
     */
    @Override
    public boolean canMove(Board board, Position from, Position to) {

        // Quedarse en la misma casilla no constituye un movimiento.
        if (from.equals(to)) {
            return false;
        }

        // Una torre solamente puede desplazarse por la misma fila o columna.
        if (!isStraightMovement(from, to)) {
            return false;
        }

        // Si el movimiento tiene la forma correcta, verificamos el recorrido.
        return isPathClear(board, from, to);
    }

    /**
     * Comprueba que origen y destino estén alineados
     * horizontal o verticalmente.
     */
    private boolean isStraightMovement(Position from, Position to) {
        boolean sameRow = from.row() == to.row();
        boolean sameColumn = from.column() == to.column();

        return sameRow || sameColumn;
    }

    /**
     * Recorre las casillas intermedias entre origen y destino
     * y comprueba que ninguna esté ocupada.
     *
     * La casilla destino no se verifica acá, porque puede contener
     * una pieza rival. Determinar si la pieza del destino puede
     * capturarse pertenece a MoveValidator.
     */
    private boolean isPathClear(Board board, Position from, Position to) {

        // Dirección de avance en cada eje: -1, 0 o 1.
        int rowDirection = Integer.compare(to.row(), from.row());
        int columnDirection = Integer.compare(to.column(), from.column());

        // Comenzamos en la primera casilla posterior al origen.
        int currentRow = from.row() + rowDirection;
        int currentColumn = from.column() + columnDirection;

        // Revisamos únicamente las casillas intermedias.
        while (currentRow != to.row() || currentColumn != to.column()) {

            Position currentPosition =
                    new Position(currentRow, currentColumn);

            if (!board.isEmpty(currentPosition)) {
                return false;
            }

            currentRow += rowDirection;
            currentColumn += columnDirection;
        }

        return true;
    }
}