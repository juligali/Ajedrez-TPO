# Ajedrez TPO — Ingeniería de Software


> El núcleo de movimientos está implementado y probado. La validación completa, la detección de jaque y la aplicación jugable siguen en desarrollo. Las secciones de arquitectura describen tanto el código existente como las responsabilidades previstas, identificadas como pendientes.

## Estado actual

- [x] Java 21, Maven y JUnit 5 configurados.
- [x] Tablero, posiciones, piezas, colores y resultados de jugadas.
- [x] Las seis estrategias de movimiento implementadas.
- [x] Camino libre compartido mediante `PathClearChecker`.
- [x] Copias del tablero para aislar el estado de `Game`.
- [x] Tests de las seis estrategias: 105 casos aprobados en la última ejecución.
- [ ] Detección de jaque: `CheckDetector`.
- [ ] Validación completa: `MoveValidator`.
- [ ] Ejecución de jugadas, capturas y alternancia de turnos: `Game.move()`.
- [ ] Partida inicial con 32 piezas: `StandardGame.create()`.
- [ ] Consola y método `main`.
- [ ] Actualizar el UML con `PathClearChecker` y regenerar su PDF.
- [ ] Revisar las dependencias sobrantes de TestNG y JUnit 4 en `pom.xml`.

Todavía no se puede jugar una partida completa. Los métodos pendientes lanzan `UnsupportedOperationException`.

## Requisitos y comandos

Se necesita JDK 21 y Maven disponible en la terminal. También se puede ejecutar Maven desde el IDE configurado con JDK 21.

Desde la raíz del proyecto:

```sh
mvn test
```

Maven lee `pom.xml`, resuelve las dependencias, compila el código y ejecuta los tests con JUnit 5 y Surefire. Los archivos generados y los informes quedan en `target/` y `target/surefire-reports/`.

Para compilar sin ejecutar las pruebas:

```sh
mvn compile
```

El comando `mvn` requiere que Maven esté instalado y agregado al PATH; en la última verificación se utilizó una instalación local por su ruta completa. No hay Maven Wrapper en el repositorio.

## Alcance obligatorio

- Tablero de 8×8.
- Peón, torre, caballo, alfil, reina y rey con movimientos válidos.
- Alternancia de turnos.
- Captura de piezas.
- Rechazo de movimientos inválidos.
- Detección de jaque.
- Tests unitarios del núcleo.

## Organización del proyecto

```text
pom.xml                     (configuración de Maven)
src/main/java/ar/edu/ajedrez/
├── core/
│   ├── board/
│   │   ├── Board.java
│   │   └── Position.java
│   ├── pieces/
│   │   ├── Piece.java
│   │   └── PieceColor.java
│   ├── movement/
│   │   ├── IMovementStrategy.java
│   │   ├── PawnMovement.java
│   │   ├── RookMovement.java
│   │   ├── KnightMovement.java
│   │   ├── BishopMovement.java
│   │   ├── QueenMovement.java
│   │   ├── KingMovement.java
│   │   └── PathClearChecker.java
│   ├── game/
│   │   ├── IGameService.java
│   │   ├── Game.java
│   │   ├── Move.java
│   │   └── MoveResult.java
│   └── validation/
│       ├── MoveValidator.java
│       ├── CheckDetector.java
│       ├── ValidationResult.java
│       └── RejectionReason.java     
└── app/
    ├── StandardGame.java
    └── ConsoleUI.java
src/test/java/ar/edu/ajedrez/core/movement/
├── RookMovementTest.java
├── BishopMovementTest.java
├── KnightMovementTest.java
├── KingMovementTest.java
├── PawnMovementTest.java
└── QueenMovementTest.java
docs/
├── UML-Ajedrez-TPO.puml   (fuente del diagrama de clases)
└── UML-Ajedrez-TPO.pdf    (diagrama renderizado)
```

El paquete `core` contiene las reglas y el estado del juego. No depende de consola, interfaz gráfica, bases de datos ni servicios externos.

El paquete `app` contiene el armado de la partida y el adaptador de consola.

