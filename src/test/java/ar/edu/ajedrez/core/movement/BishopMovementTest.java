package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;
import ar.edu.ajedrez.core.pieces.Piece;
import ar.edu.ajedrez.core.pieces.PieceColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitarios de la estrategia de movimiento del alfil.
 *
 * Responsabilidad probada:
 * determinar si un alfil puede desplazarse entre dos posiciones
 * respetando su movimiento diagonal y los obstáculos del tablero.
 *
 * No se prueban acá reglas generales de la partida como:
 * - turnos;
 * - captura de piezas propias;
 * - jaque.
 *
 * Esas reglas corresponden a MoveValidator.
 */
class BishopMovementTest {

    private final BishopMovement movement = new BishopMovement();

    /**
     * Crea un tablero vacío de 8x8 y coloca un alfil blanco
     * en la posición indicada.
     *
     * Se utiliza para evitar repetir el mismo código de preparación
     * en cada test.
     */
    private Board boardWithBishopAt(Position position) {
        Board board = new Board(8, 8);

        Piece bishop = new Piece(
                "Bishop",
                PieceColor.WHITE,
                movement,
                false
        );

        board.place(bishop, position);

        return board;
    }

    /**
     * Un alfil debe poder moverse diagonalmente
     * cuando el recorrido está libre.
     *
     * Esperado:
     * canMove(...) devuelve true.
     */
    @Test
    void shouldMoveDiagonallyWhenPathIsClear() {

        // Arrange
        Position from = new Position(3, 3);
        Position to = new Position(6, 6);

        Board board = boardWithBishopAt(from);

        // Act
        boolean result = movement.canMove(board, from, to);

        // Assert
        assertTrue(result);
    }

    /**
     * Comprueba que el alfil pueda desplazarse también
     * en la dirección diagonal opuesta.
     *
     * Esto verifica que la implementación no dependa
     * únicamente de coordenadas crecientes.
     *
     * Esperado:
     * canMove(...) devuelve true.
     */
    @Test
    void shouldMoveDiagonallyInOppositeDirection() {

        // Arrange
        Position from = new Position(4, 4);
        Position to = new Position(1, 1);

        Board board = boardWithBishopAt(from);

        // Act
        boolean result = movement.canMove(board, from, to);

        // Assert
        assertTrue(result);
    }

    /**
     * Un alfil no puede desplazarse horizontalmente.
     *
     * Esperado:
     * canMove(...) devuelve false.
     */
    @Test
    void shouldRejectHorizontalMovement() {

        // Arrange
        Position from = new Position(3, 3);
        Position to = new Position(3, 7);

        Board board = boardWithBishopAt(from);

        // Act
        boolean result = movement.canMove(board, from, to);

        // Assert
        assertFalse(result);
    }

    /**
     * Un alfil no puede desplazarse verticalmente.
     *
     * Esperado:
     * canMove(...) devuelve false.
     */
    @Test
    void shouldRejectVerticalMovement() {

        // Arrange
        Position from = new Position(3, 3);
        Position to = new Position(7, 3);

        Board board = boardWithBishopAt(from);

        // Act
        boolean result = movement.canMove(board, from, to);

        // Assert
        assertFalse(result);
    }

    /**
     * Un alfil no puede atravesar otra pieza.
     *
     * No importa el color de la pieza intermedia:
     * cualquier pieza bloquea el recorrido.
     *
     * Esperado:
     * canMove(...) devuelve false.
     */
    @Test
    void shouldRejectMovementWhenPathIsBlocked() {

        // Arrange
        Position from = new Position(2, 2);
        Position obstaclePosition = new Position(4, 4);
        Position to = new Position(6, 6);

        Board board = boardWithBishopAt(from);

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
     * Permanecer en la misma casilla no constituye
     * un movimiento válido.
     *
     * Esperado:
     * canMove(...) devuelve false.
     */
    @Test
    void shouldRejectMovementToSamePosition() {

        // Arrange
        Position position = new Position(3, 3);

        Board board = boardWithBishopAt(position);

        // Act
        boolean result = movement.canMove(board, position, position);

        // Assert
        assertFalse(result);
    }
}