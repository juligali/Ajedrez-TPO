package ar.edu.ajedrez.core.pieces;

public enum PieceColor {
    WHITE, BLACK;
    public PieceColor opposite() { return this == WHITE ? BLACK : WHITE; }
    public int direction() { return this == WHITE ? -1 : 1; }
    public int homeRow() { return this == WHITE ? 7 : 0; }
    public String label() { return this == WHITE ? "Blancas" : "Negras"; }
}
