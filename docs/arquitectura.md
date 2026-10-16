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


## Revisión final y contratos vigentes

Fecha de revisión: 16 de octubre de 2026. La fuente de verdad es `src/main/java`, contrastada con los tests. El UML anterior describía otra API; se reemplazó por seis vistas de los 37 tipos públicos actuales. [Fuente PlantUML](UML-Ajedrez-TPO.puml) y [PDF](UML-Ajedrez-TPO.pdf).

- `StandardGame.create()` devuelve `Game` y conecta Board, PieceFactory, CheckDetector y MoveValidator.
- `Game` recibe `(Board, PieceColor, MoveValidator, CheckDetector, PieceFactory)` y copia el tablero recibido.
- `Board` conserva `SIZE = 8`, `get`, `set`, `copy`, `restore`, al paso y contador de medias jugadas.
- `Position` y `Move` son clases finales, no records. Sus accesores son `getRow/getCol` y `getFrom/getTo/getPromotion`.
- `Piece` recibe `(PieceType, PieceColor, IMovementStrategy)`. Las estrategias reciben el color en cada llamada. Sólo PawnMovement redefine `canAttack`; QueenMovement hereda el método por defecto.
- `MoveValidator.validate(Board, Move, PieceColor)` devuelve `ValidationResult`. El enum incluye VALID y nueve rechazos semánticos, sin texto de UI.
- `MoveResult.getValidationResult()` devuelve un Optional con el motivo cuando el validador rechaza. `MoveMessages.message` lo traduce en los adaptadores. `getMessage` conserva mensajes de operaciones previas y queda vacío para estos rechazos tipados.
- `Game.getHistory()` devuelve MoveRecord inmutables. Command reside en MoveCommand, que implementa execute/undo; redo reutiliza execute.

## Decisiones para defender

| Qué | Por qué | Cuándo cambiaría |
|---|---|---|
| Conservar Board y Position para 8×8 | El producto implementa ajedrez estándar y el docente acepta esta restricción. Generalizar ahora agrega API sin un caso de uso real. | Una variante concreta exige dimensiones distintas y reglas acordadas. |
| Conservar PieceType y PieceFactory con EnumMap | El catálogo finito permite leer toda la configuración en un lugar. Piece delega el comportamiento a IMovementStrategy. | Se necesita cargar tipos externos en ejecución o mantener muchas variantes. |
| ValidationResult como enum con isValid | El núcleo comunica el motivo sin imponer texto visible. Los rechazos normales no usan excepciones. | Los errores necesitan datos adicionales, por ejemplo una casilla o parámetros para traducción; entonces podría usarse un resultado con motivo y datos. |
| MoveMessages compartido por web y consola | Evita duplicar traducciones y mantiene la dependencia desde app hacia core. | Los adaptadores requieren idiomas o estilos distintos. |
| Copia del tablero en MoveCommand | Hace explícita la restauración de capturas, derechos y contadores para 64 casillas. | Un historial muy grande o tableros mayores justifican, mediante mediciones, almacenar diferencias. |
| Puertos en las fronteras e inyección manual | Repositorio, IA y reloj se sustituyen en pruebas; no hay contenedor de DI ni dependencias de infraestructura en core. | Nuevas integraciones o responsabilidades concretas requieren otros puertos. |
| Guardas en pathClear | Una trayectoria no alineada podía no terminar. La precondición ahora devuelve false y está probada. | Otro algoritmo admite trayectorias diferentes; debe tener su contrato propio. |

No se agregaron State, Observer, herencia de piezas ni plugins. La revisión conserva Java 21, Maven y JUnit 5 sin nuevas dependencias de aplicación.

## Agregar una pieza