## Clases y responsabilidades

### Board

Representa el tablero y guarda las piezas que ocupan sus casillas.

Sus responsabilidades son:

- Conocer las dimensiones del tablero.
- Comprobar si una posición está dentro de sus límites.
- Consultar qué pieza ocupa una casilla.
- Colocar y mover piezas (el destino ocupado queda capturado).
- Indicar las posiciones que ocupan las piezas de un color, para que `CheckDetector` encuentre al rey y a los atacantes.
- Permitir obtener una copia del estado del tablero.
- Compararse por contenido con otro tablero, para que los tests verifiquen el estado antes y después de una jugada

`Board` guarda el estado. La coordinación de turnos y la validación de jugadas corresponden a otras clases.
Sus operaciones exigen posiciones dentro del tablero: quien reciba posiciones de afuera, como `MoveValidator`, consulta `contains` antes.

### Position

Representa una casilla mediante fila y columna.
Permite expresar posiciones con un objeto, evitando pasar números sueltos por todos los métodos.

Es un valor inmutable con igualdad por contenido, así que dos posiciones con la misma fila y columna son la misma casilla. No valida límites: una posición fuera del tablero es un dato legítimo que `MoveValidator` debe poder rechazar con `OUT_OF_BOUNDS`.

### Piece

Representa una pieza concreta de la partida.

Contiene:

- Nombre, solo para mostrar la pieza: ninguna regla depende de él.
- Color.
- Una estrategia de movimiento.
- Un indicador explícito de que es el rey (`isKing()`), necesario para detectar jaque.

Recibe su estrategia por constructor y expone `canMove` y `canAttack`, que delegan en ella las reglas particulares de movimiento y ataque. Así, quien consulta a una pieza no necesita conocer su estrategia.

Sus atributos no cambian después de construirla. Las copias del tablero pueden compartir piezas siempre que las estrategias también sean inmutables y sus consultas no tengan efectos secundarios. Su igualdad es por identidad: cada objeto es una pieza concreta de la partida.

### PieceColor

Es un enum con los valores `WHITE` y `BLACK`.

Se utiliza para representar el color de las piezas y el turno actual.

### IMovementStrategy

Define el contrato de las estrategias de movimiento:

- `canMove(board, from, to)`: comprueba si la pieza puede realizar un movimiento.
- `canAttack(board, from, target)`: comprueba si la pieza amenaza una casilla, esté ocupada o no. Por defecto equivale a `canMove`; `PawnMovement` la sobreescribe por sus ataques diagonales y `QueenMovement` para delegar el ataque en sus dos estrategias.

Movimiento y ataque se distinguen porque el peón avanza hacia adelante y ataca en diagonal.

Las estrategias comprueban las reglas particulares de cada pieza. La protección del propio rey y el rechazo de un destino con pieza propia corresponden a la validación general.

Ninguno de los métodos recibe el color: la estrategia lo obtiene de la pieza que está en `from`. Esa es una precondición del contrato (el origen está dentro del tablero y contiene la pieza dueña de la estrategia), que `MoveValidator` garantiza al verificar los límites y la pieza de origen antes de consultar a la estrategia.

### Estrategias concretas

Cada clase implementa `IMovementStrategy`:

| Clase | Responsabilidad |
|---|---|
| `PawnMovement` | Avance del peón (una casilla, o dos desde su fila inicial) y captura en diagonal. |
| `RookMovement` | Movimiento horizontal y vertical de la torre. |
| `KnightMovement` | Movimiento en L del caballo, que puede saltar obstáculos. |
| `BishopMovement` | Movimiento diagonal del alfil. |
| `QueenMovement` | Combinación de movimientos de torre y alfil. |
| `KingMovement` | Movimiento del rey de una casilla en cualquier dirección. |

Torre y alfil usan `PathClearChecker` para comprobar las casillas intermedias. La reina reutiliza esta comprobación mediante ambas estrategias. El destino no se considera un obstáculo intermedio: la validación general debe rechazar las piezas propias.

