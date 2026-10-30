package ar.edu.ajedrez;

import java.io.IOException;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.file.*;
import org.junit.jupiter.api.io.TempDir;
import ar.edu.ajedrez.app.StandardGame;
import ar.edu.ajedrez.app.persistence.FileGameRepository;
import ar.edu.ajedrez.core.ai.*;
import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.game.*;
import ar.edu.ajedrez.core.pieces.*;
import ar.edu.ajedrez.core.validation.*;
import ar.edu.ajedrez.core.session.*;
import static ar.edu.ajedrez.core.pieces.PieceType.*;
import static ar.edu.ajedrez.core.pieces.PieceColor.*;

class FeatureTest {
    static final PieceFactory factory=new PieceFactory();
    static final CheckDetector detector=new CheckDetector();
    static final MoveValidator validator=new MoveValidator(detector,factory);
    static class MemoryRepository implements IGameRepository {
        SavedMatch saved; boolean fail;
        @Override
        public Optional<SavedMatch> load(){return Optional.ofNullable(saved);}
        @Override
        public void save(SavedMatch m){if(fail)throw new PersistenceException("Disco simulado",null);saved=m;}
    }
    static class TestClock extends Clock {
        long now=100000;
        @Override
        public ZoneId getZone(){return ZoneOffset.UTC;}
        @Override
        public Clock withZone(ZoneId zone){return this;}
        @Override
        public Instant instant(){return Instant.ofEpochMilli(now);}
    }
    static void expect(boolean condition,String message){assertTrue(condition, message);}
    static Move move(String f,String t){return new Move(Position.from(f),Position.from(t));}
    static void put(Board b,String square,PieceType type,PieceColor color){b.set(Position.from(square),factory.create(type,color));}
    static Game game(Board b,PieceColor color){return new Game(b,color,validator,detector,factory);}
    static MinimaxAi ai(){return new MinimaxAi(validator,detector,factory,new Random(12));}
    static MatchSession session(IGameRepository repo,Clock clock){return new MatchSession(StandardGame::create,repo,ai(),clock);}
    static void play(IGameService g,String f,String t){expect(g.move(move(f,t)).isAccepted(),"Jugada "+f+t);}
    static String board(Board b){
        StringBuilder s=new StringBuilder();
        for(int r=0;r<8;r++)for(int c=0;c<8;c++){Piece p=b.get(r,c);s.append(p==null?".":p.getType()+""+p.getColor()+p.hasMoved());}
        return s.toString()+b.getEnPassant()+b.getHalfMoveClock();
    }
    @TempDir
    Path directory;

    @Test
    @DisplayName("Valores pedidos y rey sin valor")
    void scenario1() {
            expect(PAWN.getPoints()==1&&KNIGHT.getPoints()==3&&BISHOP.getPoints()==3&&ROOK.getPoints()==5&&QUEEN.getPoints()==9,"Valores");
            expect(KING.getPoints()==null,"Rey sin valor numérico");
    }

    @Test
    @DisplayName("Puntos por bando se restauran con undo y redo")
    void scenario2() {
            Game g=StandardGame.create();play(g,"e2","e4");play(g,"d7","d5");play(g,"e4","d5");play(g,"d8","d5");
            expect(g.getCapturedPoints(WHITE)==1&&g.getCapturedPoints(BLACK)==1,"Un punto cada uno");
            g.undo();expect(g.getCapturedPoints(BLACK)==0,"Undo captura");
            g.redo();expect(g.getCapturedPoints(BLACK)==1,"Redo captura");
    }

    @Test
    @DisplayName("Mate con una torre atacante y apoyo del rey")
    void scenario3() {
            Board b=new Board();put(b,"g6",KING,WHITE);put(b,"h8",KING,BLACK);put(b,"a1",ROOK,WHITE);
            Game g=game(b,WHITE);play(g,"a1","a8");
            expect(g.getWinner()==WHITE&&g.getStatus().equals("CHECKMATE"),"Mate con una sola pieza atacante");
            g.undo();expect(g.getWinner()==null,"Corona desaparece al deshacer");
    }

