package ar.edu.ajedrez.core.pieces;

import java.util.EnumMap;
import ar.edu.ajedrez.core.movement.*;

/** Simple Factory: selecciona la estrategia y comparte comportamientos sin estado. */
public class PieceFactory {
    private final EnumMap<PieceType, IMovementStrategy> strategies = new EnumMap<>(PieceType.class);
    public PieceFactory() {
        IMovementStrategy rook = new RookMovement(), bishop = new BishopMovement();
        strategies.put(PieceType.ROOK, rook);
        strategies.put(PieceType.BISHOP, bishop);
        strategies.put(PieceType.QUEEN, new QueenMovement(rook, bishop));
        strategies.put(PieceType.KING, new KingMovement());
        strategies.put(PieceType.KNIGHT, new KnightMovement());
        strategies.put(PieceType.PAWN, new PawnMovement());
    }
    public Piece create(PieceType type, PieceColor color) {
        if (type == null || color == null) throw new IllegalArgumentException("Pieza incompleta.");
        return new Piece(type, color, strategies.get(type));
    }
}
