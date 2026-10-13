package ar.edu.ajedrez.app;

import java.util.Scanner;
import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.game.*;
import ar.edu.ajedrez.core.pieces.*;

/** Segundo adaptador: demuestra que el núcleo funciona sin navegador. */
public class ConsoleUI {
    private final IGameService game;
    public ConsoleUI(IGameService game) { this.game = game; }
    public void run() {
        Scanner input = new Scanner(System.in);
        System.out.println("Ajedrez: e2 e4 | deshacer | rehacer | salir");
        while (true) {
            Board b = game.getBoard();
            for (int r = 0; r < 8; r++) {
                System.out.print((8-r) + " ");
                for (int c = 0; c < 8; c++) {
                    Piece p = b.get(r, c);
                    String symbol = p == null ? "." : "KQRBNP".substring(p.getType().ordinal(), p.getType().ordinal()+1);
                    System.out.print((p != null && p.getColor() == PieceColor.BLACK ? symbol.toLowerCase() : symbol) + " ");
                }
                System.out.println();
            }
            System.out.println("  a b c d e f g h\n" + game.getTurn().label() + " / " + game.getStatus());
            if (!input.hasNextLine()) break;
            String line = input.nextLine().trim();
            if (line.equals("salir")) break;
            try {
                MoveResult result;
                if (line.equals("deshacer")) result = game.undo();
                else if (line.equals("rehacer")) result = game.redo();
                else {
                    String[] parts = line.split("\\s+");
                    if (parts.length < 2 || parts.length > 3) throw new IllegalArgumentException("Usá: e2 e4. Promoción opcional: e7 e8 KNIGHT.");
                    result = game.move(new Move(Position.from(parts[0]), Position.from(parts[1]),
                            parts.length == 3 ? PieceType.valueOf(parts[2].toUpperCase()) : PieceType.QUEEN));
                }
                System.out.println(result.getMessage());
            } catch (IllegalArgumentException ex) { System.out.println(ex.getMessage()); }
        }
    }
    public static void main(String[] args) { new ConsoleUI(StandardGame.create()).run(); }
}