`PawnMovement` recibe por constructor su dirección (+1 o -1) y su fila inicial. Cuando se implemente, `StandardGame` creará una instancia por color y las piezas de ese color la comparten, porque no tiene estado mutable. La convención de filas es la de los ejemplos de la cátedra: las blancas empiezan en la fila 1 y avanzan hacia filas mayores; las negras empiezan en la fila 6 y avanzan hacia filas menores.

`QueenMovement` recibe las estrategias de torre y alfil por constructor y reutiliza sus reglas mediante composición, tanto para mover como para atacar.

### Game

Estado: constructor, turno inicial y `snapshot()` implementados; `move()` pendiente.

Su responsabilidad prevista es coordinar la partida.

Sus responsabilidades son:

- Mantener el tablero y el turno actual.
- Recibir solicitudes de movimiento.
- Consultar al validador.
- Aplicar las jugadas aceptadas y las capturas.
- Cambiar el turno después de una jugada válida.
- Consultar si el siguiente jugador está en jaque.
- Devolver el resultado de la jugada.

Recibe sus colaboradores por constructor.

### IGameService

Define las operaciones que el núcleo ofrece a la interfaz:

- Solicitar un movimiento.
- Consultar el turno actual.
- Obtener una copia del tablero.

`Game` implementa este contrato. La interfaz de usuario lo utiliza sin necesitar conocer los detalles internos de la partida.

### Move

Representa una solicitud de movimiento con dos posiciones:

- Origen.
- Destino.

Es un objeto de datos. No implementa el patrón Command: no ejecuta ni deshace acciones.

### MoveResult

Representa la respuesta a una solicitud de movimiento.

Puede informar:

- Si la jugada fue aceptada.
- El motivo de un rechazo, como `RejectionReason` (dato, no texto).
- La pieza capturada, si hubo alguna.
- Si la jugada produjo jaque.

Permite que la interfaz muestre resultados sin incorporar reglas del juego.

### MoveValidator

Estado: `validate()` pendiente. Las comprobaciones y su orden indicados a continuación describen el contrato previsto.

Su responsabilidad es comprobar si una jugada completa es válida.

Verifica:

- Que las posiciones estén dentro del tablero.
- Que haya una pieza en el origen.
- Que la pieza pertenezca al jugador del turno actual.
- Que origen y destino sean distintos.
- Que el destino no contenga una pieza propia.
- Que la estrategia permita el movimiento.
- Que no se intente capturar al rey.
- Que la jugada no deje al propio rey en jaque.

Para comprobar la seguridad del rey, puede simular el movimiento en una copia del tablero y consultar a `CheckDetector`.

`validate` devuelve un `ValidationResult`: indica si la jugada es válida y, si no lo es, el `RejectionReason` que corresponde. Hay un motivo por cada regla de la lista anterior (`OUT_OF_BOUNDS`, `NO_PIECE_AT_ORIGIN`, `NOT_PLAYERS_TURN`, `SAME_SQUARE`, `OWN_PIECE_AT_DESTINATION`, `ILLEGAL_MOVEMENT`, `KING_CAPTURE`, `LEAVES_KING_IN_CHECK`). Las reglas se evalúan en ese orden y se informa la primera que falla.

### CheckDetector

Estado: detección pendiente.

Su responsabilidad es determinar si el rey de un color está amenazado.

Localiza al rey (mediante `Piece.isKing()`) y consulta las estrategias de ataque de las piezas enemigas.

Se mantiene separado de `MoveValidator` para poder probarlo de manera independiente y reutilizarlo tanto en la validación como en la consulta del estado de la partida.

### StandardGame

Estado: `create()` pendiente.

Deberá armar la partida estándar de 8×8.

Crea y conecta:

- Tablero.
- Piezas.
- Estrategias.
- Detector de jaque.
- Validador.
- Partida.

Es el punto de composición de la aplicación: concentra la creación de implementaciones concretas y la conexión de dependencias por constructor.

### ConsoleUI

Estado: `run()` pendiente. También falta el método `main` que inicia la aplicación.

Es el adaptador de consola previsto.

Sus responsabilidades son:

