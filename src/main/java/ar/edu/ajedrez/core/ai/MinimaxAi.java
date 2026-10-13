package ar.edu.ajedrez.core.ai;

import java.util.*;
import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.game.*;
import ar.edu.ajedrez.core.pieces.*;
import ar.edu.ajedrez.core.validation.*;

/**
 * IA didáctica: azar, capturas y búsqueda minimax según el nivel.
 * La poda alfa-beta evita explorar opciones que ya sabemos peores.
 * Usa exactamente el mismo validador que una jugada humana.
 */
public class MinimaxAi implements IChessAi {
    private final MoveValidator validator;
    private final CheckDetector detector;
    private final PieceFactory factory;
    private final Random random;
    private int nodes, nodeLimit;
    private long deadline;
    private static final int MATE = 100000;
    private static class SearchLimit extends RuntimeException { private static final long serialVersionUID = 1L; }

    public MinimaxAi(MoveValidator validator, CheckDetector detector, PieceFactory factory, Random random) {
        this.validator=validator; this.detector=detector; this.factory=factory; this.random=random;
    }
    @Override
    public synchronized Move chooseMove(Board board, PieceColor color, Difficulty difficulty) {
        List<Move> moves = legalMoves(board, color);
        if (moves.isEmpty()) return null;
        Collections.shuffle(moves, random);
        if (difficulty == Difficulty.SUPER_EASY) return moves.get(0);
        moves.sort(Comparator.comparingInt((Move m) -> movePriority(board,m)).reversed());
        if (difficulty == Difficulty.EASY)
            return random.nextInt(100) < 35 ? moves.get(random.nextInt(moves.size())) : moves.get(0);

        nodes=0; nodeLimit=difficulty.getNodes();
        deadline=System.nanoTime()+difficulty.getMilliseconds()*1_000_000L;
        Move best=moves.get(0);
        // Sólo conservamos una profundidad si se terminó de comparar toda la raíz.
        for (int depth=1; depth<=difficulty.getDepth(); depth++) {
            Move candidate=best; int bestScore=-MATE*2;
            try {
                for (Move move:moves) {
                    checkLimit();
                    Board child=apply(board,move);
                    int score=-search(child,color.opposite(),depth-1,-MATE*2,MATE*2,1);
                    if (score>bestScore) { bestScore=score; candidate=move; }
                }
                best=candidate;
                if (bestScore>=MATE-100) break;
                final Move first=best;
                moves.sort(Comparator.comparingInt(m -> m==first ? 0 : 1));
            } catch (SearchLimit limit) { break; }
        }
        return best;
    }
    private void checkLimit() {
        if (++nodes>nodeLimit || System.nanoTime()>deadline) throw new SearchLimit();
    }
    private int search(Board board, PieceColor turn, int depth, int alpha, int beta, int ply) {
        checkLimit();
        List<Move> moves=legalMoves(board,turn);
        if (moves.isEmpty()) return detector.isInCheck(board,turn) ? -MATE+ply : 0;
        if (board.getHalfMoveClock()>=100) return 0;
        if (depth==0) return evaluate(board,turn);
        moves.sort(Comparator.comparingInt((Move m)->movePriority(board,m)).reversed());
        int best=-MATE*2;
        for (Move move:moves) {
            int score=-search(apply(board,move),turn.opposite(),depth-1,-beta,-alpha,ply+1);
            best=Math.max(best,score); alpha=Math.max(alpha,score);
            if (alpha>=beta) break;
        }
        return best;
    }
    public List<Move> legalMoves(Board board, PieceColor turn) {
        List<Move> result=new ArrayList<>();
        for(int r=0;r<8;r++) for(int c=0;c<8;c++) {
            Position from=new Position(r,c); Piece piece=board.get(from);
            if(piece==null || piece.getColor()!=turn) continue;
            for(int tr=0;tr<8;tr++) for(int tc=0;tc<8;tc++) {
                Position to=new Position(tr,tc);
                if(piece.getType()==PieceType.PAWN && (tr==0 || tr==7)) {
                    for(PieceType type:new PieceType[]{PieceType.QUEEN,PieceType.ROOK,PieceType.BISHOP,PieceType.KNIGHT}) {
                        Move move=new Move(from,to,type);
                        if(validator.validate(board,move,turn)==null) result.add(move);
                    }
                } else {
                    Move move=new Move(from,to);
                    if(validator.validate(board,move,turn)==null) result.add(move);
                }
            }
        }
        return result;
    }
    private Board apply(Board board, Move move) {
        Board child=board.copy(); new MoveCommand(child,move,factory).execute(); return child;
    }
    private int value(Piece piece) { return piece==null || piece.getType().getPoints()==null ? 0 : piece.getType().getPoints()*100; }
    private int movePriority(Board board, Move move) {
        Piece piece=board.get(move.getFrom()), target=board.get(move.getTo());
        int capture=value(target);
        if(piece.getType()==PieceType.PAWN && move.getFrom().getCol()!=move.getTo().getCol() && target==null) capture=100;
        int promotion=piece.getType()==PieceType.PAWN && (move.getTo().getRow()==0 || move.getTo().getRow()==7)
                ? move.getPromotion().getPoints()*100 : 0;
        return capture*10+promotion-(capture>0 ? value(piece) : 0);
    }
    private int evaluate(Board board, PieceColor turn) {
        int score=0;
        for(int r=0;r<8;r++) for(int c=0;c<8;c++) {
            Piece piece=board.get(r,c); if(piece==null) continue;
            int points=value(piece);
            if(piece.getType()==PieceType.PAWN) points+=(piece.getColor()==PieceColor.WHITE ? 6-r : r-1)*5;
            if(piece.getType()==PieceType.KNIGHT || piece.getType()==PieceType.BISHOP)
                points+=28-(Math.abs(2*r-7)+Math.abs(2*c-7))*2;
            score+=piece.getColor()==turn ? points : -points;
        }
        return score;
    }
}
