````markdown
# Ajedrez TPO — Ingeniería de Software


> Este README describe la arquitectura propuesta. Las clases y relaciones se implementarán progresivamente.

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
│   │   └── KingMovement.java
│   ├── game/
│   │   ├── IGameService.java
│   │   ├── Game.java
│   │   ├── Move.java
│   │   └── MoveResult.java
│   └── validation/
│       ├── MoveValidator.java
│       └── CheckDetector.java
└── app/
    ├── StandardGame.java
    └── ConsoleUI.java
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
- Colocar y actualizar piezas.
- Permitir obtener una copia del estado del tablero.

`Board` guarda el estado. La coordinación de turnos y la validación de jugadas corresponden a otras clases.

### Position

Representa una casilla mediante fila y columna.
Permite expresar posiciones con un objeto, evitando pasar números sueltos por todos los métodos.

### Piece

Representa una pieza concreta de la partida.

Contiene:

- Nombre o identificación.
- Color.
- Una estrategia de movimiento.
- Una forma explícita de identificar si es el rey, necesaria para detectar jaque.

Recibe su estrategia por constructor y delega en ella las reglas particulares de movimiento y ataque.

### PieceColor

Es un enum con los valores `WHITE` y `BLACK`.

Se utiliza para representar el color de las piezas y el turno actual.

### IMovementStrategy

Define el contrato de las estrategias de movimiento:

- `canMove(...)`: comprueba si la pieza puede realizar un movimiento.
- `canAttack(...)`: comprueba si la pieza amenaza una casilla.

Movimiento y ataque se distinguen porque el peón avanza hacia adelante y ataca en diagonal.

Las estrategias comprueban las reglas particulares de cada pieza. La protección del propio rey corresponde a la validación general.

### Estrategias concretas

Cada clase implementa `IMovementStrategy`:

| Clase | Responsabilidad |
|---|---|
| `PawnMovement` | Avance y captura diagonal del peón. |
| `RookMovement` | Movimiento horizontal y vertical de la torre. |
| `KnightMovement` | Movimiento en L del caballo, que puede saltar obstáculos. |
| `BishopMovement` | Movimiento diagonal del alfil. |
| `QueenMovement` | Combinación de movimientos de torre y alfil. |
| `KingMovement` | Movimiento del rey de una casilla en cualquier dirección. |

Las estrategias de torre, alfil y reina comprueban que el camino esté libre.

`QueenMovement` recibe las estrategias de torre y alfil por constructor y reutiliza sus reglas mediante composición.

### Game

Coordina la partida.

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
- El motivo de un rechazo.
- La pieza capturada, si hubo alguna.
- Si la jugada produjo jaque.

Permite que la interfaz muestre resultados sin incorporar reglas del juego.

### MoveValidator

Comprueba si una jugada completa es válida.

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

El resultado de la validación deberá permitir comunicar el motivo del rechazo.

### CheckDetector

Determina si el rey de un color está amenazado.

Localiza al rey y consulta las estrategias de ataque de las piezas enemigas.

Se mantiene separado de `MoveValidator` para poder probarlo de manera independiente y reutilizarlo tanto en la validación como en la consulta del estado de la partida.

### StandardGame

Arma la partida estándar de 8×8.

Crea y conecta:

- Tablero.
- Piezas.
- Estrategias.
- Detector de jaque.
- Validador.
- Partida.

Es el punto de composición de la aplicación: concentra la creación de implementaciones concretas y la conexión de dependencias por constructor.

### ConsoleUI

Es el adaptador de consola.

Sus responsabilidades son:

- Mostrar el tablero.
- Leer las jugadas ingresadas.
- Convertirlas en solicitudes `Move`.
- Llamar a `IGameService`.
- Mostrar los resultados.

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
| `CheckDetector` usa las estrategias | Determina qué casillas amenazan las piezas enemigas. |
| `ConsoleUI` usa `IGameService` | Solicita operaciones al núcleo mediante su contrato. |
| `StandardGame` crea y conecta los objetos | Centraliza el armado de la partida. |

## Flujo de una jugada

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

**Qué:** los objetos reciben sus colaboradores al construirse. `StandardGame` conecta las implementaciones concretas.

**Por qué:** las dependencias quedan explícitas y los objetos nacen con los colaboradores necesarios.

**Cuándo cambiar esta decisión:** si aparece otra necesidad de configuración. No se agregará un contenedor de dependencias sin una razón concreta.

Crear valores como `Position` o `Move` no requiere una interfaz ni un mecanismo de inyección.

### Separación entre núcleo y adaptadores

**Qué:** las reglas viven en `core`; la consola y el armado de la aplicación viven en `app`.

**Por qué:** permite probar las reglas sin interfaz ni infraestructura y reemplazar la presentación sin modificar el núcleo.

**Cuándo cambiar esta decisión:** en un prototipo descartable donde mantener esa frontera no aporte valor. En este TPO la separación es un requisito.

### Turnos sin State

**Qué:** el turno se representa mediante `PieceColor`.

**Por qué:** existen dos turnos estables. Un patrón State completo agregaría complejidad para el alcance actual.

**Cuándo cambiar esta decisión:** si aparecen fases con distintos comportamientos y transiciones, como preparación, promoción pendiente o fin de partida.

### Patrones que no incorporamos inicialmente

- **Command:** se evaluará si agregamos deshacer/rehacer o un historial de acciones ejecutables.
- **Observer:** se evaluará si varios componentes necesitan reaccionar a eventos de la partida.
- **State:** se evaluará si aparecen fases con comportamientos diferentes.
- **Factory:** se evaluará si la elección y creación de objetos requiere una lógica específica. `StandardGame` funciona inicialmente como punto de composición.

## Testing

Las pruebas del núcleo deberán ejecutarse sin consola, interfaz gráfica ni servicios externos.

Se probarán, entre otros escenarios:

- Movimientos válidos e inválidos de las seis piezas.
- Obstáculos y límites del tablero.
- Captura de piezas enemigas.
- Rechazo de capturas de piezas propias.
- Alternancia de turnos.
- Conservación del estado después de una jugada rechazada.
- Ataque diagonal del peón.
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

El UML inicial sirve como guía de diseño. Durante el desarrollo deberá actualizarse para representar las clases y relaciones realmente implementadas.

Cada cambio relevante deberá incluir:

- Código correspondiente.
- Pruebas de su comportamiento.
- Actualización del UML.
- Justificación de la decisión en formato **qué / por qué / cuándo cambiarla**.
````