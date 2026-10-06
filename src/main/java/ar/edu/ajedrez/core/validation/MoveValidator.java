package ar.edu.ajedrez.core.validation;

import java.util.Objects;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.game.Move;
import ar.edu.ajedrez.core.pieces.PieceColor;

public class MoveValidator {
    private final CheckDetector checkDetector;

    public MoveValidator(CheckDetector checkDetector) {
        this.checkDetector = Objects.requireNonNull(checkDetector, "checkDetector");
    }

    public ValidationResult validate(Board board, Move move, PieceColor turn) {
        throw new UnsupportedOperationException("Pendiente: se implementa con TDD");
    }
}
