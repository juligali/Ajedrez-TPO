package ar.edu.ajedrez.app;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import ar.edu.ajedrez.core.board.*;
import ar.edu.ajedrez.core.game.*;
import ar.edu.ajedrez.core.pieces.*;
import ar.edu.ajedrez.core.session.*;
import ar.edu.ajedrez.core.ai.*;
import ar.edu.ajedrez.core.validation.*;
import ar.edu.ajedrez.app.persistence.FileGameRepository;
import java.time.Clock;

/**
 * Adaptador HTTP local del JDK. Una partida compartida por las pestañas del mismo equipo.
 * El navegador dibuja; todas las decisiones de ajedrez se toman mediante IGameService.
 */
public class WebServer {
    private final IMatchService session;
    private int revision = 0;
    private final Path webRoot;
    public WebServer(IMatchService session, Path webRoot) { this.session = session; this.webRoot = webRoot.toAbsolutePath().normalize(); }
    public void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/api/", this::api);
        server.createContext("/", this::staticFile);
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            synchronized(WebServer.this) { session.save(); }
        },"guardar-tiempo-al-cerrar"));
        System.out.println("\nAjedrez de bloques listo: http://127.0.0.1:" + port);
        System.out.println("Abrí esa dirección en tu navegador. Para cerrar el servidor: Ctrl+C.");
    }
    private synchronized void api(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            if (path.equals("/api/state") && method.equals("GET")) {
                send(exchange, 200, "application/json", stateJson()); return;
            }
            if (path.equals("/api/legal") && method.equals("GET")) {
                Map<String, String> params = parse(exchange.getRequestURI().getRawQuery());
                List<Position> moves = session.getGame().legalMoves(Position.from(params.get("from")));
                StringJoiner result = new StringJoiner(",", "[", "]");
                for (Position position : moves) result.add(quote(position.toString()));
                send(exchange, 200, "application/json", "{\"revision\":" + revision + ",\"moves\":" + result + "}"); return;
            }
            if (!method.equals("POST")) { send(exchange, 405, "application/json", "{\"error\":\"Método no permitido\"}"); return; }
            // Evita que otro sitio web envíe jugadas a la aplicación local.
            String origin = exchange.getRequestHeaders().getFirst("Origin");
            String expected = "http://" + exchange.getRequestHeaders().getFirst("Host");
            if (origin != null && !origin.equals(expected)) { send(exchange, 403, "application/json", "{\"error\":\"Origen no permitido\"}"); return; }
            byte[] bytes = exchange.getRequestBody().readNBytes(2049);
            if (bytes.length > 2048) { send(exchange, 413, "application/json", "{\"error\":\"Solicitud demasiado grande\"}"); return; }
            Map<String, String> params = parse(new String(bytes, StandardCharsets.UTF_8));
            if(path.equals("/api/presence")) {
                String active=params.get("active");
                if(!"true".equals(active) && !"false".equals(active)) throw new IllegalArgumentException("Actividad inválida.");
                MoveResult activity=session.presence(params.get("clientId"),Boolean.parseBoolean(active));
                send(exchange,200,"application/json","{\"accepted\":"+activity.isAccepted()+",\"elapsedMillis\":"+session.getElapsedMillis()+",\"clockRunning\":"+session.isClockRunning()+"}");
                return;
            }
            if (!Integer.toString(revision).equals(params.get("revision"))) {
                send(exchange, 409, "application/json", "{\"error\":\"La partida cambió. Actualizá el tablero.\"}"); return;
            }
            MoveResult result;
            switch (path) {
                case "/api/move":
                    PieceType promotion = PieceType.valueOf(params.getOrDefault("promotion", "QUEEN"));
                    result = session.move(new Move(Position.from(params.get("from")), Position.from(params.get("to")), promotion));
                    break;
                case "/api/undo": result = session.undo(); break;
                case "/api/redo": result = session.redo(); break;
                case "/api/ai": result = session.playAi(); break;
                case "/api/save": result = session.save(); break;
                case "/api/new":
                    result = session.newGame(GameMode.valueOf(params.getOrDefault("mode","TWO_PLAYERS")),
                            Difficulty.valueOf(params.getOrDefault("difficulty","MEDIUM")));
                    break;
                default: send(exchange, 404, "application/json", "{\"error\":\"Ruta inexistente\"}"); return;
            }
            if (result.isAccepted()) revision++;
            send(exchange, 200, "application/json", "{\"accepted\":" + result.isAccepted() + ",\"message\":" + quote(MoveMessages.message(result)) + ",\"state\":" + stateJson() + "}");
        } catch (IllegalArgumentException ex) {
            send(exchange, 400, "application/json", "{\"error\":\"Solicitud inválida: verificá las casillas y la promoción.\"}");
        } catch (Exception ex) {
            ex.printStackTrace();
            send(exchange, 500, "application/json", "{\"error\":\"No se pudo procesar la solicitud.\"}");
        } finally { exchange.close(); }
    }
    private String stateJson() {
        IGameService game = session.getGame();
        StringJoiner squares = new StringJoiner(",", "[", "]");
        Board board = session.getGame().getBoard();
        for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) {
            Piece p = board.get(r, c);
            if (p != null) squares.add("{\"square\":" + quote(new Position(r,c).toString()) + ",\"type\":" + quote(p.getType().name()) + ",\"color\":" + quote(p.getColor().name()) + "}");
        }
        StringJoiner history = new StringJoiner(",", "[", "]");
        StringJoiner captures = new StringJoiner(",", "[", "]");
        for (MoveRecord command : session.getGame().getHistory()) {
            history.add("{\"from\":" + quote(command.getMove().getFrom().toString()) + ",\"to\":" + quote(command.getMove().getTo().toString()) + ",\"label\":" + quote(command.getLabel()) + "}");
            Piece p = command.getCaptured();
            if (p != null) captures.add("{\"type\":" + quote(p.getType().name()) + ",\"color\":" + quote(p.getColor().name()) + "}");
        }
        return "{\"revision\":" + revision + ",\"turn\":" + quote(session.getGame().getTurn().name()) + ",\"status\":" + quote(session.getGame().getStatus())
                + ",\"check\":" + session.getGame().isInCheck() + ",\"finished\":" + session.getGame().isFinished() + ",\"canRedo\":" + session.getGame().canRedo()
                + ",\"whitePoints\":" + game.getCapturedPoints(PieceColor.WHITE)
                + ",\"blackPoints\":" + game.getCapturedPoints(PieceColor.BLACK)
                + ",\"winner\":" + (game.getWinner()==null ? "null" : quote(game.getWinner().name()))
                + ",\"mode\":" + quote(session.getMode().name()) + ",\"difficulty\":" + quote(session.getDifficulty().name())
                + ",\"configured\":" + session.isConfigured() + ",\"aiTurn\":" + session.isAiTurn()
                + ",\"elapsedMillis\":" + session.getElapsedMillis()
                + ",\"clockRunning\":" + session.isClockRunning()
                + ",\"board\":" + squares + ",\"history\":" + history + ",\"captures\":" + captures + "}";
    }
    private void staticFile(HttpExchange exchange) throws IOException {
        try {
            if (!exchange.getRequestMethod().equals("GET")) { send(exchange, 405, "text/plain", "Método no permitido"); return; }
            String request = exchange.getRequestURI().getPath();
            Path file = webRoot.resolve(request.equals("/") ? "index.html" : request.substring(1)).normalize();
            if (!file.startsWith(webRoot) || !Files.isRegularFile(file)) { send(exchange, 404, "text/plain", "No encontrado"); return; }
            String name = file.toString();
            String mime = name.endsWith(".html") ? "text/html" : name.endsWith(".css") ? "text/css" : name.endsWith(".js") ? "text/javascript"
                    : name.endsWith(".svg") ? "image/svg+xml" : name.endsWith(".png") ? "image/png" : "application/octet-stream";
            exchange.getResponseHeaders().set("Content-Type", mime + (mime.startsWith("text/") ? "; charset=utf-8" : ""));
            exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
            byte[] data = Files.readAllBytes(file);
            exchange.sendResponseHeaders(200, data.length);
            exchange.getResponseBody().write(data);
        } finally { exchange.close(); }
    }
    private static Map<String, String> parse(String raw) {
        Map<String, String> params = new HashMap<>();
        if (raw == null || raw.isEmpty()) return params;
        for (String pair : raw.split("&")) {
            String[] parts = pair.split("=", 2);
            params.put(URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                    parts.length > 1 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "");
        }
        return params;
    }
    private static String quote(String text) {
        return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + "\"";
    }
    private static void send(HttpExchange exchange, int status, String mime, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", mime + "; charset=utf-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
    }
    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        Path saveFile=Paths.get(args.length>1 ? args[1] : "data/current-match.properties");
        PieceFactory factory=new PieceFactory();
        CheckDetector detector=new CheckDetector();
        IChessAi ai=new MinimaxAi(new MoveValidator(detector,factory),detector,factory,new Random());
        IMatchService session=new MatchSession(StandardGame::create,new FileGameRepository(saveFile),ai,Clock.systemUTC());
        new WebServer(session, Paths.get("web")).start(port);
    }
}

