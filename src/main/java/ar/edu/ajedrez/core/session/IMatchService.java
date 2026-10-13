package ar.edu.ajedrez.core.session;
import ar.edu.ajedrez.core.ai.Difficulty;
import ar.edu.ajedrez.core.game.*;

public interface IMatchService {
    IGameService getGame();
    MoveResult newGame(GameMode mode, Difficulty difficulty);
    MoveResult move(Move move);
    MoveResult playAi();
    MoveResult undo();
    MoveResult redo();
    MoveResult save();
    boolean isAiTurn();
    boolean isConfigured();
    GameMode getMode();
    Difficulty getDifficulty();
    long getElapsedMillis();
}
