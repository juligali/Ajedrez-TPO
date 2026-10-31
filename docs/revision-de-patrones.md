# Revisión de patrones de BLOCKMATE

Revisión técnica del 31 de octubre de 2026 sobre `d90061e81c85b010f1a691bd7797cd20f55fc16d`. Rama: `RAMA-VICTORIA`. Presentación: 6 de noviembre de 2026.

## Resultado de la revisión

Strategy, Command y Simple Factory ya tienen usos concretos en el código. Se propone conservarlos. No se identifica una necesidad actual que justifique agregar Decorator, Proxy, State u Observer para cumplir una cantidad de patrones. La decisión debe revisarse si cambia el alcance o aparece evidencia de un problema.

La arquitectura de puertos y adaptadores organiza dependencias. La inyección por constructor conecta colaboradores. Son conceptos distintos de los tres patrones de diseño revisados.

## Strategy para los movimientos

**Problema:** cada pieza tiene reglas diferentes. [Piece](../src/main/java/ar/edu/ajedrez/core/pieces/Piece.java) recibe [IMovementStrategy](../src/main/java/ar/edu/ajedrez/core/movement/IMovementStrategy.java) por constructor y delega `canMove` y `canAttack`.

**Alternativa sencilla:** un condicional por tipo dentro de Piece. Centralizaría las seis reglas y dificultaría probarlas por separado.

**Beneficio:** permite sustituir el comportamiento conservando el contrato de la pieza. QueenMovement compone los movimientos de torre y alfil; PawnMovement redefine ataque porque no coincide con avance. El método `canAttack` por defecto pertenece a la interfaz, no es una redefinición de QueenMovement.

**Costo y límite:** requiere interfaz y seis clases. Agregar una pieza exige también configurar PieceType, PieceFactory y su presentación; no significa que todo el sistema pueda extenderse sin modificar archivos.

**Decisión:** conservar. Reconsiderar si el producto pasa a tener un único comportamiento fijo.

**Pruebas existentes:** [ChessTest](../src/test/java/ar/edu/ajedrez/ChessTest.java), escenarios 2 a 8 de movimientos y `scenario9`, que inyecta un stub para comprobar la delegación de Piece.

## Command para ejecutar y deshacer

**Problema:** restaurar exactamente una jugada, incluidas capturas y reglas especiales. [MoveCommand](../src/main/java/ar/edu/ajedrez/core/game/MoveCommand.java) implementa [ICommand](../src/main/java/ar/edu/ajedrez/core/game/ICommand.java); conserva una copia del tablero anterior. [Game](../src/main/java/ar/edu/ajedrez/core/game/Game.java) administra el historial y la pila de rehacer.

**Alternativa sencilla:** reescribir la lógica inversa de cada movimiento dentro de Game. Aumentaría la duplicación entre capturas, promoción, enroque y al paso.

**Beneficio:** ejecutar y deshacer quedan encapsulados. Rehacer reutiliza `execute`. Move representa la solicitud de datos; el patrón está en MoveCommand.

**Costo y límite:** cada comando conserva una copia del tablero y metadatos. El historial crece con el número de jugadas; no consume memoria constante. Para 64 casillas resulta sencillo de comprender.

**Decisión:** conservar. Evaluar almacenar diferencias solo si mediciones y un caso de uso justifican ese cambio.

**Pruebas existentes:** ChessTest `scenario26` verifica execute/undo y `scenario27` la invalidación de rehacer; los escenarios 18 a 25 cubren enroque, al paso y promoción. [FeatureTest](../src/test/java/ar/edu/ajedrez/FeatureTest.java) `scenario2` comprueba puntos tras undo/redo.

## Simple Factory para crear piezas

**Problema:** construir cada tipo con su estrategia tanto al iniciar como al promocionar. [PieceFactory](../src/main/java/ar/edu/ajedrez/core/pieces/PieceFactory.java) centraliza esa selección con un EnumMap y comparte estrategias sin estado.

**Alternativa sencilla:** construir cada pieza y estrategia en cada punto de uso. Duplicaría la configuración.

**Beneficio:** [StandardGame.create](../src/main/java/ar/edu/ajedrez/app/StandardGame.java) y MoveCommand reutilizan la creación.

**Costo y límite:** el catálogo es explícito; un tipo nuevo requiere registrar su estrategia y metadatos. El diseño no carga piezas arbitrarias en ejecución.

**Decisión:** conservar como **Simple Factory**. No llamarlo Factory Method: no hay una jerarquía de creadores que redefina el método de creación. Tampoco hay familias de fábricas Abstract Factory.

