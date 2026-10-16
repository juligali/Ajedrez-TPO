package ar.edu.ajedrez.core.game;

import java.util.Optional;
import ar.edu.ajedrez.core.validation.ValidationResult;

public final class MoveResult {
    private final boolean accepted;
    private final String message;
    private final ValidationResult validation;
    public MoveResult(boolean accepted, String message) { this.accepted = accepted; this.message = message; this.validation = null; }
    public MoveResult(ValidationResult validation) {
        if (validation == null || validation.isValid()) throw new IllegalArgumentException("Expected rejection.");
        this.accepted = false;
        this.message = "";
        this.validation = validation;
    }
    public Optional<ValidationResult> getValidationResult() { return Optional.ofNullable(validation); }
    public boolean isAccepted() { return accepted; }
    public String getMessage() { return message; }
}
