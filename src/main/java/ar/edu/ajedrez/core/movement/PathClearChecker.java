package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;

/**
 * Utilidad interna del paquete de movimientos.
 *
 * Responsabilidad:
 * comprobar que todas las casillas intermedias entre dos posiciones
 * estén libres.
 *
 * Es utilizada por las piezas que se desplazan recorriendo varias
 * casillas, como la torre y el alfil.
 *
 * No valida:
 * - si la forma del movimiento es válida;
 * - qué pieza existe en el destino;
 * - turnos;
 * - capturas.
 *
 * Cada estrategia sigue siendo responsable de validar su propio
 * patrón de movimiento.
 */
final class PathClearChecker {

    /**
     * Constructor privado porque esta clase no representa un objeto
     * del dominio ni necesita mantener estado.
     */
    private PathClearChecker() {
    }

    /**
     * Comprueba que no haya piezas entre origen y destino.
     *
     * Precondición:
     * la estrategia que llama a este método ya validó que el movimiento
     * sigue una dirección válida (horizontal, vertical o diagonal).
     *
     * @param board tablero actual
     * @param from posición de origen
     * @param to posición de destino
     * @return true si todas las casillas intermedias están libres
     */
    static boolean isPathClear(Board board, Position from, Position to) {

        // Determina la dirección de avance en cada eje: -1, 0 o 1.
        int rowDirection = Integer.compare(to.row(), from.row());
        int columnDirection = Integer.compare(to.column(), from.column());

        // Comenzamos en la primera casilla posterior al origen.
        int currentRow = from.row() + rowDirection;
        int currentColumn = from.column() + columnDirection;

        /*
         * Recorremos hasta llegar al destino.
         *
         * La casilla destino no se comprueba porque puede contener
         * una pieza rival. Esa decisión corresponde a MoveValidator.
         */
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