    @Test
    @DisplayName("Mate con un caballo atacante y rey rival encerrado")
    void scenario4() {
            Board b=new Board();put(b,"a1",KING,WHITE);put(b,"h8",KING,BLACK);put(b,"d6",KNIGHT,WHITE);
            put(b,"g8",ROOK,BLACK);put(b,"h7",ROOK,BLACK);put(b,"g7",PAWN,BLACK);
            Game g=game(b,WHITE);play(g,"d6","f7");expect(g.getWinner()==WHITE,"Mate de caballo");
    }

    @Test
    @DisplayName("Rey y caballo contra rey no fuerza un mate ficticio")
    void scenario5() {
            Board b=new Board();put(b,"a1",KING,WHITE);put(b,"h8",KING,BLACK);put(b,"d4",KNIGHT,WHITE);
            expect(game(b,WHITE).getStatus().equals("MATERIAL"),"Conserva reglas básicas");
    }

    @Test
    @DisplayName("Cinco niveles eligen movimientos legales sin mutar")
    void scenario6() {
            Board b=StandardGame.create().getBoard();String before=board(b);
            for(Difficulty d:Difficulty.values()){
                long start=System.nanoTime();Move m=ai().chooseMove(b,WHITE,d);
                expect(m!=null&&validator.validate(b,m,WHITE).isValid(),"Legal "+d);
                expect(board(b).equals(before),"No modifica "+d);
                System.out.printf("    %s %.0f ms%n",d,(System.nanoTime()-start)/1e6);
            }
    }

    @Test
    @DisplayName("Nivel medio encuentra mate en una jugada")
    void scenario7() {
            Board b=new Board();put(b,"g6",KING,WHITE);put(b,"h8",KING,BLACK);put(b,"a1",ROOK,WHITE);
            Game g=game(b,WHITE);Move m=ai().chooseMove(b,WHITE,Difficulty.MEDIUM);
            expect(g.move(m).isAccepted()&&g.getWinner()==WHITE,"Reconoce mate");
    }

    @Test
    @DisplayName("Dos jugadores nunca llama a IA")
    void scenario8() {
            int[] calls={0};MemoryRepository repo=new MemoryRepository();
            MatchSession s=new MatchSession(StandardGame::create,repo,(b,c,d)->{calls[0]++;return null;},new TestClock());
            s.newGame(GameMode.TWO_PLAYERS,Difficulty.EXTREME);s.move(move("e2","e4"));
            expect(!s.playAi().isAccepted()&&!s.isAiTurn()&&calls[0]==0,"IA no participa");
    }

    @Test
    @DisplayName("Solo bloquea movimiento humano negro y deshace la pareja")
    void scenario9() {
            MatchSession s=session(new MemoryRepository(),new TestClock());s.newGame(GameMode.SOLO,Difficulty.SUPER_EASY);
            expect(s.move(move("e2","e4")).isAccepted()&&s.isAiTurn(),"Turno IA");
            expect(!s.move(move("e7","e5")).isAccepted(),"No mueve jugador negro");
            expect(s.playAi().isAccepted()&&s.getGame().getHistory().size()==2,"Respuesta IA");
            s.undo();expect(s.getGame().getHistory().isEmpty()&&!s.isAiTurn(),"Undo par");
            s.redo();expect(s.getGame().getHistory().size()==2&&!s.isAiTurn(),"Redo par");
    }

    @Test
    @DisplayName("Guardado recupera historial, capturas, turno y redo")
    void scenario10() {
            MemoryRepository repo=new MemoryRepository();TestClock clock=new TestClock();MatchSession s=session(repo,clock);
            s.newGame(GameMode.TWO_PLAYERS,Difficulty.HARD);
            s.move(move("e2","e4"));s.move(move("d7","d5"));s.move(move("e4","d5"));s.undo();
            String before=board(s.getGame().getBoard());
            MatchSession restored=session(repo,clock);
            expect(board(restored.getGame().getBoard()).equals(before)&&restored.getGame().canRedo(),"Estado y redo");
            restored.redo();expect(restored.getGame().getCapturedPoints(WHITE)==1,"Captura recuperada");
    }

