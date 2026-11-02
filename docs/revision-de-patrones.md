# Revisión de patrones de BLOCKMATE

Diseño del código integrado en main. El contraste UML del 31/10/2026 se conserva como [evidencia histórica](evidencias/2026-10-31-revision-uml.md).

## Resultado de la revisión

Strategy, Command y Simple Factory se conservan. Memento se incorpora el 2/11/2026 para encapsular el estado que Command ya necesitaba al deshacer. No se identifica una necesidad actual que justifique agregar Decorator, Proxy, State u Observer para cumplir una cantidad de patrones. La decisión debe revisarse si cambia el alcance o aparece evidencia de un problema.

La arquitectura de puertos y adaptadores organiza dependencias. La inyección por constructor conecta colaboradores. Son conceptos distintos de los tres patrones de diseño revisados.

## Strategy para los movimientos

**Problema:** cada pieza tiene reglas diferentes. [Piece](../src/main/java/ar/edu/ajedrez/core/pieces/Piece.java) recibe [IMovementStrategy](../src/main/java/ar/edu/ajedrez/core/movement/IMovementStrategy.java) por constructor y delega `canMove` y `canAttack`.

**Alternativa sencilla:** un condicional por tipo dentro de Piece. Centralizaría las seis reglas y dificultaría probarlas por separado.

**Beneficio:** permite sustituir el comportamiento conservando el contrato de la pieza. QueenMovement compone los movimientos de torre y alfil; PawnMovement redefine ataque porque no coincide con avance. El método `canAttack` por defecto pertenece a la interfaz, no es una redefinición de QueenMovement.

**Costo y límite:** requiere interfaz y seis clases. Agregar una pieza exige también configurar PieceType, PieceFactory y su presentación; no significa que todo el sistema pueda extenderse sin modificar archivos.

**Decisión:** conservar. Reconsiderar si el producto pasa a tener un único comportamiento fijo.

**Pruebas existentes:** [ChessTest](../src/test/java/ar/edu/ajedrez/ChessTest.java), escenarios 2 a 8 de movimientos y `scenario9`, que inyecta un stub para comprobar la delegación de Piece.

## Command para ejecutar y deshacer

**Problema:** restaurar exactamente una jugada, incluidas capturas y reglas especiales. [MoveCommand](../src/main/java/ar/edu/ajedrez/core/game/MoveCommand.java) implementa [ICommand](../src/main/java/ar/edu/ajedrez/core/game/ICommand.java); conserva un Board.Memento opaco del tablero anterior. [Game](../src/main/java/ar/edu/ajedrez/core/game/Game.java) administra el historial y la pila de rehacer.

**Alternativa sencilla:** reescribir la lógica inversa de cada movimiento dentro de Game. Aumentaría la duplicación entre capturas, promoción, enroque y al paso.

**Beneficio:** ejecutar y deshacer quedan encapsulados. Rehacer reutiliza `execute`. Move representa la solicitud de datos; el patrón está en MoveCommand.

**Costo y límite:** cada comando conserva un memento con las casillas y metadatos del tablero. El historial crece con el número de jugadas; no consume memoria constante. Para 64 casillas resulta sencillo de comprender.

**Decisión:** conservar. Evaluar almacenar diferencias solo si mediciones y un caso de uso justifican ese cambio.

**Pruebas existentes:** ChessTest `scenario26` verifica execute/undo y `scenario27` la invalidación de rehacer; los escenarios 18 a 25 cubren enroque, al paso y promoción. [FeatureTest](../src/test/java/ar/edu/ajedrez/FeatureTest.java) `scenario2` comprueba puntos tras undo/redo.

## Memento para conservar el estado previo

**Problema:** antes MoveCommand retenía otro Board mutable como estado anterior. El comando dependía de una representación que también ofrecía operaciones de juego. El estado de undo debe conservarse sin que el historial pueda modificarlo ni leer sus detalles.

**Aplicación:** Board es el originador, Board.Memento el estado encapsulado y MoveCommand el custodio. `createMemento()` y `restoreMemento(Memento)` pertenecen a Board. La clase anidada final tiene constructor/campos privados y no expone getters, setters ni arrays.

**Alternativa sencilla:** mantener Board.copy()/restore(Board). Ya resolvía la restauración, pero entregaba un objeto mutable al custodio. Memento hace explícita la frontera de acceso sin cambiar las reglas.

**Beneficio:** Board controla qué estado capturar y restaurar: piezas y marcas de movimiento, casilla de al paso y contador. El comando solo guarda y devuelve el token. La copia es independiente y se puede restaurar varias veces.

**Costo y límite:** suma un tipo anidado y dos métodos. Sigue guardando O(n) referencias por jugada y O(m × n) en el historial; no es una optimización de memoria. Piece y Position son inmutables; sus referencias se comparten. No incluye turno, pilas, puntos de sesión ni disco, que pertenecen a otras responsabilidades.

**Pruebas:** BoardMementoTest verifica contenido completo, independencia/reutilización y rechazo de null sin mutación. Las pruebas existentes de Command y reglas especiales vuelven a ejecutarse. [Resultados y API](evidencias/2026-11-02-memento/README.md).

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
| Decorator sobre IChessAi | Medir cada respuesta sin mezclar métricas con minimax | Medir llamadas en un escenario de rendimiento aislado | Instrumentación reutilizable | Clase adicional, destino de métricas y pruebas del contrato | No agregar: la campaña de rendimiento se resolvió con instrumentos aislados; reevaluar si se requieren métricas permanentes |
| Proxy sobre IChessAi | Controlar acceso a un recurso o integración externa | Llamada al puerto local actual | Controlar un recurso real | Indirección y gestión de fallos | No agregar: no existe esa integración ni control de acceso requerido |
| State | Gestionar estados con comportamientos y transiciones complejas | Estado calculado por Game y validaciones actuales | Aislar transiciones si crece el flujo | Más clases y coordinación | No agregar: los estados actuales no justifican ese costo |
| Observer | Notificar a varios suscriptores independientes | Actualizar la interfaz con la respuesta HTTP | Desacoplar consumidores de eventos | Suscripciones, orden y liberación de referencias | No agregar: el núcleo no necesita un sistema de suscripciones actual |

Si el grupo acepta un patrón nuevo, debe registrar el requisito concreto, implementar y probar su comportamiento y actualizar UML e informe. No se incorporan Decorator, Proxy, State u Observer; Memento se aplica al undo existente.

## Verificación

Los métodos citados forman parte de la suite actual de 65 pruebas. Los resultados de ejecución se registran en [calidad](calidad-y-metricas.md), sin duplicarlos como una campaña nueva de revisión de patrones. El contraste actual de 38 tipos y 196 miembros públicos está en la [evidencia de Memento](evidencias/2026-11-02-memento/README.md). El registro UML del 31/10 conserva la API anterior.

El [estado de revisión](integridad-y-cierre.md) identifica los acuerdos del grupo aún no registrados. Si se incorpora un patrón, registrar el requisito, probar su comportamiento y actualizar UML e informe.
