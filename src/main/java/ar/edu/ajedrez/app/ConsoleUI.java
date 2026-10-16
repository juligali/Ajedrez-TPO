package ar.edu.ajedrez.app;

import java.util.Locale;
import java.util.Scanner;
import ar.edu.ajedrez.core.board.Board;
import ar.edu.ajedrez.core.board.Position;
import ar.edu.ajedrez.core.game.IGameService;
import ar.edu.ajedrez.core.game.Move;
import ar.edu.ajedrez.core.game.MoveResult;
import ar.edu.ajedrez.core.pieces.Piece;
import ar.edu.ajedrez.core.pieces.PieceColor;
import ar.edu.ajedrez.core.pieces.PieceType;

/** Segundo adaptador: demuestra que el núcleo funciona sin navegador. */
public class ConsoleUI {
    private final IGameService game;

    public ConsoleUI(IGameService game) {
        this.game = game;
    }

    public void run() {
        // Esta consola es dueña de la entrada durante su ejecución.
        // Al salir se cierran el Scanner y System.in.
        try (Scanner input = new Scanner(System.in)) {
            System.out.println("Ajedrez: e2 e4 | deshacer | rehacer | salir");
            while (true) {
                Board board = game.getBoard();
                for (int row = 0; row < Board.SIZE; row++) {
                    System.out.print((Board.SIZE - row) + " ");
                    for (int column = 0; column < Board.SIZE; column++) {
                        Piece piece = board.get(row, column);
                        String symbol = piece == null ? "." :
                                "KQRBNP".substring(piece.getType().ordinal(), piece.getType().ordinal() + 1);
                        System.out.print((piece != null && piece.getColor() == PieceColor.BLACK
                                ? symbol.toLowerCase(Locale.ROOT) : symbol) + " ");
                    }
                    System.out.println();
                }
                System.out.println("  a b c d e f g h\n" + game.getTurn().label() + " / " + game.getStatus());
                if (!input.hasNextLine()) {
                    break;
                }
                String line = input.nextLine().trim();
                if (line.equals("salir")) {
                    break;
                }
                try {
                    MoveResult result = switch (line) {
                        case "deshacer" -> game.undo();
                        case "rehacer" -> game.redo();
                        default -> {
                            String[] parts = line.split("\\s+");
                            if (parts.length < 2 || parts.length > 3) {
                                throw new IllegalArgumentException(
                                        "Usá: e2 e4. Promoción opcional: e7 e8 KNIGHT.");
                            }
                            yield game.move(new Move(Position.from(parts[0]), Position.from(parts[1]),
                                    parts.length == 3 ? PieceType.valueOf(parts[2].toUpperCase(Locale.ROOT))
                                            : PieceType.QUEEN));
                        }
                    };
                    System.out.println(MoveMessages.message(result));
                } catch (IllegalArgumentException exception) {
                    System.out.println(exception.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        new ConsoleUI(StandardGame.create()).run();
    }
}
