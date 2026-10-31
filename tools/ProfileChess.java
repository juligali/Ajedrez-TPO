import ar.edu.ajedrez.app.StandardGame;
import ar.edu.ajedrez.core.game.*;
import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.ai.*;
import ar.edu.ajedrez.core.pieces.*;
import ar.edu.ajedrez.core.validation.*;
import java.lang.management.*;
import java.lang.ref.WeakReference;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import jdk.jfr.*;
import jdk.jfr.consumer.*;

/** Diagnostic scenario, not a JMH benchmark or a latency guarantee. Java 21. */
public class ProfileChess {
    static volatile Object sink;
    static final ThreadMXBean cpu = ManagementFactory.getThreadMXBean();
    static final String[][] OPENING = {{"e2","e4"},{"e7","e5"},{"g1","f3"},{"b8","c6"},{"f1","b5"},{"a7","a6"}};
    static Game position() {
        Game g = StandardGame.create();
        for (String[] m : OPENING) require(g.move(new Move(Position.from(m[0]), Position.from(m[1]))));
        return g;
    }
    static void require(MoveResult r) { if (!r.isAccepted()) throw new AssertionError(r.getMessage()); }
    static void cycle() {
        Game g=position();
        for(int i=0;i<6;i++) require(g.undo());
        for(int i=0;i<6;i++) require(g.redo());
        if(g.getHistory().size()!=6) throw new AssertionError("history");
        sink=g;
    }
    static long heap() throws Exception {
        sink=null;
        // Same diagnostic conditions at every checkpoint; not application behavior.
        System.gc(); Thread.sleep(200); System.gc(); Thread.sleep(200);
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
    }
    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ROOT);
        Path out=Path.of(args.length>0?args[0]:"build/profile"); Files.createDirectories(out);
        if(!cpu.isCurrentThreadCpuTimeSupported()) throw new IllegalStateException("CPU time unavailable");
        cpu.setThreadCpuTimeEnabled(true);
        try(Recording recording=new Recording(Configuration.getConfiguration("profile"));
            PrintWriter rows=new PrintWriter(Files.newBufferedWriter(out.resolve("java-samples.csv")))) {
            recording.setName("BLOCKMATE stage 5"); recording.start();
            rows.println("scenario,sample,wall_ms,thread_cpu_ms,result");
            Game fixed=position(); Position from=Position.from("b5");
            for(int i=0;i<500;i++) sink=fixed.legalMoves(from);
            for(int i=1;i<=1000;i++) {
                long c=cpu.getCurrentThreadCpuTime(),t=System.nanoTime();
                var moves=fixed.legalMoves(from);
                long dt=System.nanoTime()-t,dc=cpu.getCurrentThreadCpuTime()-c;
                rows.printf("legalMoves_b5,%d,%.6f,%.6f,%d%n",i,dt/1e6,dc/1e6,moves.size()); sink=moves;
            }
            PieceFactory f=new PieceFactory(); CheckDetector d=new CheckDetector();
            MoveValidator v=new MoveValidator(d,f);
            for(int i=-2;i<=10;i++) {
                MinimaxAi ai=new MinimaxAi(v,d,f,new Random(20261031));
                Board board=fixed.getBoard();
                long c=cpu.getCurrentThreadCpuTime(),t=System.nanoTime();
                Move move=ai.chooseMove(board,fixed.getTurn(),Difficulty.EXTREME);
                long dt=System.nanoTime()-t,dc=cpu.getCurrentThreadCpuTime()-c;
                if(move==null || !v.validate(board,move,fixed.getTurn()).isValid()) throw new AssertionError("AI move");
                if(i>0) rows.printf("AI_EXTREME,%d,%.6f,%.6f,%s-%s%n",i,dt/1e6,dc/1e6,move.getFrom(),move.getTo());
            }
            for(int i=0;i<50;i++) cycle();
            List<WeakReference<Game>> refs=new ArrayList<>();
            try(PrintWriter mem=new PrintWriter(Files.newBufferedWriter(out.resolve("java-memory.csv")))) {
                mem.println("completed_games,heap_used_after_gc_bytes,old_games_alive,old_games_observed");
                mem.printf("0,%d,0,0%n",heap());
                for(int batch=1;batch<=5;batch++) {
                    for(int i=0;i<100;i++) {cycle();refs.add(new WeakReference<>((Game)sink));}
                    long bytes=heap(); long alive=refs.stream().filter(r->r.get()!=null).count();
                    mem.printf("%d,%d,%d,%d%n",batch*100,bytes,alive,refs.size());
                }
            }
            recording.stop(); recording.dump(out.resolve("java-profile.jfr"));
        }
        Map<String,Long> stacks=new TreeMap<>(),alloc=new TreeMap<>();
        long samples=0,allocSamples=0;
        for(RecordedEvent e:RecordingFile.readAllEvents(out.resolve("java-profile.jfr"))) {
            String name=e.getEventType().getName();
            if(name.equals("jdk.ExecutionSample")) {
                samples++;
                var st=e.getStackTrace();
                if(st!=null) for(var frame:st.getFrames()) {
                    var m=frame.getMethod(); String cls=m.getType().getName();
                    if(cls.startsWith("ar.edu.ajedrez")) {stacks.merge(cls+"."+m.getName(),1L,Long::sum);break;}
                }
            } else if(name.equals("jdk.ObjectAllocationSample")) {
                allocSamples++; alloc.merge(e.getClass("objectClass").getName(),e.getLong("weight"),Long::sum);
            }
        }
        try(PrintWriter p=new PrintWriter(Files.newBufferedWriter(out.resolve("jfr-summary.txt")))) {
            p.println("jdk.ExecutionSample events="+samples+"; first chess frame per sampled stack (not exact CPU time):");
            stacks.entrySet().stream().sorted(Map.Entry.<String,Long>comparingByValue().reversed()).limit(15).forEach(e->p.println(e.getValue()+" "+e.getKey()));
            p.println("jdk.ObjectAllocationSample events="+allocSamples+"; estimated allocation weight bytes (not retained heap):");
            alloc.entrySet().stream().sorted(Map.Entry.<String,Long>comparingByValue().reversed()).limit(15).forEach(e->p.println(e.getValue()+" "+e.getKey()));
        }
        System.out.println("Profile complete: 1000 legal queries, 10 AI samples, 500 memory cycles; assertions passed.");
    }
}
