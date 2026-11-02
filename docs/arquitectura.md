# Arquitectura actual

Arquitectura de la copia de trabajo basada en main (`38f2635`), con Memento incorporado el 2/11/2026. Las campañas históricas y el estado de revisión están enlazados desde el [índice](README.md).

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
| POST | /api/presence | clientId, active; no usa revision |

El estado incluye tablero, turno, historial, capturas, puntos, ganador o null, modo, dificultad, turno de IA y tiempo transcurrido.

Las operaciones se serializan. Una revisión desactualizada devuelve HTTP 409, un formato inválido HTTP 400 y una jugada ilegal accepted=false sin cambiar el estado.

## Persistencia

SavedMatch guarda la secuencia de movimientos y el cursor. MatchSession reconstruye las jugadas con el mismo validador y luego deshace hasta el cursor. Así conserva capturas, promociones, enroque, al paso, repetición y redo.

FileGameRepository escribe un archivo temporal y reemplaza el guardado. Se utiliza reemplazo atómico cuando el sistema de archivos lo admite. Si falla, la sesión revierte la operación. No se coordinan varios procesos sobre el mismo archivo.

El cronómetro acumula tiempo sólo cuando existe al menos una vista visible. WebServer recibe POST /api/presence con clientId y active, sin modificar la revisión del tablero. MatchSession contabiliza intervalos con Clock inyectado y actividad que vence a los 15 segundos; varias vistas no duplican el tiempo. La vista renueva su actividad cada 5 segundos y avisa al ocultarse o salir. El tiempo se guarda como elapsedMillis (formato 2) y se restaura en pausa, sin añadir el tiempo con Java cerrado. Los archivos de formato 1 se migran tomando su última escritura como referencia para las partidas no terminadas.

## Separación de responsabilidades

Game.getCapturedPoints calcula capturas del historial actual y getWinner sólo devuelve un color ante jaque mate. La corona es su representación gráfica. PieceType.getPoints devuelve null para el rey.

MinimaxAi usa el mismo MoveValidator. En TWO_PLAYERS la sesión no llama a la IA. En solo se agrupa deshacer/rehacer para volver al turno humano.

El tutorial es ilustrativo y no modifica la partida. La consola sigue siendo un adaptador básico sin los controles web de sesión. El informe Word documenta las clases y las decisiones con más detalle.


## Contratos del núcleo

El [UML editable](UML-Ajedrez-TPO.puml) y su [PDF](UML-Ajedrez-TPO.pdf) muestran seis vistas de 38 tipos públicos (37 tipos superiores y Board.Memento anidado), con 196 miembros públicos declarados.

- `StandardGame.create()` devuelve `Game` y conecta Board, PieceFactory, CheckDetector y MoveValidator.
- `Game` recibe `(Board, PieceColor, MoveValidator, CheckDetector, PieceFactory)` y copia el tablero recibido.
- `Board` conserva `SIZE = 8`, `get`, `set`, `copy`, `restore`, al paso y contador de medias jugadas.
- `Position` y `Move` son clases finales, no records. Sus accesores son `getRow/getCol` y `getFrom/getTo/getPromotion`.
- `Piece` recibe `(PieceType, PieceColor, IMovementStrategy)`. Las estrategias reciben el color en cada llamada. Sólo PawnMovement redefine `canAttack`; QueenMovement hereda el método por defecto.
- `MoveValidator.validate(Board, Move, PieceColor)` devuelve `ValidationResult`. El enum incluye VALID y nueve rechazos semánticos, sin texto de UI.
- `MoveResult.getValidationResult()` devuelve un Optional con el motivo cuando el validador rechaza. `MoveMessages.message` lo traduce en los adaptadores. `getMessage` conserva mensajes de operaciones previas y queda vacío para estos rechazos tipados.
- `Game.getHistory()` devuelve MoveRecord inmutables. Command reside en MoveCommand, que implementa execute/undo; redo reutiliza execute.

## Decisiones de diseño

