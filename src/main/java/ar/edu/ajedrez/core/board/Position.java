package ar.edu.ajedrez.core.board;

/**
 * Una casilla del tablero: fila y columna, contadas desde 0.
 * Es un valor inmutable con igualdad por contenido (equals/hashCode del record).
 * No valida límites: una posición fuera del tablero es un dato legítimo
 * (por ejemplo, una jugada ingresada por el usuario) que Board.contains permite detectar.
 */
public record Position(int row, int column) {
}
