package ar.edu.ajedrez.core.pieces;

public enum PieceType {
    KING("Rey", "Steve", null), QUEEN("Reina", "Alex", 9), ROOK("Torre", "Gólem de hierro", 5),
    BISHOP("Alfil", "Bruja", 3), KNIGHT("Caballo", "Caballo", 3), PAWN("Peón", "Creeper", 1);
    private final String label;
    private final String character;
    private final Integer points;
    PieceType(String label, String character, Integer points) {
        this.label = label; this.character = character; this.points = points;
    }
    public String getLabel() { return label; }
    public String getCharacter() { return character; }
    /** null significa sin valor numérico: el rey no se captura. */
    public Integer getPoints() { return points; }
}
