package ar.edu.ajedrez.core.pieces;

public enum PieceType {
    KING("Rey", null), QUEEN("Reina", 9), ROOK("Torre", 5),
    BISHOP("Alfil", 3), KNIGHT("Caballo", 3), PAWN("Peón", 1);

    private final String label;
    private final Integer points;

    PieceType(String label, Integer points) {
        this.label = label;
        this.points = points;
    }

    public String getLabel() { return label; }

    /** null significa sin valor numérico: el rey no se captura. */
    public Integer getPoints() { return points; }
}
