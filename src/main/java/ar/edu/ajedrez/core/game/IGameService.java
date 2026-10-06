package ar.edu.ajedrez.core.game;

import java.util.Objects;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.pieces.PieceColor;

/**
 * Operaciones que el núcleo ofrece a la interfaz de usuario (puerto).
 * Los adaptadores dependen de este contrato y no de los detalles de la partida.
 */
public interface IGameService {
    /** Solicita una jugada. Si se rechaza, el tablero y el turno no cambian. */
    MoveResult move(Move move);

    PieceColor currentTurn();

    /** Copia del tablero actual: modificarla no afecta a la partida. */
    Board snapshot();
}
