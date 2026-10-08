package ar.edu.ajedrez.core.movement;
import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;
import ar.edu.ajedrez.core.pieces.Piece;
import ar.edu.ajedrez.core.pieces.PieceColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class PawnMovementTest {
    private Board board(PawnMovement movement, PieceColor color, Position from) {
        Board board = new Board(8,8);
        board.place(new Piece("Pawn", color, movement, false), from);
        return board;
    }
    private void place(Board board, Position at, PieceColor color) {
        board.place(new Piece("Obstacle", color, new KnightMovement(), false), at);
    }
    @ParameterizedTest
    @CsvSource({"1,1,WHITE","-1,6,BLACK"})
    void advancesOneOrTwoSquaresFromInitialRow(int direction, int startRow, PieceColor color) {
        PawnMovement movement = new PawnMovement(direction, startRow);
        Position from = new Position(startRow,3);
        Board board = board(movement,color,from);
        Board before = board.copy();
        assertTrue(movement.canMove(board,from,new Position(startRow+direction,3)));
        assertTrue(movement.canMove(board,from,new Position(startRow+2*direction,3)));
        assertEquals(before,board);
    }
    @ParameterizedTest
    @CsvSource({"1,1,WHITE","-1,6,BLACK"})
    void rejectsDoubleAdvanceAwayFromInitialRow(int direction, int startRow, PieceColor color) {
        PawnMovement movement = new PawnMovement(direction,startRow);
        Position from = new Position(startRow+direction,3);
        Board board = board(movement,color,from);
        assertTrue(movement.canMove(board,from,new Position(from.row()+direction,3)));
        assertFalse(movement.canMove(board,from,new Position(from.row()+2*direction,3)));
    }
    @ParameterizedTest
    @CsvSource({"1,1,WHITE","-1,6,BLACK"})
    void cannotAdvanceThroughAnOccupiedSquare(int direction, int startRow, PieceColor color) {
        PawnMovement movement = new PawnMovement(direction,startRow);
        Position from = new Position(startRow,3);
        Board board = board(movement,color,from);
        place(board,new Position(startRow+direction,3),PieceColor.BLACK);
        assertFalse(movement.canMove(board,from,new Position(startRow+direction,3)));
        assertFalse(movement.canMove(board,from,new Position(startRow+2*direction,3)));
    }
    @ParameterizedTest
    @CsvSource({"1,1,WHITE","-1,6,BLACK"})
    void cannotDoubleAdvanceIntoAnOccupiedDestination(int direction, int startRow, PieceColor color) {
        PawnMovement movement = new PawnMovement(direction,startRow);
        Position from = new Position(startRow,3);
        Board board = board(movement,color,from);
        place(board,new Position(startRow+2*direction,3),PieceColor.WHITE);
        assertFalse(movement.canMove(board,from,new Position(startRow+2*direction,3)));
    }
    @ParameterizedTest
    @CsvSource({"1,1,WHITE,BLACK,-1","1,1,WHITE,BLACK,1","-1,6,BLACK,WHITE,-1","-1,6,BLACK,WHITE,1"})
    void capturesOnlyAnEnemyOnAForwardDiagonal(int direction,int startRow,PieceColor color,PieceColor enemy,int side) {
        PawnMovement movement = new PawnMovement(direction,startRow);
        Position from = new Position(startRow,3);
        Position target = new Position(startRow+direction,3+side);
        Board board = board(movement,color,from);
        assertFalse(movement.canMove(board,from,target));
        place(board,target,color);
        assertFalse(movement.canMove(board,from,target));
        place(board,target,enemy);
        Board before = board.copy();
        assertTrue(movement.canMove(board,from,target));
        assertEquals(before,board);
    }
    @ParameterizedTest
    @CsvSource({"1,1,WHITE","-1,6,BLACK"})
    void attacksForwardDiagonalsRegardlessOfOccupancy(int direction,int startRow,PieceColor color) {
        PawnMovement movement = new PawnMovement(direction,startRow);
        Position from = new Position(startRow,3);
        Board board = board(movement,color,from);
        for (int side : new int[]{-1,1}) {
            Position target = new Position(startRow+direction,3+side);
            assertTrue(movement.canAttack(board,from,target));
            place(board,target,color);
            assertTrue(movement.canAttack(board,from,target));
        }
        assertFalse(movement.canAttack(board,from,new Position(startRow+direction,3)));
        assertFalse(movement.canAttack(board,from,new Position(startRow-direction,4)));
        assertFalse(movement.canAttack(board,from,new Position(startRow+2*direction,4)));
    }
    @ParameterizedTest
    @CsvSource({"0,0","-1,0","0,1","1,2","2,1","3,0","-1,1"})
    void rejectsOtherMovementPatterns(int rowOffset,int columnOffset) {
        PawnMovement movement = new PawnMovement(1,1);
        Position from = new Position(3,3);
        Board board = board(movement,PieceColor.WHITE,from);
        Position to = new Position(from.row()+rowOffset,from.column()+columnOffset);
        if (!to.equals(from)) place(board,to,PieceColor.BLACK);
        assertFalse(movement.canMove(board,from,to));
    }
    @Test void validatesConstructorConfiguration() {
        assertThrows(IllegalArgumentException.class,()->new PawnMovement(0,1));
        assertThrows(IllegalArgumentException.class,()->new PawnMovement(2,1));
        assertThrows(IllegalArgumentException.class,()->new PawnMovement(1,-1));
    }
}

