package ar.edu.ajedrez.core.board;

import ar.edu.ajedrez.core.pieces.Piece;

public class Board {
    public static final int SIZE = 8;
    private final Piece[][] squares = new Piece[SIZE][SIZE];
    private Position enPassant;
    private int halfMoveClock;
    public boolean contains(Position p) {
        return p != null && p.getRow() >= 0 && p.getRow() < SIZE && p.getCol() >= 0 && p.getCol() < SIZE;
    }
    public Piece get(Position p) { return contains(p) ? squares[p.getRow()][p.getCol()] : null; }
    public Piece get(int row, int col) { return get(new Position(row, col)); }
    public void set(Position p, Piece piece) {
        if (!contains(p)) throw new IllegalArgumentException("Casilla fuera del tablero.");
        squares[p.getRow()][p.getCol()] = piece;
    }
    public Position getEnPassant() { return enPassant; }
    public void setEnPassant(Position p) { enPassant = p; }
    public int getHalfMoveClock() { return halfMoveClock; }
    public void setHalfMoveClock(int value) { halfMoveClock = value; }
    public boolean pathClear(Position from, Position to) {
        if (!contains(from) || !contains(to) || from.equals(to)) return false;
        int rowDistance = Math.abs(to.getRow() - from.getRow());
        int colDistance = Math.abs(to.getCol() - from.getCol());
        if (rowDistance != 0 && colDistance != 0 && rowDistance != colDistance) return false;
        int dr = Integer.signum(to.getRow() - from.getRow());
        int dc = Integer.signum(to.getCol() - from.getCol());
        int r = from.getRow() + dr, c = from.getCol() + dc;
        while (r != to.getRow() || c != to.getCol()) {
            if (get(r, c) != null) return false;
            r += dr; c += dc;
        }
        return true;
    }
    public Board copy() { Board copy = new Board(); copy.restore(this); return copy; }
    public void restore(Board other) {
        // Piece y Position son inmutables: es seguro compartir sus referencias.
        for (int r = 0; r < SIZE; r++) System.arraycopy(other.squares[r], 0, squares[r], 0, SIZE);
        enPassant = other.enPassant;
        halfMoveClock = other.halfMoveClock;
    }
}
