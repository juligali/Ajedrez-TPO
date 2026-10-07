package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;
import ar.edu.ajedrez.core.pieces.Piece;
import ar.edu.ajedrez.core.pieces.PieceColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitarios de la estrategia de movimiento de la torre.
 *
 * Se prueba únicamente la responsabilidad de RookMovement:
 * determinar si una torre puede desplazarse entre dos posiciones
 * según su patrón de movimiento y los obstáculos del tablero.
 *
 * No se validan aquí reglas generales de la partida como:
 * - turno del jugador
 * - captura de una pieza propia
 * - dejar al propio rey en jaque
 *
 * Esas validaciones corresponden a MoveValidator.
 */
class RookMovementTest {

    private final RookMovement movement = new RookMovement();

    /**
     * Crea un tablero vacío de 8x8 y coloca una torre blanca
     * en la posición indicada.
     *
     * Este método evita repetir el mismo código de preparación
     * en todos los tests.
     */
    private Board boardWithRookAt(Position position) {
        Board board = new Board(8, 8);

        Piece rook = new Piece(
                "Rook",
                PieceColor.WHITE,
                movement,
                false
        );

        board.place(rook, position);

        return board;
    }

    /**
     * Una torre debe poder desplazarse horizontalmente
     * mientras no existan obstáculos en el camino.
     *
     * Esperado:
     * canMove(...) devuelve true.
     */
    @Test
    void shouldMoveHorizontallyWhenPathIsClear() {

        // Arrange: torre ubicada en el centro del tablero.
        Position from = new Position(3, 3);
        Position to = new Position(3, 7);

        Board board = boardWithRookAt(from);

        // Act: consultamos si el movimiento horizontal es válido.
        boolean result = movement.canMove(board, from, to);

        // Assert: una torre puede moverse horizontalmente.
        assertTrue(result);
    }

    /**
     * Una torre debe poder desplazarse verticalmente
     * mientras no existan obstáculos en el camino.
     *
     * Esperado:
     * canMove(...) devuelve true.
     */
    @Test
    void shouldMoveVerticallyWhenPathIsClear() {

        // Arrange
        Position from = new Position(3, 3);
        Position to = new Position(7, 3);

        Board board = boardWithRookAt(from);

        // Act
        boolean result = movement.canMove(board, from, to);

        // Assert
        assertTrue(result);
    }

    /**
     * Una torre no puede desplazarse diagonalmente.
     *
     * Esperado:
     * canMove(...) devuelve false.
     */
    @Test
    void shouldRejectDiagonalMovement() {

        // Arrange
        Position from = new Position(3, 3);
        Position to = new Position(5, 5);

        Board board = boardWithRookAt(from);

        // Act
        boolean result = movement.canMove(board, from, to);

        // Assert
        assertFalse(result);
    }

    /**
     * Una torre no puede atravesar otra pieza.
     *
     * El color de la pieza bloqueadora no importa para este test:
     * cualquier pieza intermedia bloquea el recorrido.
     *
     * Esperado:
     * canMove(...) devuelve false.
     */
    @Test
    void shouldRejectMovementWhenPathIsBlocked() {

        // Arrange
        Position from = new Position(3, 3);
        Position obstaclePosition = new Position(3, 5);
        Position to = new Position(3, 7);

        Board board = boardWithRookAt(from);

        Piece obstacle = new Piece(
                "Obstacle",
                PieceColor.BLACK,
                movement,
                false
        );

        board.place(obstacle, obstaclePosition);

        // Act
        boolean result = movement.canMove(board, from, to);

        // Assert
        assertFalse(result);
    }

    /**
     * Una torre no debería considerar válido un movimiento
     * donde origen y destino sean la misma casilla.
     *
     * Esperado:
     * canMove(...) devuelve false.
     */
    @Test
    void shouldRejectMovementToSamePosition() {

        // Arrange
        Position position = new Position(3, 3);
        Board board = boardWithRookAt(position);

        // Act
        boolean result = movement.canMove(board, position, position);

        // Assert
        assertFalse(result);
    }
}