package ar.edu.ajedrez.core.game;

public final class MoveResult {
    private final boolean accepted;
    private final String message;
    public MoveResult(boolean accepted, String message) { this.accepted = accepted; this.message = message; }
    public boolean isAccepted() { return accepted; }
    public String getMessage() { return message; }
}