1. Implementar IMovementStrategy con canMove; redefinir canAttack sólo si atacar y mover difieren. No mutar Board dentro de una estrategia.
2. Agregar el identificador y metadatos en PieceType y registrar la estrategia en PieceFactory.
3. Incorporarla al setup elegido y adaptar su símbolo de consola, personaje web y tutorial si se ofrece al usuario.
4. Probar su movimiento, ataques, bloqueos y seguridad del rey. La prueba stub existente demuestra la sustitución del comportamiento de Piece.

OCP se aplica al comportamiento variable, no a un catálogo universal sin cambios. La promoción admite explícitamente reina, torre, alfil y caballo en MoveValidator y MinimaxAi; una pieza nueva promocionable requiere cambiar esas reglas y la UI. Los tipos con efectos especiales requieren revisar MoveCommand y la clave de repetición. Una pieza ordinaria desconocida no debe clasificarse automáticamente como material insuficiente. Este límite es más sencillo de explicar que un motor genérico de variantes.

## Adaptar el tablero en una futura variante

Primero se definen el setup y las reglas de esa variante. Después se introducen dimensiones por instancia en Board, se adaptan contains/copy/restore y los recorridos de Game, CheckDetector y MinimaxAi. Position necesita conversión algebraica con dimensiones explícitas; no alcanza con cambiar SIZE.

También deben revisarse las filas de peones, promoción, enroque y derechos de repetición, material insuficiente y evaluación de IA. StandardGame puede seguir creando únicamente ajedrez estándar. Consola, JSON, web y guardado requieren acordar cómo representar la nueva configuración. El enroque clásico no debe extrapolarse automáticamente a otro tablero. Estas modificaciones quedan documentadas, no implementadas anticipadamente.

## Verificación y límites

`mvn clean test`: 53 pruebas, sin fallos ni errores. Se conservan los 47 escenarios existentes, incluido perft 20/400/8902. ValidationTest cubre todos los motivos, propagación, invariantes y traducción; BoardPathTest cubre el caso que podía no terminar y extremos inválidos. La única integración de archivos conserva @TempDir; los tests nuevos no usan archivos, red ni navegador.

La separación de dependencias es real: core no importa app, HTTP, UI ni archivos. Permanece texto en Game/MatchSession, etiquetas de tipos y notación descriptiva del historial. Se acepta ese límite de presentación para evitar una migración de todos los contratos: la separación tipada se acota a MoveValidator. La IA es didáctica y sus presupuestos no constituyen una garantía de tiempo máximo de respuesta.


## Archivos de esta revisión

Código de producción, dentro de `src/main/java/ar/edu/ajedrez`:

- `core/board/Board.java`: guarda de trayectorias inválidas.
- `core/validation/MoveValidator.java` y nuevo `ValidationResult.java`: contrato semántico.
- `core/game/Game.java` y `MoveResult.java`: consumo y propagación del motivo.
- `core/ai/MinimaxAi.java`: consumo del nuevo contrato.
- `app/ConsoleUI.java`, `WebServer.java` y nuevo `MoveMessages.java`: traducción de presentación.

Pruebas, dentro de `src/test/java/ar/edu/ajedrez`:

- `ChessTest.java` y `FeatureTest.java`: adaptación al contrato, conservando los 47 escenarios.
- Nuevos `ValidationTest.java` y `BoardPathTest.java`: seis pruebas adicionales.

Documentación y entrega:

- `README.md`, `docs/arquitectura.md`, `docs/requisitos.md`.
- `docs/UML-Ajedrez-TPO.puml` y `docs/UML-Ajedrez-TPO.pdf`.
- `docs/Informe arquitectura Ajedrez Minecraft.docx` y nuevo `docs/BLOCKMATE-final.pptx`.
- Nueva `.gitattributes`: marca PDF, DOCX y PPTX como binarios para evitar conversiones de fin de línea en Git.

No se modificaron la UI web, pom.xml, el catálogo de piezas ni Position. Los experimentos de dimensiones y PieceKind se retiraron después de evaluar la aclaración del docente. La carpeta .idea ya existía sin seguimiento y no forma parte de esta revisión.
