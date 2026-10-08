package ar.edu.ajedrez.core.validation;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;
import ar.edu.ajedrez.core.movement.*;
import ar.edu.ajedrez.core.pieces.Piece;
import ar.edu.ajedrez.core.pieces.PieceColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.*;

class CheckDetectorTest {
    private final CheckDetector detector = new CheckDetector();
    private final Position kingPosition = new Position(3, 3);

    private PieceColor opposite(PieceColor color) {
        return color == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;
    }
    private Board boardWithKing(PieceColor color) {
        Board board = new Board(8,8);
        // El nombre no identifica al rey: lo hace isKing().
        board.place(new Piece("Monarca",color,new KingMovement(),true),kingPosition);
        return board;
    }
    private IMovementStrategy movement(String type, PieceColor color) {
        return switch(type) {
            case "ROOK" -> new RookMovement();
            case "BISHOP" -> new BishopMovement();
            case "QUEEN" -> new QueenMovement(new RookMovement(),new BishopMovement());
            case "KNIGHT" -> new KnightMovement();
            case "KING" -> new KingMovement();
            case "PAWN" -> new PawnMovement(color == PieceColor.WHITE ? 1 : -1,
                                            color == PieceColor.WHITE ? 1 : 6);
            default -> throw new IllegalArgumentException(type);
        };
    }
    private void place(Board board,String type,PieceColor color,Position at) {
        board.place(new Piece(type,color,movement(type,color),type.equals("KING")),at);
    }

    @ParameterizedTest
    @CsvSource({
        "WHITE,ROOK,3,7","BLACK,ROOK,0,3",
        "WHITE,BISHOP,6,6","BLACK,BISHOP,0,6",
        "WHITE,QUEEN,3,0","BLACK,QUEEN,7,7",
        "WHITE,KNIGHT,5,4","BLACK,KNIGHT,1,2",
        "WHITE,KING,4,4","BLACK,KING,2,2",
        "WHITE,PAWN,4,2","BLACK,PAWN,2,4"
    })
    void detectsAttacksByEveryPieceForBothColors(PieceColor color,String type,int row,int column) {
        Board board = boardWithKing(color);
        place(board,type,opposite(color),new Position(row,column));
        Board before = board.copy();
        assertTrue(detector.isInCheck(board,color));
        assertEquals(before,board);
    }

    @ParameterizedTest
    @CsvSource({
        "ROOK,3,7,3,5,WHITE","ROOK,3,7,3,5,BLACK",
        "BISHOP,6,6,4,4,WHITE","BISHOP,6,6,4,4,BLACK",
        "QUEEN,0,6,2,4,WHITE","QUEEN,0,6,2,4,BLACK"
    })
    void anyInterveningPieceBlocksSlidingAttack(String type,int row,int column,
                                                int blockRow,int blockColumn,PieceColor blocker) {
        Board board = boardWithKing(PieceColor.WHITE);
        place(board,type,PieceColor.BLACK,new Position(row,column));
        place(board,"KNIGHT",blocker,new Position(blockRow,blockColumn));
        assertFalse(detector.isInCheck(board,PieceColor.WHITE));
    }

    @ParameterizedTest
    @EnumSource(PieceColor.class)
    void kingWithoutAttackersIsSafe(PieceColor color) {
        assertFalse(detector.isInCheck(boardWithKing(color),color));
    }

    @ParameterizedTest
    @CsvSource({"ROOK,3,7","BISHOP,6,6","QUEEN,0,3","KNIGHT,5,4","PAWN,2,2"})
    void ownPiecesDoNotGiveCheck(String type,int row,int column) {
        Board board = boardWithKing(PieceColor.WHITE);
        place(board,type,PieceColor.WHITE,new Position(row,column));
        assertFalse(detector.isInCheck(board,PieceColor.WHITE));
    }

    @ParameterizedTest
    @CsvSource({"WHITE,4,3","WHITE,2,2","BLACK,2,3","BLACK,4,4"})
    void pawnForwardMovementAndBackwardDiagonalAreNotAttacks(PieceColor color,int row,int column) {
        Board board = boardWithKing(color);
        place(board,"PAWN",opposite(color),new Position(row,column));
        assertFalse(detector.isInCheck(board,color));
    }

    @Test void knightAttackIsNotBlockedByOtherPieces() {
        Board board = boardWithKing(PieceColor.WHITE);
        place(board,"KNIGHT",PieceColor.BLACK,new Position(5,4));
        place(board,"PAWN",PieceColor.WHITE,new Position(4,4));
        assertTrue(detector.isInCheck(board,PieceColor.WHITE));
    }

    @Test void detectsAttackerAfterAnUnthreateningEnemy() {
        Board board = boardWithKing(PieceColor.WHITE);
        place(board,"ROOK",PieceColor.BLACK,new Position(0,0));
        place(board,"KNIGHT",PieceColor.BLACK,new Position(5,4));
        assertTrue(detector.isInCheck(board,PieceColor.WHITE));
    }

    @Test void checksOnlyTheRequestedKing() {
        Board board = boardWithKing(PieceColor.WHITE);
        place(board,"KING",PieceColor.BLACK,new Position(7,7));
        place(board,"ROOK",PieceColor.BLACK,new Position(3,7));
        assertTrue(detector.isInCheck(board,PieceColor.WHITE));
        assertFalse(detector.isInCheck(board,PieceColor.BLACK));
    }

    @Test void pinnedEnemyStillThreatensItsAttackSquares() {
        Board board = new Board(8,8);
        place(board,"KING",PieceColor.WHITE,new Position(4,3));
        place(board,"KING",PieceColor.BLACK,new Position(7,4));
        place(board,"KNIGHT",PieceColor.BLACK,new Position(6,4));
        place(board,"ROOK",PieceColor.WHITE,new Position(0,4));
        assertTrue(detector.isInCheck(board,PieceColor.WHITE));
    }

    @Test void missingKingIsAnInvalidPosition() {
        assertThrows(IllegalStateException.class,
                () -> detector.isInCheck(new Board(8,8),PieceColor.WHITE));
    }

    @Test void multipleKingsOfRequestedColorAreAnInvalidPosition() {
        Board board = boardWithKing(PieceColor.WHITE);
        place(board,"KING",PieceColor.WHITE,new Position(0,0));
        assertThrows(IllegalStateException.class,
                () -> detector.isInCheck(board,PieceColor.WHITE));
    }

    @Test void worksOnOtherBoardDimensions() {
        Board board = new Board(10,10);
        place(board,"KING",PieceColor.WHITE,new Position(9,9));
        place(board,"ROOK",PieceColor.BLACK,new Position(9,0));
        assertTrue(detector.isInCheck(board,PieceColor.WHITE));
    }
}

