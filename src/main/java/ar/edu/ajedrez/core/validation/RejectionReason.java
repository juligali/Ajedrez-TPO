package ar.edu.ajedrez.core.validation;

/**
 * Motivos por los que MoveValidator puede rechazar una jugada.
 * Hay uno por cada regla que verifica, en el mismo orden en que se evalúan:
 * se informa el primero que falla.
 */
public enum RejectionReason {
    OUT_OF_BOUNDS,
    NO_PIECE_AT_ORIGIN,
    NOT_PLAYERS_TURN,
    SAME_SQUARE,
    OWN_PIECE_AT_DESTINATION,
    ILLEGAL_MOVEMENT,
    KING_CAPTURE,
    LEAVES_KING_IN_CHECK
}