**Pruebas existentes:** ChessTest `scenario10` comprueba la disposición inicial y `scenario24`/`scenario25` las promociones. Son comprobaciones de integración de sus usos, no una suite aislada exhaustiva de la fábrica.

## Arquitectura e inyección de dependencias

WebServer depende de IMatchService y ConsoleUI de IGameService. MatchSession recibe un proveedor de partidas, IGameRepository, IChessAi y Clock. FileGameRepository y MinimaxAi implementan puertos de salida. StandardGame conecta el núcleo mediante constructores; no existe un contenedor de DI.

Las piezas y QueenMovement utilizan composición. Estas decisiones favorecen pruebas con colaboradores sustituibles: FeatureTest usa repositorio en memoria, reloj controlado y una IA sustituible. Arquitectura hexagonal, DI y composición no se cuentan como nuevas implementaciones de Strategy, Command o Factory Method.

## Evaluación de patrones adicionales

| Opción | Problema que podría resolver | Alternativa sencilla | Beneficio potencial | Costo | Decisión propuesta |
|---|---|---|---|---|---|
| Decorator sobre IChessAi | Medir cada respuesta sin mezclar métricas con minimax | Medir llamadas en un escenario de rendimiento aislado | Instrumentación reutilizable | Clase adicional, destino de métricas y pruebas del contrato | Diferir hasta que la etapa 5 demuestre necesidad de instrumentación permanente |
| Proxy sobre IChessAi | Controlar acceso a un recurso o integración externa | Llamada al puerto local actual | Controlar un recurso real | Indirección y gestión de fallos | No agregar: no existe esa integración ni control de acceso requerido |
| State | Gestionar estados con comportamientos y transiciones complejas | Estado calculado por Game y validaciones actuales | Aislar transiciones si crece el flujo | Más clases y coordinación | No agregar: los estados actuales no justifican ese costo |
| Observer | Notificar a varios suscriptores independientes | Actualizar la interfaz con la respuesta HTTP | Desacoplar consumidores de eventos | Suscripciones, orden y liberación de referencias | No agregar: el núcleo no necesita un sistema de suscripciones actual |

Si el grupo acepta un patrón nuevo, debe registrar el requisito concreto, implementar y probar su comportamiento y actualizar UML e informe. Esta revisión no incorpora ninguno.

## Correspondencia entre UML y programa

El docente pidió que los diagramas coincidan con las clases. Se contrastó [el UML](UML-Ajedrez-TPO.puml) con los **37 tipos públicos de producción** y **194 miembros públicos declarados o constructores implícitos**, obtenidos al compilar la base y consultar `javap -public`. Se excluyen métodos heredados, miembros privados y `values/valueOf` generados para enums.

Las diferencias detectadas en la versión de GitHub eran:

- IMatchService y MatchSession no mostraban `presence(String, boolean)` ni `isClockRunning()`.
- SavedMatch no mostraba `elapsedMillis` y su constructor todavía tenía siete parámetros, en lugar de ocho.
- Se agregó una aclaración sobre `canAttack` por defecto y su redefinición en PawnMovement.

Se corrigieron esas declaraciones y se regeneró el [PDF](UML-Ajedrez-TPO.pdf) desde la misma fuente, manteniendo seis vistas. Las relaciones principales se contrastaron con las dependencias del código. Los nodos sin miembros en una vista son referencias a tipos desarrollados en otra vista o tipos del JDK.

## Verificación y límites

- Código de producción compilado en una carpeta temporal con JDK 21.0.6 y codificación UTF-8.
- API pública contrastada con `javap -public`: 37 tipos y 194 miembros; ninguna declaración pendiente tras la corrección.
- Fuente renderizada con PlantUML 1.2026.9; PDF de seis páginas revisado visualmente.
- No se modificó código de producción ni tests.
- Se localizaron las pruebas citadas, pero no se ejecutó Maven en esta revisión. Los 62 métodos de prueba existentes no se presentan como 62 pruebas aprobadas hoy.
- No se hicieron mediciones de rendimiento ni revisión nueva del navegador.
- La correspondencia revisada alcanza el archivo PlantUML y su PDF. Revisar cualquier diagrama reproducido dentro del informe Word o las diapositivas antes de la presentación.
- La revisión por otro integrante y la aceptación grupal de las decisiones siguen pendientes; ver [alcance y criterios](alcance-y-criterios.md).

Para repetir el contraste sobre otra versión: registrar el nuevo commit, compilar sus fuentes, comparar las declaraciones públicas, revisar relaciones, renderizar la fuente actualizada y comprobar cada página del PDF. Conservar los resultados de ejecuciones anteriores identificados por su propia fecha y commit.
