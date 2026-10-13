package ar.edu.ajedrez.core.board;

public final class Position {
    private final int row;
    private final int col;

    public Position(int row, int col) { this.row = row; this.col = col; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public static Position from(String square) {
        if (square == null || !square.matches("[a-h][1-8]"))
            throw new IllegalArgumentException("La casilla debe estar entre a1 y h8.");
        return new Position(8 - (square.charAt(1) - '0'), square.charAt(0) - 'a');
    }
    @Override public String toString() { return "" + (char) ('a' + col) + (8 - row); }
    @Override public boolean equals(Object other) {
        if (!(other instanceof Position)) return false;
        Position p = (Position) other;
        return row == p.row && col == p.col;
    }
    @Override public int hashCode() { return row * 8 + col; }
}