- Mostrar el tablero.
- Leer las jugadas ingresadas.
- Convertirlas en solicitudes `Move`.
- Llamar a `IGameService`.
- Mostrar los resultados, traduciendo cada `RejectionReason` a un mensaje legible.

Recibe un `IGameService` por constructor. Las reglas del ajedrez permanecen en el núcleo.

## Relaciones entre clases

| Relación | Significado |
|---|---|
| `Board` contiene `Piece` | El tablero guarda las piezas ubicadas en sus casillas. |
| `Board` usa `Position` | Las consultas y actualizaciones identifican casillas mediante posiciones. |
| `Piece` tiene `PieceColor` | Cada pieza pertenece a un color. |
| `Piece` tiene `IMovementStrategy` | Delega sus reglas particulares en una estrategia. |
| Las seis estrategias implementan `IMovementStrategy` | Comparten un contrato con comportamientos diferentes. |
| `QueenMovement` tiene dos estrategias | Reutiliza los movimientos de torre y alfil. |
| `Game` implementa `IGameService` | Ofrece las operaciones públicas para jugar. |
| `Game` tiene `Board` | Mantiene el estado de su partida. |
| `Game` tiene `PieceColor` | Identifica el turno actual. |
| `Game` usa `MoveValidator` | Valida antes de modificar el estado. |
| `Game` usa `CheckDetector` | Consulta el jaque. |
| `Game` recibe `Move` | Recibe la jugada solicitada. |
| `Game` devuelve `MoveResult` | Comunica el resultado de la jugada. |
| `Move` tiene dos `Position` | Representa origen y destino. |
| `MoveResult` puede referenciar `Piece` | Informa la pieza capturada. |
| `MoveValidator` usa `CheckDetector` | Comprueba que el propio rey quede protegido. |
| `MoveValidator` devuelve `ValidationResult` | Informa si la jugada es válida y, si no, por qué. |
| `ValidationResult` y `MoveResult` tienen `RejectionReason` | Comparten el motivo del rechazo como dato. |
| `CheckDetector` usa las estrategias | Determina qué casillas amenazan las piezas enemigas. |
| `ConsoleUI` usa `IGameService` | Solicita operaciones al núcleo mediante su contrato. |
| `StandardGame` crea y conecta los objetos | Centraliza el armado de la partida. |

## Flujo de una jugada

Flujo previsto: todavía no está conectado, porque faltan la consola, el validador y la ejecución de jugadas.

1. `ConsoleUI` recibe el origen y destino ingresados por el jugador.
2. Construye un `Move` y lo entrega a `IGameService`.
3. `Game` solicita la validación a `MoveValidator`.
4. El validador comprueba tablero, turno, destino y estrategia de movimiento.
5. Simula la jugada y consulta a `CheckDetector` para proteger al propio rey.
6. Si la jugada es inválida, `Game` devuelve un rechazo y conserva el tablero y el turno.
7. Si es válida, `Game` aplica el movimiento, realiza la captura y cambia el turno.
8. Consulta si el siguiente jugador está en jaque.
9. Devuelve un `MoveResult`.
10. `ConsoleUI` muestra el resultado.

## Patrones y principios aplicados

### Strategy

**Qué:** cada pieza recibe una implementación de `IMovementStrategy`.

**Por qué:** las piezas tienen diferentes reglas. El contrato permite incorporar movimientos nuevos manteniendo estable la lógica general de la partida.

**Cuándo cambiar esta decisión:** si el dominio tuviera un único comportamiento fijo y separar estrategias no aportara extensibilidad.

Para agregar una pieza nueva se implementa otra estrategia y se incorpora su configuración al armado de la partida.

### Composición sobre herencia

**Qué:** `Piece` tiene una estrategia y `QueenMovement` combina estrategias de torre y alfil.

**Por qué:** permite reutilizar y sustituir comportamientos sin depender de una jerarquía rígida de clases.

**Cuándo cambiar esta decisión:** si existiera una relación de herencia estable y claramente sustituible que aportara una solución más simple.

### Inyección de dependencias por constructor

