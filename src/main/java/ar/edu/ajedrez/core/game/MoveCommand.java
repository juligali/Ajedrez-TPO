package ar.edu.ajedrez.core.game;

import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.pieces.*;

/** Ejecuta una jugada ya validada y conserva lo necesario para deshacerla. */
public class MoveCommand implements ICommand {
    private final Board board;
    private final Board before;
    private final Move move;
    private final PieceFactory factory;
    private final Piece piece;
    private Piece captured;
    public MoveCommand(Board board, Move move, PieceFactory factory) {
        this.board = board; this.before = board.copy(); this.move = move; this.factory = factory;
        this.piece = board.get(move.getFrom());
    }
    @Override
    public void execute() {
        Position f = move.getFrom(), t = move.getTo();
        captured = board.get(t);
        if (piece.getType() == PieceType.PAWN && f.getCol() != t.getCol() && captured == null) {
            Position victim = new Position(f.getRow(), t.getCol());
            captured = board.get(victim);
            board.set(victim, null);
        }
        board.set(f, null);
        board.set(t, piece.movedCopy());
        if (piece.getType() == PieceType.KING && Math.abs(t.getCol() - f.getCol()) == 2) {
            Position rookFrom = new Position(f.getRow(), t.getCol() == 6 ? 7 : 0);
            Position rookTo = new Position(f.getRow(), t.getCol() == 6 ? 5 : 3);
            board.set(rookTo, board.get(rookFrom).movedCopy());
            board.set(rookFrom, null);
        }
        if (piece.getType() == PieceType.PAWN && (t.getRow() == 0 || t.getRow() == 7))
            board.set(t, factory.create(move.getPromotion(), piece.getColor()).movedCopy());
        board.setEnPassant(null);
        if (piece.getType() == PieceType.PAWN && Math.abs(t.getRow() - f.getRow()) == 2)
            board.setEnPassant(new Position((f.getRow() + t.getRow()) / 2, f.getCol()));
        board.setHalfMoveClock(piece.getType() == PieceType.PAWN || captured != null ? 0 : board.getHalfMoveClock() + 1);
    }
    @Override
    public void undo() { board.restore(before); }
    public Move getMove() { return move; }
    public Piece getCaptured() { return captured; }
    public String getLabel() {
        if (piece.getType() == PieceType.KING && Math.abs(move.getTo().getCol() - move.getFrom().getCol()) == 2)
            return move.getTo().getCol() == 6 ? "O-O" : "O-O-O";
        String suffix = piece.getType() == PieceType.PAWN && (move.getTo().getRow() == 0 || move.getTo().getRow() == 7)
                ? " = " + move.getPromotion().getLabel() : "";
        return move.getFrom() + (captured == null ? " → " : " × ") + move.getTo() + suffix;
    }
}
