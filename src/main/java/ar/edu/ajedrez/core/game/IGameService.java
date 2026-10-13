package ar.edu.ajedrez.core.game;

import java.util.List;
import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.pieces.*;

public interface IGameService {
    MoveResult move(Move move);
    MoveResult undo();
    MoveResult redo();
    Board getBoard();
    PieceColor getTurn();
    List<Position> legalMoves(Position from);
    List<MoveRecord> getHistory();
    String getStatus();
    boolean isInCheck();
    boolean isFinished();
    boolean canRedo();
    int getCapturedPoints(PieceColor player);
    PieceColor getWinner();
    List<Move> getTimeline();
}


