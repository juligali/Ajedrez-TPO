package ar.edu.ajedrez.core.game;

import java.util.Objects;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.pieces.PieceColor;
import ar.edu.ajedrez.core.validation.CheckDetector;
import ar.edu.ajedrez.core.validation.MoveValidator;

/**
 * Coordina la partida: mantiene el tablero y el turno, valida y aplica las jugadas.
 * Sus colaboradores entran por constructor. Empieza el jugador de piezas blancas.
 */
public class Game implements IGameService {
    private final Board board;
    private PieceColor turn = PieceColor.WHITE;
    private final MoveValidator validator;
    private final CheckDetector checkDetector;

    public Game(Board board, MoveValidator validator, CheckDetector checkDetector) {
        this.board = Objects.requireNonNull(board, "board").copy();
        this.validator = Objects.requireNonNull(validator, "validator");
        this.checkDetector = Objects.requireNonNull(checkDetector, "checkDetector");
    }

    @Override
    public MoveResult move(Move move) {
        throw new UnsupportedOperationException("Pendiente: se implementa con TDD");
    }

    @Override
    public PieceColor currentTurn() {
        return turn;
    }

    @Override
    public Board snapshot() {
        return board.copy();
    }
}
