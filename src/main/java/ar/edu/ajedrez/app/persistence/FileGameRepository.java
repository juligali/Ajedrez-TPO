package ar.edu.ajedrez.app.persistence;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import ar.edu.ajedrez.core.session.*;
import ar.edu.ajedrez.core.ai.Difficulty;
import ar.edu.ajedrez.core.board.Position;
import ar.edu.ajedrez.core.game.Move;
import ar.edu.ajedrez.core.pieces.PieceType;

/** Adaptador de salida: archivo Properties con reemplazo atómico cuando está disponible. */
public class FileGameRepository implements IGameRepository {
    private final Path file;
    public FileGameRepository(Path file) { this.file=file.toAbsolutePath(); }
    public Optional<SavedMatch> load() {
        if(!Files.exists(file)) return Optional.empty();
        try(InputStream input=Files.newInputStream(file)) {
            Properties p=new Properties(); p.load(input);
            if(!"1".equals(p.getProperty("version"))) throw new IllegalArgumentException("Versión no admitida.");
            int count=Integer.parseInt(p.getProperty("count"));
            if(count<0 || count>20000) throw new IllegalArgumentException("Historial inválido.");
            List<Move> moves=new ArrayList<>();
            for(int i=0;i<count;i++) {
                String[] parts=p.getProperty("move."+i).split(",");
                if(parts.length!=3) throw new IllegalArgumentException("Jugada incompleta.");
                moves.add(new Move(Position.from(parts[0]),Position.from(parts[1]),PieceType.valueOf(parts[2])));
            }
            return Optional.of(new SavedMatch(moves,Integer.parseInt(p.getProperty("cursor")),
                    GameMode.valueOf(p.getProperty("mode")),Difficulty.valueOf(p.getProperty("difficulty")),
                    Long.parseLong(p.getProperty("startedAt")),Long.parseLong(p.getProperty("endedAt")),
                    Boolean.parseBoolean(p.getProperty("configured"))));
        } catch(IOException | IllegalArgumentException | NullPointerException error) {
            throw new PersistenceException("No se pudo leer "+file+". Conservá ese archivo antes de intentar recuperarlo.",error);
        }
    }
    public void save(SavedMatch match) {
        Path temp=file.resolveSibling(file.getFileName()+".tmp");
        try {
            Files.createDirectories(file.getParent());
            Properties p=new Properties();
            p.setProperty("version","1"); p.setProperty("count",String.valueOf(match.moves.size()));
            p.setProperty("cursor",String.valueOf(match.cursor)); p.setProperty("mode",match.mode.name());
            p.setProperty("difficulty",match.difficulty.name()); p.setProperty("startedAt",String.valueOf(match.startedAt));
            p.setProperty("endedAt",String.valueOf(match.endedAt)); p.setProperty("configured",String.valueOf(match.configured));
            for(int i=0;i<match.moves.size();i++) {
                Move m=match.moves.get(i); p.setProperty("move."+i,m.getFrom()+","+m.getTo()+","+m.getPromotion().name());
            }
            try(OutputStream output=Files.newOutputStream(temp)) { p.store(output,"Blockmate - partida local"); }
            try { Files.move(temp,file,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING); }
            catch(AtomicMoveNotSupportedException error) { Files.move(temp,file,StandardCopyOption.REPLACE_EXISTING); }
        } catch(IOException error) { throw new PersistenceException("No se pudo escribir "+file,error); }
    }
}
