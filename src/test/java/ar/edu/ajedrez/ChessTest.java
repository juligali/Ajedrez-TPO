package ar.edu.ajedrez;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertTrue;
import ar.edu.ajedrez.app.StandardGame;
import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.pieces.*;
import ar.edu.ajedrez.core.game.*;
import ar.edu.ajedrez.core.movement.*;
import ar.edu.ajedrez.core.validation.*;
import static ar.edu.ajedrez.core.pieces.PieceType.*;
import static ar.edu.ajedrez.core.pieces.PieceColor.*;

/** Pruebas independientes, sin red ni UI. Arrange / Act / Assert. */
class ChessTest {
    private static final PieceFactory factory = new PieceFactory();
    private static final CheckDetector check = new CheckDetector();
    private static final MoveValidator validator = new MoveValidator(check, factory);

    private static void expect(boolean condition, String message) {
        assertTrue(condition, message);
    }
    private static Position p(String square) { return Position.from(square); }
    private static Move move(String from, String to) { return new Move(p(from), p(to)); }
    private static void put(Board board, String square, PieceType type, PieceColor color) {
        board.set(p(square), factory.create(type, color));
    }
    private static Board kings(String white, String black) {
        Board b = new Board(); put(b, white, KING, WHITE); put(b, black, KING, BLACK); return b;
    }
    private static Game game(Board b, PieceColor turn) { return new Game(b, turn, validator, check, factory); }
    private static void play(Game game, String from, String to) {
        MoveResult result = game.move(move(from, to)); expect(result.isAccepted(), from + to + ": " + result.getMessage());
    }
    private static String snapshot(Board board) {
        StringBuilder result = new StringBuilder();
        for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) {
            Piece piece = board.get(r,c);
            result.append(piece == null ? "." : piece.getColor()+"/"+piece.getType()+"/"+piece.hasMoved()).append(';');
        }
        return result + "|" + board.getEnPassant() + "|" + board.getHalfMoveClock();
    }
    private static long perft(Board b, PieceColor turn, int depth) {
        if (depth == 0) return 1;
        long nodes = 0;
        for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) {
            Position from = new Position(r,c); Piece piece = b.get(from);
            if (piece == null || piece.getColor() != turn) continue;
            for (int tr = 0; tr < 8; tr++) for (int tc = 0; tc < 8; tc++) {
                Move m = new Move(from,new Position(tr,tc));
                if (validator.validate(b,m,turn) != null) continue;
                Board child = b.copy(); new MoveCommand(child,m,factory).execute();
                nodes += perft(child,turn.opposite(),depth-1);
            }
        }
        return nodes;
    }
    
    @Test
    @DisplayName("Posiciones: conversión y límites")
    void scenario1() {
            expect(p("a1").getRow()==7 && p("h8").getCol()==7, "Coordenadas");
            expect(p("e4").toString().equals("e4"), "Ida y vuelta");
            try { p("i9"); throw new AssertionError("Aceptó i9"); } catch(IllegalArgumentException expected) { }
    }

    @Test
    @DisplayName("Torre: rectas, obstáculos y rechazo de diagonal")
    void scenario2() {
            Board b = new Board(); Piece rook = factory.create(ROOK,WHITE);
            expect(rook.canMove(b,p("a1"),p("a8")), "Recta");
            expect(!rook.canMove(b,p("a1"),p("b2")), "Diagonal");
            put(b,"a4",PAWN,BLACK);
            expect(!rook.canMove(b,p("a1"),p("a8")), "No salta");
            expect(rook.canMove(b,p("a1"),p("a4")), "Puede llegar a la captura");
    }

    @Test
    @DisplayName("Alfil: diagonales y obstáculos")
    void scenario3() {
            Board b = new Board(); Piece bishop=factory.create(BISHOP,WHITE);
            expect(bishop.canMove(b,p("c1"),p("h6")), "Diagonal");
            expect(!bishop.canMove(b,p("c1"),p("c4")), "Vertical");
            put(b,"e3",PAWN,WHITE);
            expect(!bishop.canMove(b,p("c1"),p("h6")), "Obstáculo");
    }

    @Test
    @DisplayName("Reina: composición de torre y alfil")
    void scenario4() {
            Board b=new Board(); Piece queen=factory.create(QUEEN,WHITE);
            expect(queen.canMove(b,p("d4"),p("d8")) && queen.canMove(b,p("d4"),p("a1")), "Ambas estrategias");
            expect(!queen.canMove(b,p("d4"),p("e6")), "No es caballo");
    }

    @Test
    @DisplayName("Caballo: salta y sólo acepta L")
    void scenario5() {
            Board b=StandardGame.create().getBoard(); Piece knight=b.get(p("b1"));
            expect(knight.canMove(b,p("b1"),p("c3")), "Salta peones");
            expect(!knight.canMove(b,p("b1"),p("b3")), "No recto");
    }

    @Test
    @DisplayName("Rey: una casilla y nunca cero")
    void scenario6() {
            Board b=new Board(); Piece king=factory.create(KING,WHITE);
            expect(king.canMove(b,p("e4"),p("f5")), "Diagonal");
            expect(!king.canMove(b,p("e4"),p("e6")) && !king.canMove(b,p("e4"),p("e4")), "Distancia");
    }

    @Test
    @DisplayName("Peones de ambos colores: dirección, doble y bloqueo")
    void scenario7() {
            Board b=new Board(); Piece white=factory.create(PAWN,WHITE), black=factory.create(PAWN,BLACK);
            expect(white.canMove(b,p("e2"),p("e4")) && black.canMove(b,p("d7"),p("d5")), "Doble");
            expect(!white.canMove(b,p("e3"),p("e5")) && !black.canMove(b,p("d5"),p("d6")), "Inicio y dirección");
            put(b,"e3",KNIGHT,BLACK); expect(!white.canMove(b,p("e2"),p("e4")), "Obstáculo intermedio");
    }

    @Test
    @DisplayName("Ataque del peón distinto de avance")
    void scenario8() {
            Board b=new Board(); Piece pawn=factory.create(PAWN,WHITE);
            expect(pawn.canAttack(b,p("e4"),p("d5")), "Amenaza diagonal vacía");
            expect(!pawn.canMove(b,p("e4"),p("d5")), "No mueve diagonal vacía");
            expect(!pawn.canAttack(b,p("e4"),p("e5")), "Avance no es ataque");
            put(b,"d5",ROOK,BLACK);expect(pawn.canMove(b,p("e4"),p("d5")), "Captura diagonal");
    }

    @Test
    @DisplayName("Strategy se sustituye mediante una interfaz (stub)")
    void scenario9() {
            IMovementStrategy stub = new IMovementStrategy() {
                public boolean canMove(Board b, Position from, Position to, PieceColor color) { return true; }
            };
            Piece piece = new Piece(ROOK,WHITE,stub);
            expect(piece.canMove(new Board(),p("a1"),p("b3")), "Piece delega en la estrategia inyectada");
    }

    @Test
    @DisplayName("Inicio: 32 piezas, blancas y 20 movimientos")
    void scenario10() {
            Game g=StandardGame.create();int count=0,total=0;
            for(int r=0;r<8;r++)for(int c=0;c<8;c++){
                if(g.getBoard().get(r,c)!=null)count++;
                total+=g.legalMoves(new Position(r,c)).size();
            }
            expect(count==32 && total==20 && g.getTurn()==WHITE, "Disposición inicial");
            expect(g.getBoard().get(p("d1")).getType()==QUEEN,"Reina en d1");
    }

    @Test
    @DisplayName("Rechazo conserva tablero, turno e historial")
    void scenario11() {
            Game g=StandardGame.create();String before=snapshot(g.getBoard());
            expect(!g.move(move("e7","e5")).isAccepted(),"Turno");
            expect(!g.move(move("a1","a3")).isAccepted(),"Obstáculo");
            expect(!g.move(move("e1","e2")).isAccepted(),"Pieza propia");
            expect(!g.move(new Move(new Position(-1,0),p("a1"))).isAccepted(),"Fuera de rango");
            expect(!g.move(null).isAccepted(),"Null");
            expect(snapshot(g.getBoard()).equals(before)&&g.getTurn()==WHITE&&g.getHistory().isEmpty(),"Sin mutaciones");
    }

    @Test
    @DisplayName("Captura y alternancia")
    void scenario12() {
            Game g=StandardGame.create();play(g,"e2","e4");play(g,"d7","d5");play(g,"e4","d5");
            expect(g.getTurn()==BLACK&&g.getBoard().get(p("e4"))==null,"Turno y origen");
            expect(g.getHistory().get(2).getCaptured().getType()==PAWN,"Captura informada");
    }

    @Test
    @DisplayName("Copias defensivas del tablero y el historial")
    void scenario13() {
            Game g=StandardGame.create();g.getBoard().set(p("e1"),null);
            expect(g.getBoard().get(p("e1"))!=null,"Tablero protegido");
            try{g.getHistory().clear();throw new AssertionError("Historial mutable");}catch(UnsupportedOperationException expected){}
    }

    @Test
    @DisplayName("Jaque detectado y pieza clavada protegida")
    void scenario14() {
            Board b=kings("e1","a8");put(b,"e2",ROOK,WHITE);put(b,"e8",ROOK,BLACK);
            expect(!check.isInCheck(b,WHITE),"Bloqueo");
            expect(validator.validate(b,move("e2","d2"),WHITE)!=null,"No descubre al rey");
            expect(validator.validate(b,move("e2","e8"),WHITE)==null,"Puede capturar atacante");
            b.set(p("e2"),null);expect(check.isInCheck(b,WHITE),"Jaque de torre");
    }

    @Test
    @DisplayName("Reyes no adyacentes y prohibición de capturar rey")
    void scenario15() {
            Board b=kings("e1","e3");
            expect(validator.validate(b,move("e1","e2"),WHITE)!=null,"Reyes adyacentes");
            put(b,"a3",ROOK,WHITE);expect(validator.validate(b,move("a3","e3"),WHITE)!=null,"No captura rey");
    }

    @Test
    @DisplayName("Jaque mate del loco y deshacer fin de partida")
    void scenario16() {
            Game g=StandardGame.create();play(g,"f2","f3");play(g,"e7","e5");play(g,"g2","g4");play(g,"d8","h4");
            expect(g.getStatus().equals("CHECKMATE")&&g.isFinished(),"Mate");
            expect(!g.move(move("a2","a3")).isAccepted(),"Partida finalizada");
            g.undo();expect(!g.isFinished()&&g.getTurn()==BLACK,"Undo de mate");
            g.redo();expect(g.getStatus().equals("CHECKMATE"),"Redo de mate");
    }

    @Test
    @DisplayName("Ahogado diferenciado de jaque mate")
    void scenario17() {
            Board b=kings("c6","a8");put(b,"b6",QUEEN,WHITE);
            Game g=game(b,BLACK);expect(g.getStatus().equals("STALEMATE")&&!g.isInCheck(),"Ahogado");
    }

    @Test
    @DisplayName("Enroque corto y largo para ambos colores")
    void scenario18() {
            for(PieceColor color:PieceColor.values())for(String target:new String[]{"c","g"}){
                Board b=kings("e1","e8");int rank=color==WHITE?1:8;
                put(b,"a"+rank,ROOK,color);put(b,"h"+rank,ROOK,color);
                Game g=game(b,color);String before=snapshot(g.getBoard());play(g,"e"+rank,target+rank);
                expect(g.getBoard().get(p((target.equals("g")?"f":"d")+rank)).getType()==ROOK,"Torre reubicada");
                g.undo();expect(snapshot(g.getBoard()).equals(before),"Restaura enroque");
                g.redo();expect(g.getBoard().get(p(target+rank)).getType()==KING,"Rehace enroque");
            }
    }

    @Test
    @DisplayName("No enroca en jaque, atravesando jaque o hacia jaque")
    void scenario19() {
            for(String rookSquare:new String[]{"e8","f8","g8"}){
                Board b=kings("e1","a8");put(b,"h1",ROOK,WHITE);put(b,rookSquare,ROOK,BLACK);
                expect(validator.validate(b,move("e1","g1"),WHITE)!=null,"Ataque desde "+rookSquare);
            }
    }

    @Test
    @DisplayName("No enroca con rey o torre movidos, ausentes u obstáculo")
    void scenario20() {
            Board b=kings("e1","a8");put(b,"h1",ROOK,WHITE);b.set(p("h1"),b.get(p("h1")).movedCopy());
            expect(validator.validate(b,move("e1","g1"),WHITE)!=null,"Torre movida");
            put(b,"h1",ROOK,WHITE);b.set(p("e1"),b.get(p("e1")).movedCopy());
            expect(validator.validate(b,move("e1","g1"),WHITE)!=null,"Rey movido");
            put(b,"e1",KING,WHITE);put(b,"f1",BISHOP,WHITE);
            expect(validator.validate(b,move("e1","g1"),WHITE)!=null,"Camino ocupado");
            b.set(p("f1"),null);b.set(p("h1"),null);
            expect(validator.validate(b,move("e1","g1"),WHITE)!=null,"Torre ausente");
    }

    @Test
    @DisplayName("Al paso: captura, deshacer, rehacer y restauración exacta")
    void scenario21() {
            Game g=StandardGame.create();play(g,"e2","e4");play(g,"a7","a6");play(g,"e4","e5");play(g,"d7","d5");
            String before=snapshot(g.getBoard());play(g,"e5","d6");
            expect(g.getBoard().get(p("d5"))==null&&g.getBoard().get(p("d6")).getColor()==WHITE,"Captura");
            expect(g.getHistory().get(4).getCaptured()!=null,"Captura en historial");
            String after=snapshot(g.getBoard());g.undo();expect(snapshot(g.getBoard()).equals(before),"Undo exacto");
            g.redo();expect(snapshot(g.getBoard()).equals(after),"Redo exacto");
    }

    @Test
    @DisplayName("Al paso vence después de una jugada")
    void scenario22() {
            Game g=StandardGame.create();play(g,"e2","e4");play(g,"a7","a6");play(g,"e4","e5");play(g,"d7","d5");
            play(g,"a2","a3");play(g,"a6","a5");
            expect(!g.move(move("e5","d6")).isAccepted(),"Derecho vencido");
    }

    @Test
    @DisplayName("Al paso no puede descubrir un jaque horizontal")
    void scenario23() {
            Board b=kings("h5","a8");put(b,"a5",ROOK,BLACK);put(b,"g5",PAWN,WHITE);put(b,"f5",PAWN,BLACK);b.setEnPassant(p("f6"));
            expect(validator.validate(b,move("g5","f6"),WHITE)!=null,"Rey expuesto");
    }

    @Test
    @DisplayName("Promoción a las cuatro piezas y undo exacto")
    void scenario24() {
            for(PieceType type:new PieceType[]{QUEEN,ROOK,BISHOP,KNIGHT}){
                Board b=kings("h1","h8");put(b,"a7",PAWN,WHITE);Game g=game(b,WHITE);
                String before=snapshot(g.getBoard());
                expect(g.move(new Move(p("a7"),p("a8"),type)).isAccepted(),"Promoción válida");
                expect(g.getBoard().get(p("a8")).getType()==type,"Pieza elegida");
                g.undo();expect(snapshot(g.getBoard()).equals(before),"Vuelve el peón");
                g.redo();expect(g.getBoard().get(p("a8")).getType()==type,"Rehace promoción");
            }
    }

    @Test
    @DisplayName("Promoción negra con captura y elección inválida")
    void scenario25() {
            Board b=kings("h1","h8");put(b,"b2",PAWN,BLACK);put(b,"a1",ROOK,WHITE);Game g=game(b,BLACK);
            expect(!g.move(new Move(p("b2"),p("a1"),KING)).isAccepted(),"No promociona a rey");
            expect(g.move(new Move(p("b2"),p("a1"),KNIGHT)).isAccepted(),"Subpromoción con captura");
            expect(g.getBoard().get(p("a1")).getColor()==BLACK,"Color correcto");
            g.undo();expect(g.getBoard().get(p("a1")).getType()==ROOK&&g.getBoard().get(p("b2")).getType()==PAWN,"Restaura capturada");
    }

    @Test
    @DisplayName("Command ejecuta/deshace sin perder metadatos")
    void scenario26() {
            Board b=StandardGame.create().getBoard();String before=snapshot(b);
            ICommand command=new MoveCommand(b,move("e2","e4"),factory);command.execute();
            expect(!snapshot(b).equals(before),"Cambió");command.undo();expect(snapshot(b).equals(before),"Restauró todo");
    }

    @Test
    @DisplayName("Nueva rama elimina rehacer; rechazo lo conserva")
    void scenario27() {
            Game g=StandardGame.create();play(g,"e2","e4");g.undo();
            g.move(move("e7","e5"));expect(g.canRedo(),"Rechazo conserva futuro");
            play(g,"d2","d4");expect(!g.canRedo()&&!g.redo().isAccepted(),"Nueva rama");
    }

    @Test
    @DisplayName("Triple repetición y contadores tras undo/redo")
    void scenario28() {
            Game g=StandardGame.create();
            for(int i=0;i<2;i++){play(g,"g1","f3");play(g,"g8","f6");play(g,"f3","g1");play(g,"f6","g8");}
            expect(g.getStatus().equals("REPETITION"),"Tercera posición");
            g.undo();expect(!g.isFinished(),"Undo reduce repetición");g.redo();expect(g.getStatus().equals("REPETITION"),"Redo restaura");
    }

    @Test
    @DisplayName("Material insuficiente básico y material con mate posible")
    void scenario29() {
            Board b=kings("a1","h8");expect(game(b,WHITE).getStatus().equals("MATERIAL"),"Reyes");
            put(b,"b1",KNIGHT,WHITE);expect(game(b,WHITE).getStatus().equals("MATERIAL"),"Un caballo");
            put(b,"g8",KNIGHT,BLACK);expect(!game(b,WHITE).isFinished(),"Dos caballos opuestos no son tablas automáticas");
    }

    @Test
    @DisplayName("Regla de 50 movimientos y reinicio del contador")
    void scenario30() {
            Board b=kings("e1","e8");put(b,"a1",ROOK,WHITE);b.setHalfMoveClock(99);Game g=game(b,WHITE);play(g,"a1","a2");
            expect(g.getStatus().equals("FIFTY_MOVES"),"100 medios movimientos");
            g.undo();expect(g.getBoard().getHalfMoveClock()==99&&!g.isFinished(),"Undo contador");
            put(b,"b2",PAWN,WHITE);g=game(b,WHITE);play(g,"b2","b3");
            expect(g.getBoard().getHalfMoveClock()==0&&!g.isFinished(),"Peón reinicia");
    }

    @Test
    @DisplayName("Perft inicial: 20 / 400 / 8902 posiciones legales")
    void scenario31() {
            Board b=StandardGame.create().getBoard();
            expect(perft(b,WHITE,1)==20,"Profundidad 1");
            expect(perft(b,WHITE,2)==400,"Profundidad 2");
            expect(perft(b,WHITE,3)==8902,"Profundidad 3");
    }
}