| Qué | Por qué | Cuándo cambiaría |
|---|---|---|
| Conservar Board y Position para 8×8 | El producto implementa ajedrez estándar y el docente acepta esta restricción. Generalizar ahora agrega API sin un caso de uso real. | Una variante concreta exige dimensiones distintas y reglas acordadas. |
| Conservar PieceType y PieceFactory con EnumMap | El catálogo finito permite leer toda la configuración en un lugar. Piece delega el comportamiento a IMovementStrategy. | Se necesita cargar tipos externos en ejecución o mantener muchas variantes. |
| ValidationResult como enum con isValid | El núcleo comunica el motivo sin imponer texto visible. Los rechazos normales no usan excepciones. | Los errores necesitan datos adicionales, por ejemplo una casilla o parámetros para traducción; entonces podría usarse un resultado con motivo y datos. |
| MoveMessages compartido por web y consola | Evita duplicar traducciones y mantiene la dependencia desde app hacia core. | Los adaptadores requieren idiomas o estilos distintos. |
| Memento opaco en MoveCommand | Board captura/restaura su estado; el comando no puede leer ni modificar su representación. | Un historial muy grande o tableros mayores justificarían, mediante mediciones, almacenar diferencias. |
| Puertos en las fronteras e inyección manual | Repositorio, IA y reloj se sustituyen en pruebas; no hay contenedor de DI ni dependencias de infraestructura en core. | Nuevas integraciones o responsabilidades concretas requieren otros puertos. |
| Guardas en pathClear | Una trayectoria no alineada podía no terminar. La precondición ahora devuelve false y está probada. | Otro algoritmo admite trayectorias diferentes; debe tener su contrato propio. |

No se agregaron State, Observer, herencia de piezas ni plugins. La revisión conserva Java 21, Maven y JUnit 5 sin nuevas dependencias de aplicación.

## Agregar una pieza

1. Implementar IMovementStrategy con canMove; redefinir canAttack sólo si atacar y mover difieren. No mutar Board dentro de una estrategia.
2. Agregar el identificador y metadatos en PieceType y registrar la estrategia en PieceFactory.
3. Incorporarla al setup elegido y adaptar su símbolo de consola, personaje web y tutorial si se ofrece al usuario.
4. Probar su movimiento, ataques, bloqueos y seguridad del rey. La prueba stub existente demuestra la sustitución del comportamiento de Piece.

OCP se aplica al comportamiento variable, no a un catálogo universal sin cambios. La promoción admite explícitamente reina, torre, alfil y caballo en MoveValidator y MinimaxAi; una pieza nueva promocionable requiere cambiar esas reglas y la UI. Los tipos con efectos especiales requieren revisar MoveCommand y la clave de repetición. Una pieza ordinaria desconocida no debe clasificarse automáticamente como material insuficiente. Estos cambios explícitos mantienen el alcance del ajedrez estándar.

## Adaptar el tablero en una futura variante

Primero se definen el setup y las reglas de esa variante. Después se introducen dimensiones por instancia en Board, se adaptan contains/copy/restore y los recorridos de Game, CheckDetector y MinimaxAi. Position necesita conversión algebraica con dimensiones explícitas; no alcanza con cambiar SIZE.

También deben revisarse las filas de peones, promoción, enroque y derechos de repetición, material insuficiente y evaluación de IA. StandardGame puede seguir creando únicamente ajedrez estándar. Consola, JSON, web y guardado requieren acordar cómo representar la nueva configuración. El enroque clásico no debe extrapolarse automáticamente a otro tablero. Estas modificaciones quedan documentadas, no implementadas anticipadamente.

## Command y Memento

Board es el originador: `createMemento()` captura las 64 casillas, la casilla de al paso y el contador de medias jugadas; `restoreMemento(Memento)` los restaura. `Board.Memento` es una clase estática final con constructor y campos privados, sin métodos de lectura ni mutación públicos. Su matriz se copia y solo comparte referencias a Piece y Position, que son inmutables.

MoveCommand es el custodio: conserva el memento anterior en su constructor y lo entrega a Board al deshacer. Command encapsula la acción; Memento encapsula el estado previo. El turno y las pilas siguen bajo responsabilidad de Game; no se trasladan al tablero. Rehacer reutiliza execute. Restaurar no consume el memento ni comparte su matriz con el tablero.

Board.copy()/restore(Board) siguen disponibles para copias mutables de simulación y consultas. El memento es un estado en memoria para undo, no el formato persistente SavedMatch.

## Costos y rendimiento

Board consulta casillas por índice. La validación rechaza errores simples antes de simular la jugada; cada comando conserva O(n) referencias y el historial O(m × n), con n casillas y m jugadas. Las piezas son inmutables y se comparten en las copias. El navegador calcula destinos a demanda y reutiliza gráficos Canvas. Las mediciones y sus límites se mantienen en [rendimiento](rendimiento.md).

## Verificación

Las seis suites reúnen 65 pruebas, incluidos perft 20/400/8902, validación tipada, trayectorias y reloj activo. [Calidad](calidad-y-metricas.md) conserva resultados y [la matriz](matriz-requisitos-pruebas.md) relaciona escenarios con requisitos.

El núcleo no importa app, HTTP, UI ni archivos. Permanece texto en Game/MatchSession, etiquetas de tipos y notación del historial: la separación tipada se limita a MoveValidator y no implica internacionalización completa. La revisión independiente pendiente figura en [integridad](integridad-y-cierre.md).