**Qué:** los objetos reciben sus colaboradores al construirse. `StandardGame` será el encargado de conectar las implementaciones concretas cuando se implemente.

**Por qué:** las dependencias quedan explícitas y los objetos nacen con los colaboradores necesarios.

**Cuándo cambiar esta decisión:** si aparece otra necesidad de configuración. No se agregará un contenedor de dependencias sin una razón concreta.

Crear valores como `Position` o `Move` no requiere una interfaz ni un mecanismo de inyección.

### Separación entre núcleo y adaptadores

**Qué:** las reglas viven en `core`; la consola y el armado de la aplicación viven en `app`.

**Por qué:** permite probar las reglas sin interfaz ni infraestructura y reemplazar la presentación sin modificar el núcleo.

**Cuándo cambiar esta decisión:** en un prototipo descartable donde mantener esa frontera no aporte valor. En este TPO la separación es un requisito.

### Motivo de rechazo como dato

**Qué:** `MoveValidator.validate` devuelve un `ValidationResult` que, si la jugada se rechaza, trae un `RejectionReason` (enum). `MoveResult` propaga ese motivo. El texto que ve el jugador lo arma `ConsoleUI`.

**Por qué:** un `boolean` no dice por qué se rechazó, y un `String` armado en el núcleo mezcla las reglas con la presentación (idioma, formato) y obliga a los tests a comparar texto. Con un enum, los tests verifican el motivo exacto y cada adaptador decide cómo mostrarlo.

**Cuándo cambiar esta decisión:** si una validación exitosa tuviera que transportar información adicional (por ejemplo, qué jugada especial es, como enroque o captura al paso), `ValidationResult` pasaría a llevar más datos o se modelaría con tipos distintos para "válida" y "rechazada".

### Rey identificado con un indicador explícito

**Qué:** `Piece` recibe por constructor un booleano que indica si es el rey y lo expone con `isKing()`. `CheckDetector` y `MoveValidator` usan ese método. El nombre de la pieza queda solo para mostrarla.

**Por qué:** comparar el nombre con `"King"` hace depender una regla central de un texto: un error de tipeo no falla al compilar y rompe en silencio la detección de jaque. Un enum de tipos de pieza evitaría el texto, pero habría que modificarlo cada vez que se agrega una pieza, contra el requisito de sumar clases sin modificar las existentes. Con el indicador, una pieza nueva no obliga a tocar nada.

**Cuándo cambiar esta decisión:** si más reglas necesitaran saber de qué pieza se trata (por ejemplo enroque o promoción), un booleano por cada rol no escala y convendría un concepto propio, como un tipo o una capacidad de la pieza.

### Peón configurado con dirección y fila inicial

**Qué:** `PawnMovement` recibe por constructor su dirección (+1 o -1) y su fila inicial. `StandardGame` deberá armar una instancia para las blancas (dirección +1, fila 1) y otra para las negras (dirección -1, fila 6).

**Por qué:** el peón es la única pieza cuyo movimiento depende de su historia (el doble paso inicial) y de su color (hacia dónde avanza). Como un peón nunca retrocede, estar en su fila inicial equivale a no haberse movido, así que no hace falta un estado `hasMoved`. Un estado mutable dentro de `Piece` requeriría revisar las copias del tablero, que comparten las piezas, para evitar que una simulación altere el tablero real. También podría modelarse de forma inmutable o guardarse por separado; la fila inicial evita ese estado adicional para el alcance actual. Tampoco se escriben las filas 1 y 6 dentro de la estrategia, para que no dependa de un tablero de 8×8.

**Cuándo cambiar esta decisión:** si se agrega la captura al paso, que depende de la última jugada del rival y no de la posición del peón, la estrategia necesitaría acceso al historial de jugadas. También si una variante permitiera que los peones retrocedan o se coloquen en cualquier fila.

### Tablero con dimensiones configurables y piezas inmutables

