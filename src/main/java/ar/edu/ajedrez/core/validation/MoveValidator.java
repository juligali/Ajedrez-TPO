package ar.edu.ajedrez.core.validation;

import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.pieces.*;
import ar.edu.ajedrez.core.game.*;

public class MoveValidator {
    private final CheckDetector check;
    private final PieceFactory factory;
    public MoveValidator(CheckDetector check, PieceFactory factory) { this.check = check; this.factory = factory; }
    /** Resultado semántico, sin texto de presentación ni mutación del tablero. */
    public ValidationResult validate(Board board, Move move, PieceColor turn) {
        if (move == null || !board.contains(move.getFrom()) || !board.contains(move.getTo())) return ValidationResult.OUTSIDE_BOARD;
        Position f = move.getFrom(), t = move.getTo();
        Piece piece = board.get(f), target = board.get(t);
        if (piece == null) return ValidationResult.EMPTY_ORIGIN;
        if (piece.getColor() != turn) return ValidationResult.WRONG_TURN;
        if (f.equals(t)) return ValidationResult.SAME_SQUARE;
        if (target != null && target.getColor() == turn) return ValidationResult.FRIENDLY_DESTINATION;
        if (target != null && target.getType() == PieceType.KING) return ValidationResult.KING_CAPTURE;
        boolean castle = piece.getType() == PieceType.KING && Math.abs(t.getCol() - f.getCol()) == 2;
        if (castle ? !canCastle(board, f, t, piece) : !piece.canMove(board, f, t)) return ValidationResult.ILLEGAL_MOVEMENT;
        if (piece.getType() == PieceType.PAWN && (t.getRow() == 0 || t.getRow() == 7)) {
            PieceType promotion = move.getPromotion();
            if (promotion != PieceType.QUEEN && promotion != PieceType.ROOK && promotion != PieceType.BISHOP && promotion != PieceType.KNIGHT)
                return ValidationResult.INVALID_PROMOTION;
        }
        Board simulation = board.copy();
        new MoveCommand(simulation, move, factory).execute();
        if (check.isInCheck(simulation, turn)) return ValidationResult.KING_EXPOSED;
        return ValidationResult.VALID;
    }
    private boolean canCastle(Board b, Position f, Position t, Piece king) {
        if (king.hasMoved() || f.getRow() != king.getColor().homeRow() || f.getCol() != 4 || t.getRow() != f.getRow()
                || (t.getCol() != 2 && t.getCol() != 6)) return false;
        Position rookAt = new Position(f.getRow(), t.getCol() == 6 ? 7 : 0);
        Piece rook = b.get(rookAt);
        if (rook == null || rook.getType() != PieceType.ROOK || rook.getColor() != king.getColor() || rook.hasMoved()
                || !b.pathClear(f, rookAt) || check.isInCheck(b, king.getColor())) return false;
        // Al atravesar la casilla intermedia, el rey ya no bloquea su casilla original.
        Board intermediate = b.copy();
        Position step = new Position(f.getRow(), t.getCol() == 6 ? 5 : 3);
        intermediate.set(f, null); intermediate.set(step, king);
        return !check.isInCheck(intermediate, king.getColor());
    }
}
