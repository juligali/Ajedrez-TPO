package ar.edu.ajedrez.core.movement;
import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;
import ar.edu.ajedrez.core.pieces.Piece;
import ar.edu.ajedrez.core.pieces.PieceColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class KnightMovementTest {
    private final KnightMovement movement = new KnightMovement();
    private final Position from = new Position(3, 3);
    private Board board() {
        Board board = new Board(8, 8);
        board.place(new Piece("Knight", PieceColor.WHITE, movement, false), from);
        return board;
    }
    @ParameterizedTest
    @CsvSource({"1,2","1,4","2,1","2,5","4,1","4,5","5,2","5,4"})
    void acceptsAllEightJumps(int row, int column) {
        assertTrue(movement.canMove(board(), from, new Position(row, column)));
    }
    @ParameterizedTest
    @CsvSource({"3,3","3,4","4,4","3,6","6,6","5,5"})
    void rejectsOtherMovements(int row, int column) {
        assertFalse(movement.canMove(board(), from, new Position(row, column)));
    }
    @Test void jumpsOverPiecesWithoutChangingBoard() {
        Board board = board();
        board.place(new Piece("Obstacle", PieceColor.BLACK, movement, false), new Position(4,3));
        board.place(new Piece("Obstacle", PieceColor.WHITE, movement, false), new Position(4,4));
        Board before = board.copy();
        assertTrue(movement.canMove(board, from, new Position(5,4)));
        assertEquals(before, board);
    }
    @Test void attacksAnOccupiedDestination() {
        Board board = board();
        Position target = new Position(5,4);
        board.place(new Piece("Enemy", PieceColor.BLACK, movement, false), target);
        assertTrue(movement.canAttack(board, from, target));
    }
}

