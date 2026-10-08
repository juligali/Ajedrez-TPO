package ar.edu.ajedrez.core.movement;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;
import ar.edu.ajedrez.core.pieces.Piece;
import ar.edu.ajedrez.core.pieces.PieceColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Reglas de movimiento y ataque de la reina con estrategias reales.
 * Turnos, destino con pieza propia y seguridad del rey corresponden a MoveValidator.
 */
class QueenMovementTest {
    private final QueenMovement movement =
            new QueenMovement(new RookMovement(), new BishopMovement());
    private final Position from = new Position(3, 3);

    private Board board() {
        Board board = new Board(8, 8);
        board.place(new Piece("Queen", PieceColor.WHITE, movement, false), from);
        return board;
    }

    private void place(Board board, Position position, PieceColor color) {
        board.place(new Piece("Obstacle", color, new KnightMovement(), false), position);
    }

    @ParameterizedTest
    @CsvSource({"0,3","7,3","3,0","3,7","0,0","0,6","6,0","7,7"})
    void movesAndAttacksInAllEightDirections(int row, int column) {
        Board board = board();
        Position target = new Position(row, column);
        Board before = board.copy();
        assertTrue(movement.canMove(board, from, target));
        assertTrue(movement.canAttack(board, from, target));
        assertEquals(before, board);
    }

    @ParameterizedTest
    @CsvSource({"2,3","4,3","3,2","3,4","2,2","2,4","4,2","4,4"})
    void movesOneSquareInAllEightDirections(int row, int column) {
        assertTrue(movement.canMove(board(), from, new Position(row, column)));
    }

    @ParameterizedTest
    @CsvSource({"3,3","5,4","4,5","1,2","2,1","7,4","4,7"})
    void rejectsStayingPutAndNonStraightNonDiagonalMoves(int row, int column) {
        Board board = board();
        Position target = new Position(row, column);
        assertFalse(movement.canMove(board, from, target));
        assertFalse(movement.canAttack(board, from, target));
    }

    @ParameterizedTest
    @CsvSource({
        "0,3,2,3,WHITE","7,3,4,3,BLACK",
        "3,0,3,2,BLACK","3,7,3,4,WHITE",
        "0,0,2,2,WHITE","0,6,2,4,BLACK",
        "6,0,4,2,BLACK","7,7,4,4,WHITE"
    })
    void cannotMoveOrAttackThroughPieces(int row, int column,
                                         int obstacleRow, int obstacleColumn,
                                         PieceColor color) {
        Board board = board();
        place(board, new Position(obstacleRow, obstacleColumn), color);
        Position target = new Position(row, column);
        Board before = board.copy();
        assertFalse(movement.canMove(board, from, target));
        assertFalse(movement.canAttack(board, from, target));
        assertEquals(before, board);
    }

    @ParameterizedTest
    @CsvSource({"0,3","7,3","3,0","3,7","0,0","0,6","6,0","7,7"})
    void canReachAnEnemyAtTheEndOfAClearPath(int row, int column) {
        Board board = board();
        Position target = new Position(row, column);
        place(board, target, PieceColor.BLACK);
        Board before = board.copy();
        assertTrue(movement.canMove(board, from, target));
        assertTrue(movement.canAttack(board, from, target));
        assertEquals(before, board);
    }

    @Test
    void piecesOutsideThePathDoNotBlockMovement() {
        Board board = board();
        place(board, new Position(4, 3), PieceColor.BLACK);
        place(board, new Position(3, 4), PieceColor.WHITE);
        assertTrue(movement.canMove(board, from, new Position(7, 7)));
        assertTrue(movement.canAttack(board, from, new Position(7, 7)));
    }
}

