package ar.edu.ajedrez.core.validation;

/** Motivos del dominio. Los adaptadores eligen el texto visible. */
public enum ValidationResult {
    VALID, OUTSIDE_BOARD, EMPTY_ORIGIN, WRONG_TURN, SAME_SQUARE,
    FRIENDLY_DESTINATION, KING_CAPTURE, ILLEGAL_MOVEMENT, INVALID_PROMOTION, KING_EXPOSED;

    public boolean isValid() { return this == VALID; }
}