    @Test
    @DisplayName("Al paso sigue disponible tras reabrir")
    void scenario11() {
            MemoryRepository repo=new MemoryRepository();TestClock clock=new TestClock();MatchSession s=session(repo,clock);
            s.newGame(GameMode.TWO_PLAYERS,Difficulty.MEDIUM);
            for(String[] m:new String[][]{{"e2","e4"},{"a7","a6"},{"e4","e5"},{"d7","d5"}})s.move(move(m[0],m[1]));
            s=session(repo,clock);expect(s.move(move("e5","d6")).isAccepted(),"Al paso restaurado");
            expect(s.getGame().getCapturedPoints(WHITE)==1,"Puntúa al paso");
    }

    @Test
    @DisplayName("Recuperar turno pendiente de IA y nivel")
    void scenario12() {
            MemoryRepository repo=new MemoryRepository();TestClock clock=new TestClock();MatchSession s=session(repo,clock);
            s.newGame(GameMode.SOLO,Difficulty.HARD);s.move(move("e2","e4"));
            s=session(repo,clock);expect(s.isAiTurn()&&s.getDifficulty()==Difficulty.HARD,"Modo y nivel");
    }

    @Test
    @DisplayName("Cronómetro persiste, pausa al cerrar y se detiene con mate")
    void scenario13() {
            MemoryRepository repo=new MemoryRepository();TestClock clock=new TestClock();MatchSession s=session(repo,clock);
            s.newGame(GameMode.TWO_PLAYERS,Difficulty.MEDIUM);s.presence("test",true);
            clock.now+=12000;s.presence("test",false);clock.now+=60000;s=session(repo,clock);
            expect(s.getElapsedMillis()==12000,"Continúa tras reabrir");
            s.presence("test",true);
            for(String[] m:new String[][]{{"f2","f3"},{"e7","e5"},{"g2","g4"},{"d8","h4"}})s.move(move(m[0],m[1]));
            clock.now+=5000;expect(s.getElapsedMillis()==12000,"Detenido en mate");
            s=session(repo,clock);expect(s.getElapsedMillis()==12000,"Mate persistido");
            s.undo();s.presence("test",true);clock.now+=3000;
            expect(s.getElapsedMillis()==15000,"Reanuda al deshacer sin sumar el tiempo detenido");
    }

    @Test
    @DisplayName("Fallo de guardado revierte la jugada y los puntos")
    void scenario14() {
            MemoryRepository repo=new MemoryRepository();MatchSession s=session(repo,new TestClock());
            s.newGame(GameMode.TWO_PLAYERS,Difficulty.MEDIUM);String before=board(s.getGame().getBoard());repo.fail=true;
            expect(!s.move(move("e2","e4")).isAccepted(),"No confirma pérdida de datos");
            expect(board(s.getGame().getBoard()).equals(before)&&s.getGame().getHistory().isEmpty(),"Rollback");
    }

    @Test
    @DisplayName("Nueva partida limpia puntos, historial y tiempo")
    void scenario15() {
            TestClock clock=new TestClock();MatchSession s=session(new MemoryRepository(),clock);
            s.newGame(GameMode.TWO_PLAYERS,Difficulty.MEDIUM);s.move(move("e2","e4"));clock.now+=9000;
            s.newGame(GameMode.SOLO,Difficulty.EASY);
            expect(s.getGame().getHistory().isEmpty()&&s.getElapsedMillis()==0&&s.getGame().getCapturedPoints(WHITE)==0,"Reset");
    }

    @Test
    @DisplayName("Archivo real se escribe, se lee y rechaza corrupción")
    void scenario16() {
            Path file=directory.resolve("match.properties");FileGameRepository repo=new FileGameRepository(file);
            MatchSession s=session(repo,new TestClock());s.newGame(GameMode.TWO_PLAYERS,Difficulty.EXTREME);s.move(move("e2","e4"));
            expect(session(new FileGameRepository(file),new TestClock()).getGame().getHistory().size()==1,"Persistencia en disco");
            try{Files.writeString(file,"version=broken");}catch(IOException e){throw new java.io.UncheckedIOException(e);}
            try{repo.load();throw new AssertionError("Aceptó archivo inválido");}catch(PersistenceException expected){}
    }
}