**Qué:** `Board` recibe sus dimensiones por constructor y guarda las piezas en una matriz. `Position` es un valor inmutable con igualdad por contenido, y `Piece` conserva atributos finales. El contrato exige estrategias inmutables y consultas sin efectos secundarios. `Board.copy()` duplica las casillas pero comparte las piezas.

**Por qué:** las dimensiones no están escritas en el núcleo, así que otro tamaño de tablero se resuelve en `StandardGame` sin tocar las reglas (hay que definir una disposición inicial para ese tamaño). Con piezas inmutables, simular una jugada sobre una copia es barato y no puede alterar el tablero real, que es lo que usa `MoveValidator` para proteger al rey. `Position` con igualdad por contenido permite compararla en los tests y usarla en colecciones. Se eligió una matriz en vez de un `Map<Position, Piece>` por el acceso directo y el recorrido ordenado; con las dimensiones por constructor, el `Map` no aportaba flexibilidad extra.

**Cuándo cambiar esta decisión:** si las piezas necesitaran estado propio (por ejemplo, un contador de movimientos), dejarían de ser inmutables y `copy()` tendría que duplicarlas. Si hubiera tableros muy grandes y casi vacíos, convendría una estructura dispersa como un `Map`.

### Contrato de estrategia sin color y con ataque por defecto

**Qué:** `canMove` y `canAttack` no reciben el color de la pieza: la estrategia lo obtiene de la pieza que está en `from`. `canAttack` tiene una implementación por defecto igual a `canMove`, y `PawnMovement` la sobreescribe por sus ataques diagonales y `QueenMovement` para delegar el ataque en sus dos estrategias. `Piece` expone ambos métodos y delega en su estrategia.

**Por qué:** el color ya está en la pieza del origen, y pasarlo aparte permitía enviar uno que no coincidiera con ella. Cinco de las seis piezas amenazan exactamente las casillas a las que pueden moverse, así que repetir el mismo método en cada una duplicaría código sin aportar nada. El método por defecto evita una clase base abstracta, que sería herencia justo donde se busca composición. A cambio, el contrato tiene una precondición (en `from` está la pieza dueña de la estrategia), que `MoveValidator` garantiza, y los tests de cada estrategia arman un tablero con la pieza colocada. `Piece` delega para que el resto del núcleo no dependa de cómo está implementado el movimiento.

**Cuándo cambiar esta decisión:** si más de una o dos piezas necesitaran sobreescribir `canAttack`, conviene volverlo abstracto y que cada pieza lo declare, para que la diferencia no quede escondida en un valor por defecto. Si alguna estrategia necesitara más contexto que el tablero y las casillas (por ejemplo, el historial de jugadas para la captura al paso), el contrato tendría que recibirlo.

### Movimiento como consulta puntual y no como lista de destinos

**Qué:** cada estrategia responde si un movimiento concreto es posible (`canMove(board, from, to)`) y si amenaza una casilla concreta (`canAttack`). No genera la lista de todos los destinos posibles de la pieza. El ejemplo de la cátedra hace lo contrario: genera los movimientos posibles y verifica si el destino está en esa lista.

**Por qué:** el alcance obligatorio solo necesita validar la jugada que propone el jugador, de a una, y detectar si un rey está amenazado, que también es una pregunta sobre una casilla puntual. Un método que responde sí o no es más simple de implementar y de testear (una pregunta, una respuesta) y evita construir una lista de destinos cuando solo importa uno. El costo es que saber si un jugador tiene alguna jugada legal, o mostrar los movimientos posibles, se resuelve por fuerza bruta: probar cada pieza contra cada casilla. En la partida estándar, un jugador comienza con 16 piezas: probarlas contra 64 casillas supone hasta 1024 candidatos antes de filtrar movimientos. Solo los candidatos que superen las comprobaciones previas necesitan simulación. El rendimiento debe medirse antes de optimizar.

**Cuándo cambiar esta decisión:** si el grupo suma jaque mate, ahogado, oponente con IA o resaltado de movimientos legales, y la fuerza bruta se repite en varios lugares o se vuelve lenta, conviene agregar a `IMovementStrategy` un método que genere los destinos (`possibleMoves`), manteniendo `canMove` como consulta puntual. Se puede agregar como método por defecto basado en `canMove`, de modo que las estrategias existentes no se modifiquen, y cada pieza lo sobreescriba solo si necesita eficiencia.

