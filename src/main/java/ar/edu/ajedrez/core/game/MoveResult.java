package ar.edu.ajedrez.core.game;

import ar.edu.ajedrez.core.pieces.Piece;
import ar.edu.ajedrez.core.validation.RejectionReason;

/**
 * Respuesta de Game a una solicitud de movimiento.
 * Informa el motivo del rechazo como dato (RejectionReason), no como texto:
 * la presentación es responsabilidad del adaptador (ConsoleUI).
 */
public class MoveResult {
    private final boolean accepted;
    private final RejectionReason reason; // null si la jugada fue aceptada
    private final Piece captured;         // null si no hubo captura
    private final boolean check;

    private MoveResult(boolean accepted, RejectionReason reason, Piece captured, boolean check) {
        this.accepted = accepted;
        this.reason = reason;
        this.captured = captured;
        this.check = check;
    }

    public static MoveResult accepted(Piece captured, boolean check) {
        return new MoveResult(true, null, captured, check);
    }

    public static MoveResult rejected(RejectionReason reason) {
        return new MoveResult(false, reason, null, false);
    }

    public boolean isAccepted() {
        return accepted;
    }

    public RejectionReason reason() {
        return reason;
    }

    public Piece captured() {
        return captured;
    }

    public boolean isCheck() {
        return check;
    }    
}
