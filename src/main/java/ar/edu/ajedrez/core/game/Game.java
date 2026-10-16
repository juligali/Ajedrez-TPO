package ar.edu.ajedrez.core.game;

import java.util.*;
import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.pieces.*;
import ar.edu.ajedrez.core.validation.*;

public class Game implements IGameService {
    private final Board board;
    private final MoveValidator validator;
    private final CheckDetector check;
    private final PieceFactory factory;
    private PieceColor turn;
    private final List<MoveCommand> history = new ArrayList<>();
    private final Deque<MoveCommand> future = new ArrayDeque<>();
    private final Map<String, Integer> repetitions = new HashMap<>();
    private String status = "PLAYING";

    public Game(Board board, PieceColor turn, MoveValidator validator, CheckDetector check, PieceFactory factory) {
        this.board = board.copy();
        this.turn = turn; this.validator = validator; this.check = check; this.factory = factory;
        repetitions.put(positionKey(), 1);
        updateStatus();
    }
    @Override
    public MoveResult move(Move move) {
        if (isFinished()) return new MoveResult(false, "La partida terminó. Podés iniciar otra o deshacer.");
        ValidationResult validation = validator.validate(board, move, turn);
        if (!validation.isValid()) return new MoveResult(validation);
        MoveCommand command = new MoveCommand(board, move, factory);
        command.execute();
        history.add(command);
        future.clear();
        turn = turn.opposite();
        addRepetition();
        updateStatus();
        return new MoveResult(true, command.getLabel());
    }
    @Override
    public MoveResult undo() {
        if (history.isEmpty()) return new MoveResult(false, "Todavía no hay movimientos para deshacer.");
        String key = positionKey();
        repetitions.computeIfPresent(key, (k, count) -> count - 1);
        MoveCommand command = history.remove(history.size() - 1);
        command.undo();
        future.push(command);
        turn = turn.opposite();
        updateStatus();
        return new MoveResult(true, "Movimiento deshecho.");
    }
    @Override
    public MoveResult redo() {
        if (future.isEmpty()) return new MoveResult(false, "No hay movimientos para rehacer.");
        MoveCommand command = future.pop();
        command.execute();
        history.add(command);
        turn = turn.opposite();
        addRepetition();
        updateStatus();
        return new MoveResult(true, "Movimiento rehecho.");
    }
    @Override
    public Board getBoard() { return board.copy(); }
    @Override
    public PieceColor getTurn() { return turn; }
    @Override
    public List<MoveRecord> getHistory() {
        List<MoveRecord> records = new ArrayList<>();
        for (MoveCommand command : history)
            records.add(new MoveRecord(command.getMove(), command.getCaptured(), command.getLabel()));
        return Collections.unmodifiableList(records);
    }
    @Override
    public int getCapturedPoints(PieceColor player) {
        int total = 0;
        for (MoveCommand command : history) {
            Piece captured = command.getCaptured();
            if (captured != null && captured.getColor() != player && captured.getType().getPoints() != null)
                total += captured.getType().getPoints();
        }
        return total;
    }
    @Override
    public PieceColor getWinner() { return status.equals("CHECKMATE") ? turn.opposite() : null; }
    /** Jugadas realizadas y futuras, en orden; sólo datos, nunca comandos mutables. */
    @Override
    public List<Move> getTimeline() {
        List<Move> moves = new ArrayList<>();
        for (MoveCommand command : history) moves.add(command.getMove());
        for (MoveCommand command : future) moves.add(command.getMove());
        return Collections.unmodifiableList(moves);
    }
    @Override
    public boolean canRedo() { return !future.isEmpty(); }
    @Override
    public String getStatus() { return status; }
    @Override
    public boolean isInCheck() { return check.isInCheck(board, turn); }
    @Override
    public boolean isFinished() { return !status.equals("PLAYING") && !status.equals("CHECK"); }
    @Override
    public List<Position> legalMoves(Position from) {
        List<Position> result = new ArrayList<>();
        if (isFinished() || !board.contains(from)) return result;
        Piece p = board.get(from);
        if (p == null || p.getColor() != turn) return result;
        for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) {
            Position to = new Position(r, c);
            if (validator.validate(board, new Move(from, to), turn).isValid()) result.add(to);
        }
        return result;
    }
    private boolean hasLegalMove() {
        for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) {
            Position from = new Position(r, c);
            Piece p = board.get(from);
            if (p == null || p.getColor() != turn) continue;
            for (int tr = 0; tr < 8; tr++) for (int tc = 0; tc < 8; tc++)
                if (validator.validate(board, new Move(from, new Position(tr, tc)), turn).isValid()) return true;
        }
        return false;
    }
    private void updateStatus() {
        boolean checked = isInCheck();
        if (!hasLegalMove()) status = checked ? "CHECKMATE" : "STALEMATE";
        else if (insufficientMaterial()) status = "MATERIAL";
        else if (board.getHalfMoveClock() >= 100) status = "FIFTY_MOVES";
        else if (repetitions.getOrDefault(positionKey(), 0) >= 3) status = "REPETITION";
        else status = checked ? "CHECK" : "PLAYING";
    }
    private boolean insufficientMaterial() {
        int minors = 0, bishops = 0, bishopSquareColor = -1;
        boolean sameColor = true;
        for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) {
            Piece p = board.get(r, c);
            if (p == null || p.getType() == PieceType.KING) continue;
            if (p.getType() != PieceType.BISHOP && p.getType() != PieceType.KNIGHT) return false;
            minors++;
            if (p.getType() == PieceType.BISHOP) {
                bishops++;
                int squareColor = (r + c) % 2;
                if (bishopSquareColor != -1 && bishopSquareColor != squareColor) sameColor = false;
                bishopSquareColor = squareColor;
            }
        }
        return minors <= 1 || (bishops == minors && sameColor);
    }
    private void addRepetition() { repetitions.merge(positionKey(), 1, Integer::sum); }
    private String positionKey() {
        StringBuilder key = new StringBuilder(turn.name());
        for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) {
            Piece p = board.get(r, c);
            key.append('/');
            if (p != null) key.append(p.getColor().ordinal()).append(p.getType().ordinal());
        }
        for (PieceColor color : PieceColor.values()) {
            Piece king = board.get(color.homeRow(), 4);
            for (int col : new int[] {0, 7}) {
                Piece rook = board.get(color.homeRow(), col);
                boolean right = king != null && king.getType() == PieceType.KING && king.getColor() == color && !king.hasMoved()
                        && rook != null && rook.getType() == PieceType.ROOK && rook.getColor() == color && !rook.hasMoved();
                key.append(right ? 'Y' : 'N');
            }
        }
        // Sólo cuenta al paso si existe una captura legal, no si el peón está clavado.
        Position ep = board.getEnPassant();
        if (ep != null) for (int dc : new int[] {-1, 1}) {
            Position from = new Position(ep.getRow() - turn.direction(), ep.getCol() + dc);
            Piece p = board.get(from);
            if (p != null && p.getType() == PieceType.PAWN && p.getColor() == turn
                    && validator.validate(board, new Move(from, ep), turn).isValid()) {
                key.append(ep); break;
            }
        }
        return key.toString();
    }
}