### Turnos sin State

**Qué:** el turno se representa mediante `PieceColor`.

**Por qué:** existen dos turnos estables. Un patrón State completo agregaría complejidad para el alcance actual.

**Cuándo cambiar esta decisión:** si aparecen fases con distintos comportamientos y transiciones, como preparación, promoción pendiente o fin de partida.

### Patrones que no incorporamos inicialmente

- **Command:** se evaluará si agregamos deshacer/rehacer o un historial de acciones ejecutables.
- **Observer:** se evaluará si varios componentes necesitan reaccionar a eventos de la partida.
- **State:** se evaluará si aparecen fases con comportamientos diferentes.
- **Factory:** se evaluará si la elección y creación de objetos requiere una lógica específica. `StandardGame` funciona inicialmente como punto de composición.

## Aislamiento de la partida

`Game` conserva una copia del tablero recibido en el constructor. Así, modificar el tablero utilizado para crear la partida no cambia su estado interno. `snapshot()` también devuelve una copia. Ambas copias comparten piezas y estrategias, que deben respetar el contrato de inmutabilidad.

## Testing

Los tests actuales se ejecutan sin consola, interfaz gráfica ni servicios externos.

| Estrategia | Casos aprobados |
|---|---:|
| Torre | 5 |
| Alfil | 6 |
| Caballo | 16 |
| Rey | 16 |
| Peón | 22 |
| Reina | 40 |
| **Total** | **105** |

Última verificación: `mvn test` finalizó con `BUILD SUCCESS`, sin fallos, errores ni casos omitidos. Los casos parametrizados se cuentan por cada combinación ejecutada.

Se prueban movimientos válidos e inválidos, obstáculos en piezas deslizantes, saltos del caballo, avances y capturas del peón en ambos sentidos, ataques y conservación del tablero durante las consultas.

Para caballo, rey y peón se escribieron las pruebas antes de implementar, se comprobó que fallaban por los métodos pendientes y luego se implementaron las reglas. Los tests de la reina se agregaron sobre su implementación existente.

Las siguientes pruebas de integración del núcleo y validación general siguen pendientes:

- Límites del tablero y rechazo de posiciones fuera de él.
- Capturas aplicadas a través de `Game.move()`.
- Rechazo de capturas de piezas propias.
- Alternancia de turnos.
- Conservación del estado después de una jugada rechazada.
- Detección de jaque.
- Rechazo de jugadas que dejen al propio rey en jaque.

## Extensibilidad

La estructura busca permitir:

- Agregar estrategias para piezas nuevas.
- Reemplazar la consola por otra interfaz mediante `IGameService`.
- Probar las reglas independientemente de los adaptadores.
- Configurar dimensiones del tablero.

Cambiar las dimensiones también requiere definir una disposición inicial adecuada y revisar las reglas que dependan de ella. `StandardGame` arma específicamente la partida estándar de 8×8.

## UML y documentación

El diagrama de clases está en `docs/UML-Ajedrez-TPO.puml` (fuente PlantUML) y `docs/UML-Ajedrez-TPO.pdf` (diagrama renderizado). Documenta la estructura y los contratos de la arquitectura. Las seis estrategias de movimiento ya están implementadas; la validación y la coordinación de la partida siguen pendientes. El archivo PlantUML todavía no incluye `PathClearChecker`: falta actualizar la fuente y regenerar el PDF antes de considerarlo sincronizado con el código actual. Para regenerar el PDF se puede usar el plugin de PlantUML de IntelliJ, o PlantUML con Graphviz.

Durante el desarrollo el UML deberá seguir actualizándose para representar las clases y relaciones realmente implementadas.

Cada cambio relevante deberá incluir:

- Código correspondiente.
- Pruebas de su comportamiento.
- Actualización del UML.
- Justificación de la decisión en formato **qué / por qué / cuándo cambiarla**.
