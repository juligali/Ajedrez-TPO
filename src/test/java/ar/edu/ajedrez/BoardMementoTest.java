package ar.edu.ajedrez;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;
import ar.edu.ajedrez.core.pieces.Piece;
import ar.edu.ajedrez.core.pieces.PieceColor;
import ar.edu.ajedrez.core.pieces.PieceFactory;
import ar.edu.ajedrez.core.pieces.PieceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardMementoTest {
    private final PieceFactory factory = new PieceFactory();

    @Test void restoresPiecesMovedFlagsAndRuleMetadata() {
        Board board = new Board();
        Piece rook = factory.create(PieceType.ROOK, PieceColor.WHITE);
        Piece pawn = factory.create(PieceType.PAWN, PieceColor.BLACK).movedCopy();
        board.set(Position.from("a1"), rook);
        board.set(Position.from("d5"), pawn);
        board.setEnPassant(Position.from("d6"));
        board.setHalfMoveClock(17);
        Board.Memento before = board.createMemento();

        board.set(Position.from("a1"), null);
        board.set(Position.from("a4"), rook.movedCopy());
        board.set(Position.from("d5"), null);
        board.setEnPassant(null);
        board.setHalfMoveClock(0);
        board.restoreMemento(before);

        assertSame(rook, board.get(Position.from("a1")));
        assertFalse(board.get(Position.from("a1")).hasMoved());
        assertSame(pawn, board.get(Position.from("d5")));
        assertTrue(board.get(Position.from("d5")).hasMoved());
        assertNull(board.get(Position.from("a4")));
        assertEquals(Position.from("d6"), board.getEnPassant());
        assertEquals(17, board.getHalfMoveClock());
    }

    @Test void independentSnapshotsCanBeRestoredRepeatedly() {
        Board board = new Board();
        Piece knight = factory.create(PieceType.KNIGHT, PieceColor.WHITE);
        board.set(Position.from("b1"), knight);
        Board.Memento first = board.createMemento();
        board.set(Position.from("b1"), null);
        board.set(Position.from("c3"), knight.movedCopy());
        board.setEnPassant(Position.from("e3"));
        board.setHalfMoveClock(1);
        Board.Memento second = board.createMemento();

        board.restoreMemento(first);
        board.set(Position.from("b1"), null);
        board.setEnPassant(Position.from("h6"));
        board.setHalfMoveClock(90);
        board.restoreMemento(second);
        assertNull(board.get(Position.from("b1")));
        assertTrue(board.get(Position.from("c3")).hasMoved());
        assertEquals(Position.from("e3"), board.getEnPassant());
        assertEquals(1, board.getHalfMoveClock());

        board.restoreMemento(first);
        assertSame(knight, board.get(Position.from("b1")));
        assertNull(board.get(Position.from("c3")));
        assertNull(board.getEnPassant());
        assertEquals(0, board.getHalfMoveClock());
    }

    @Test void nullSnapshotIsRejectedBeforeChangingTheBoard() {
        Board board = new Board();
        Piece king = factory.create(PieceType.KING, PieceColor.WHITE);
        board.set(Position.from("e1"), king);
        board.setEnPassant(Position.from("a3"));
        board.setHalfMoveClock(8);

        assertThrows(NullPointerException.class, () -> board.restoreMemento(null));
        assertSame(king, board.get(Position.from("e1")));
        assertEquals(Position.from("a3"), board.getEnPassant());
        assertEquals(8, board.getHalfMoveClock());
    }
}
