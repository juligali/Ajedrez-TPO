package ar.edu.ajedrez.app;

import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.pieces.*;
import ar.edu.ajedrez.core.game.*;
import ar.edu.ajedrez.core.validation.*;

/** Punto de composición: conecta explícitamente los colaboradores por constructor. */
public class StandardGame {
    public static Game create() {
        PieceFactory factory = new PieceFactory();
        Board board = new Board();
        PieceType[] order = {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};
        for (PieceColor color : PieceColor.values()) {
            for (int c = 0; c < 8; c++) {
                board.set(new Position(color.homeRow(), c), factory.create(order[c], color));
                board.set(new Position(color == PieceColor.WHITE ? 6 : 1, c), factory.create(PieceType.PAWN, color));
            }
        }
        CheckDetector check = new CheckDetector();
        return new Game(board, PieceColor.WHITE, new MoveValidator(check, factory), check, factory);
    }
}
