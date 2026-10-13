package ar.edu.ajedrez.core.ai;

import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.game.Move;
import ar.edu.ajedrez.core.pieces.PieceColor;

/** Puerto de salida: elegir una jugada sin modificar el tablero recibido. */
public interface IChessAi {
    Move chooseMove(Board board, PieceColor color, Difficulty difficulty);
}
