package ar.edu.ajedrez.core.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ar.edu.ajedrez.core.pieces.Piece;
import ar.edu.ajedrez.core.pieces.PieceColor;

/**
 * El tablero: guarda qué pieza ocupa cada casilla. No conoce las reglas del juego.
 * Sus dimensiones entran por constructor, no están fijas en el código.
 * Las operaciones que reciben una Position exigen que esté dentro del tablero
 * (se consulta con contains) y lanzan IllegalArgumentException si no lo está.
 */
public class Board {
       private final Piece[][] squares;

    public Board(int rows, int columns) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Board dimensions must be positive: " + rows + "x" + columns);
        }
        this.squares = new Piece[rows][columns];
    }

    public int rows() {
        return squares.length;
    }

    public int columns() {
        return squares[0].length;
    }

    public boolean contains(Position position) {
        return position.row() >= 0 && position.row() < rows()
                && position.column() >= 0 && position.column() < columns();
    }

    /** Devuelve la pieza de la casilla, o null si está vacía. */
    public Piece pieceAt(Position position) {
        requireInside(position);
        return squares[position.row()][position.column()];
    }

    public boolean isEmpty(Position position) {
        return pieceAt(position) == null;
    }

    /** Coloca la pieza en la casilla; si había otra, la reemplaza. */
    public void place(Piece piece, Position position) {
        Objects.requireNonNull(piece, "piece");
        requireInside(position);
        squares[position.row()][position.column()] = piece;
    }

    /** Mueve la pieza de origen a destino; si el destino estaba ocupado, esa pieza queda capturada. */
    public void movePiece(Position from, Position to) {
        Piece piece = pieceAt(from);
        if (piece == null) {
            throw new IllegalArgumentException("There is no piece at " + from);
        }
        requireInside(to);
        squares[from.row()][from.column()] = null;
        squares[to.row()][to.column()] = piece;
    }

    /** Posiciones ocupadas por piezas de ese color, recorriendo por filas. */
    public List<Position> positionsOf(PieceColor color) {
        List<Position> positions = new ArrayList<>();
        for (int row = 0; row < rows(); row++) {
            for (int column = 0; column < columns(); column++) {
                Piece piece = squares[row][column];
                if (piece != null && piece.color() == color) {
                    positions.add(new Position(row, column));
                }
            }
        }
        return positions;
    }

    /**
     * Copia independiente del tablero: duplica las casillas pero comparte las piezas,
     * que son inmutables. Mover una pieza en la copia no afecta al original.
     */
    public Board copy() {
        Board copy = new Board(rows(), columns());
        for (int row = 0; row < rows(); row++) {
            System.arraycopy(squares[row], 0, copy.squares[row], 0, columns());
        }
        return copy;
    }

    /** Dos tableros son iguales si tienen las mismas dimensiones y las mismas piezas en las mismas casillas. */
    @Override
    public boolean equals(Object other) {
        return other instanceof Board board && Arrays.deepEquals(squares, board.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    private void requireInside(Position position) {
        if (!contains(position)) {
            throw new IllegalArgumentException("Position outside the board: " + position);
        }
    }
}
