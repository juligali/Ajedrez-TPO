package ar.edu.ajedrez;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ar.edu.ajedrez.app.StandardGame;
import ar.edu.ajedrez.app.MoveMessages;
import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.game.*;
import ar.edu.ajedrez.core.pieces.*;
import ar.edu.ajedrez.core.validation.*;
import static ar.edu.ajedrez.core.validation.ValidationResult.*;
import static ar.edu.ajedrez.core.pieces.PieceType.*;
import static ar.edu.ajedrez.core.pieces.PieceColor.*;

class ValidationTest {
    private final PieceFactory factory = new PieceFactory();
    private final MoveValidator validator = new MoveValidator(new CheckDetector(), factory);
    private Move move(String from, String to) { return new Move(Position.from(from), Position.from(to)); }

    @Test void basicRejectionsHaveSemanticReasons() {
        Board board = StandardGame.create().getBoard();
        assertEquals(OUTSIDE_BOARD, validator.validate(board, null, WHITE));
        assertEquals(OUTSIDE_BOARD, validator.validate(board, new Move(null, Position.from("a1")), WHITE));
        assertEquals(OUTSIDE_BOARD, validator.validate(board, new Move(Position.from("a1"), new Position(0, 8)), WHITE));
        assertEquals(EMPTY_ORIGIN, validator.validate(board, move("e4", "e5"), WHITE));
        assertEquals(WRONG_TURN, validator.validate(board, move("e7", "e5"), WHITE));
        assertEquals(SAME_SQUARE, validator.validate(board, move("e2", "e2"), WHITE));
        assertEquals(FRIENDLY_DESTINATION, validator.validate(board, move("e1", "e2"), WHITE));
        assertEquals(ILLEGAL_MOVEMENT, validator.validate(board, move("a1", "a3"), WHITE));
        assertEquals(VALID, validator.validate(board, move("e2", "e4"), WHITE));
        assertNotNull(board.get(Position.from("e2")));
        assertNull(board.get(Position.from("e4")));
    }

    @Test void kingSafetyCaptureAndPromotionHaveDistinctReasons() {
        Board board = new Board();
        board.set(Position.from("e1"), factory.create(KING, WHITE));
        board.set(Position.from("a8"), factory.create(KING, BLACK));
        board.set(Position.from("e2"), factory.create(ROOK, WHITE));
        board.set(Position.from("e8"), factory.create(ROOK, BLACK));
        assertEquals(KING_EXPOSED, validator.validate(board, move("e2", "d2"), WHITE));
        board.set(Position.from("a2"), factory.create(ROOK, WHITE));
        assertEquals(KING_CAPTURE, validator.validate(board, move("a2", "a8"), WHITE));
        board.set(Position.from("b7"), factory.create(PAWN, WHITE));
        assertEquals(INVALID_PROMOTION, validator.validate(board,
                new Move(Position.from("b7"), Position.from("b8"), KING), WHITE));
    }

    @Test void gamePropagatesReasonWithoutChangingHistoryTurnOrRedo() {
        Game game = StandardGame.create();
        game.move(move("e2", "e4"));
        game.undo();
        MoveResult result = game.move(move("e7", "e5"));
        assertFalse(result.isAccepted());
        assertEquals(WRONG_TURN, result.getValidationResult().orElseThrow());
        assertEquals(WHITE, game.getTurn());
        assertTrue(game.getHistory().isEmpty());
        assertTrue(game.canRedo());
        assertNotNull(game.getBoard().get(Position.from("e7")));
        assertTrue(result.getMessage().isEmpty(), "El núcleo no fabrica texto para este rechazo");
    }

    @Test void adaptersTranslateEveryReasonAndKeepExistingSuccessMessages() {
        for (ValidationResult reason : ValidationResult.values()) {
            assertFalse(MoveMessages.validationMessage(reason).isBlank());
            if (!reason.isValid()) assertEquals(MoveMessages.validationMessage(reason), MoveMessages.message(new MoveResult(reason)));
        }
        assertEquals("Movimiento deshecho.", MoveMessages.message(new MoveResult(true, "Movimiento deshecho.")));
        assertThrows(IllegalArgumentException.class, () -> new MoveResult(VALID));
    }
}
