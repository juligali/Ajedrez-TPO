package ar.edu.ajedrez;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;
import ar.edu.ajedrez.core.board.*;

class BoardPathTest {
    @Test void invalidRayReturnsFalseInsteadOfLooping() {
        Board board = new Board();
        assertTimeoutPreemptively(Duration.ofSeconds(1), () ->
                assertFalse(board.pathClear(Position.from("a1"), Position.from("b3"))));
    }

    @Test void pathRejectsMissingOutOfBoundsAndIdenticalEndpoints() {
        Board board = new Board();
        Position origin = Position.from("a1");
        assertFalse(board.pathClear(null, origin));
        assertFalse(board.pathClear(origin, null));
        assertFalse(board.pathClear(origin, new Position(8, 0)));
        assertFalse(board.pathClear(origin, origin));
        assertTrue(board.pathClear(origin, Position.from("h8")));
        assertTrue(board.pathClear(origin, Position.from("a8")));
    }
}
