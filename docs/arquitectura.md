# Arquitectura actual

El núcleo de reglas se mantiene separado de HTTP, gráficos y archivos.

## Entrada y salida

- **WebServer → IMatchService → MatchSession**: entrada web de sesión.
- **ConsoleUI → IGameService → Game**: entrada de consola básica.
- **MatchSession → IGameService**: coordina las reglas.
- **MatchSession → IGameRepository ← FileGameRepository**: salida de persistencia.
- **MatchSession → IChessAi ← MinimaxAi**: elección sustituible de una jugada.
- **MatchSession → Clock**: tiempo inyectado mediante abstracción estándar.

Game contiene Board y usa MoveValidator, CheckDetector y MoveCommand. Piece delega en IMovementStrategy. QueenMovement compone las estrategias de torre y alfil. PieceFactory configura la creación.

## Contrato HTTP

| Método | Ruta | Parámetros |
|---|---|---|
| GET | /api/state | Ninguno |
| GET | /api/legal | from |
| POST | /api/new | revision, mode, difficulty |
| POST | /api/move | revision, from, to, promotion opcional |
| POST | /api/ai | revision |
| POST | /api/undo | revision |
| POST | /api/redo | revision |
| POST | /api/save | revision |

El estado incluye tablero, turno, historial, capturas, puntos, ganador o null, modo, dificultad, turno de IA y tiempo transcurrido.

Las operaciones se serializan. Una revisión desactualizada devuelve HTTP 409, un formato inválido HTTP 400 y una jugada ilegal accepted=false sin cambiar el estado.

## Persistencia

SavedMatch guarda la secuencia de movimientos y el cursor. MatchSession reconstruye las jugadas con el mismo validador y luego deshace hasta el cursor. Así conserva capturas, promociones, enroque, al paso, repetición y redo.

FileGameRepository escribe un archivo temporal y reemplaza el guardado. Se utiliza reemplazo atómico cuando el sistema de archivos lo admite. Si falla, la sesión revierte la operación. No se coordinan varios procesos sobre el mismo archivo.

El cronómetro cuenta desde la creación hasta el fin, también durante cierres. Clock se inyecta para poder probar el tiempo sin esperas.

## Separación de responsabilidades

Game.getCapturedPoints calcula capturas del historial actual y getWinner sólo devuelve un color ante jaque mate. La corona es su representación gráfica. PieceType.getPoints devuelve null para el rey.

MinimaxAi usa el mismo MoveValidator. En TWO_PLAYERS la sesión no llama a la IA. En solo se agrupa deshacer/rehacer para volver al turno humano.

El tutorial es ilustrativo y no modifica la partida. La consola sigue siendo un adaptador básico sin los controles web de sesión. El informe Word documenta las clases y las decisiones con más detalle.
