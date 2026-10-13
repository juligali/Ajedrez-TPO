package ar.edu.ajedrez.core.validation;

import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.pieces.*;
import ar.edu.ajedrez.core.game.*;

public class MoveValidator {
    private final CheckDetector check;
    private final PieceFactory factory;
    public MoveValidator(CheckDetector check, PieceFactory factory) { this.check = check; this.factory = factory; }
    /** Devuelve null si es legal, o el motivo del rechazo. No modifica el tablero. */
    public String validate(Board board, Move move, PieceColor turn) {
        if (move == null || !board.contains(move.getFrom()) || !board.contains(move.getTo())) return "Casilla fuera del tablero.";
        Position f = move.getFrom(), t = move.getTo();
        Piece piece = board.get(f), target = board.get(t);
        if (piece == null) return "No hay una pieza en esa casilla.";
        if (piece.getColor() != turn) return "Es el turno de las " + turn.label().toLowerCase() + ".";
        if (f.equals(t)) return "Elegí una casilla de destino diferente.";
        if (target != null && target.getColor() == turn) return "Esa casilla tiene una pieza de tu equipo.";
        if (target != null && target.getType() == PieceType.KING) return "El rey no se captura: se gana con jaque mate.";
        boolean castle = piece.getType() == PieceType.KING && Math.abs(t.getCol() - f.getCol()) == 2;
        if (castle ? !canCastle(board, f, t, piece) : !piece.canMove(board, f, t)) return "Esa pieza no puede moverse allí.";
        if (piece.getType() == PieceType.PAWN && (t.getRow() == 0 || t.getRow() == 7)) {
            PieceType promotion = move.getPromotion();
            if (promotion != PieceType.QUEEN && promotion != PieceType.ROOK && promotion != PieceType.BISHOP && promotion != PieceType.KNIGHT)
                return "Elegí reina, torre, alfil o caballo para promocionar.";
        }
        Board simulation = board.copy();
        new MoveCommand(simulation, move, factory).execute();
        if (check.isInCheck(simulation, turn)) return "Ese movimiento deja a tu rey en jaque.";
        return null;
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
