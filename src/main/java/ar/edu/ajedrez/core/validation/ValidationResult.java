package ar.edu.ajedrez.core.validation;

import java.util.Objects;

/**
 * Resultado de validar una jugada: es válida, o fue rechazada por un motivo.
 * Es un valor inmutable; equals() generado por el record permite comparar
 * resultados completos en los tests.
 *
 * @param reason motivo del rechazo; null si la jugada es válida
 *               (se crea siempre mediante valid() o rejected(...))
 */
public record ValidationResult(RejectionReason reason) {
   private static final ValidationResult VALID = new ValidationResult(null);

    public static ValidationResult valid() {
        return VALID;
    }

    public static ValidationResult rejected(RejectionReason reason) {
        return new ValidationResult(Objects.requireNonNull(reason, "reason"));
    }

    public boolean isValid() {
        return reason == null;
    }
}
