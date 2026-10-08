package ar.edu.ajedrez.core.movement;
import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;
import ar.edu.ajedrez.core.pieces.Piece;
import ar.edu.ajedrez.core.pieces.PieceColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class KingMovementTest {
    private final KingMovement movement = new KingMovement();
    private final Position from = new Position(3, 3);
    private Board board() {
        Board board = new Board(8, 8);
        board.place(new Piece("King", PieceColor.WHITE, movement, true), from);
        return board;
    }
    @ParameterizedTest
    @CsvSource({"2,2","2,3","2,4","3,2","3,4","4,2","4,3","4,4"})
    void acceptsAllEightAdjacentSquares(int row, int column) {
        assertTrue(movement.canMove(board(), from, new Position(row, column)));
    }
    @ParameterizedTest
    @CsvSource({"3,3","1,3","5,3","3,1","3,5","1,1","5,5"})
    void rejectsStayingPutAndLongMovements(int row, int column) {
        assertFalse(movement.canMove(board(), from, new Position(row, column)));
    }
    @Test void attacksAdjacentEnemyWithoutChangingBoard() {
        Board board = board();
        Position target = new Position(4,4);
        board.place(new Piece("Enemy", PieceColor.BLACK, movement, false), target);
        Board before = board.copy();
        assertTrue(movement.canMove(board, from, target));
        assertTrue(movement.canAttack(board, from, target));
        assertEquals(before, board);
    }
}

