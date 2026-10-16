package ar.edu.ajedrez.app;

import ar.edu.ajedrez.core.game.MoveResult;
import ar.edu.ajedrez.core.validation.ValidationResult;

/** Traducción compartida por los adaptadores web y consola. */
public final class MoveMessages {
    private MoveMessages() { }
    public static String message(MoveResult result) {
        return result.getValidationResult().map(MoveMessages::validationMessage).orElseGet(result::getMessage);
    }
    public static String validationMessage(ValidationResult result) {
        return switch (result) {
            case VALID -> "Movimiento válido.";
            case OUTSIDE_BOARD -> "Casilla fuera del tablero.";
            case EMPTY_ORIGIN -> "No hay una pieza en esa casilla.";
            case WRONG_TURN -> "No es el turno de esa pieza.";
            case SAME_SQUARE -> "Elegí una casilla de destino diferente.";
            case FRIENDLY_DESTINATION -> "Esa casilla tiene una pieza de tu equipo.";
            case KING_CAPTURE -> "El rey no se captura: se gana con jaque mate.";
            case ILLEGAL_MOVEMENT -> "Esa pieza no puede moverse allí.";
            case INVALID_PROMOTION -> "Elegí reina, torre, alfil o caballo para promocionar.";
            case KING_EXPOSED -> "Ese movimiento deja a tu rey en jaque.";
        };
    }
}
