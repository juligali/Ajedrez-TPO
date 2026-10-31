# BLOCKMATE · Ajedrez de Minecraft

Ajedrez local para **una persona contra IA o dos personas en la misma computadora**, con reglas implementadas en Java y una interfaz de personajes hechos con bloques.

[Ver una captura de la interfaz](docs/vista-previa.png).

La versión web se desarrolló a partir de la estructura inicial de Ajedrez-TPO y luego se integró en este repositorio, conservando su historial de Git. El proyecto actual incluye el juego completo, los adaptadores web y de consola y las pruebas con Maven y JUnit 5.

## Actualización del juego

[Informe Word sobre clases del núcleo, puertos y adaptadores](docs/Informe%20arquitectura%20Ajedrez%20Minecraft.docx).

### Puntos y ganador

Cada equipo suma los valores de las piezas rivales que captura: **peón 1, caballo 3, alfil 3, torre 5 y reina 9**. El rey no tiene valor numérico ni se captura. El marcador empieza en cero, se corrige al deshacer/rehacer y no determina el ganador. La corona aparece únicamente para el ganador por jaque mate y desaparece al deshacer el final.

Se puede dar mate con una sola pieza atacante si la posición no tiene defensa legal. Las pruebas incluyen una torre apoyada por el rey y un caballo con el rey rival encerrado por piezas propias. Rey y caballo contra rey sigue siendo material insuficiente.

### Tutorial

Antes de crear una partida se muestra **Cómo se juega**: incluye un tablero interactivo para cada una de las seis piezas, sus movimientos y valores, el objetivo del juego y reglas especiales. Tocá una casilla marcada para probar un movimiento. Los aros del peón muestran capturas de piezas rivales. Podés volver a consultarlo desde **Cómo jugar**.

Se mantienen los destinos legales resaltados en la partida real. El tutorial es ilustrativo y no altera el juego.

### Uno o dos jugadores

Al empezar elegís el modo. En **dos jugadores** ambos equipos son humanos y la IA no interviene. En **solo** jugás con blancas y la IA responde con negras.

| Nivel | Comportamiento |
|---|---|
| Súper fácil | Movimiento legal al azar |
| Fácil | Prioriza capturas/promociones, con un 35 % de elecciones aleatorias |
| Medio | Hasta 2 medias jugadas, 1.200 nodos o 600 ms |
| Difícil | Hasta 3 medias jugadas, 5.000 nodos o 1.200 ms |
| Hiper mega difícil | Hasta 4 medias jugadas, 16.000 nodos o 2.200 ms |

La IA es **didáctica y local**, no un motor profesional ni una clasificación Elo. Los niveles superiores usan minimax con poda alfa-beta, evaluando material y posición. La profundidad alcanzada depende de los límites. No utiliza aprendizaje automático.

En solo, **deshacer** normalmente revierte tu movimiento y su respuesta para volver a tu decisión anterior. Rehacer restaura ambos. Si la IA todavía no respondió, se deshace sólo el movimiento humano.

### Guardado y cronómetro

La partida se guarda automáticamente después de crearla, mover, responder la IA, deshacer y rehacer. También hay un botón **Guardar partida**.

El archivo es **data/current-match.properties**, dentro de esta carpeta. Conserva movimientos, cursor del historial, modo, dificultad y marcas de tiempo. Al cargar se reconstruyen tablero, capturas, puntaje, turnos, al paso, derechos de enroque, repetición y posibilidad de rehacer. Si la IA quedó pendiente, responde al volver a abrir la web.

El cronómetro acumula únicamente el tiempo con el juego **abierto y visible**. Se pausa al cerrar, minimizar o cambiar de pestaña y continúa desde el tiempo guardado al volver, sin sumar las horas con el juego cerrado. Si hay varias pestañas, basta una visible y el tiempo no se duplica. Se detiene al terminar y se reanuda al deshacer el final. Una partida nueva reinicia reloj y puntos, después de la confirmación.

La vista informa actividad cada 5 segundos y avisa al salir. El servidor guarda el tiempo acumulado y, al reiniciar, siempre espera una vista activa. Si el navegador desaparece sin avisar, su actividad vence en 15 segundos. Ante un corte abrupto se conserva el último guardado y pueden perderse los últimos segundos. Los guardados antiguos se migran conservando el tiempo hasta su última escritura, porque no registraban las pausas; no es posible reconstruir las pausas anteriores a esta actualización.

Si falla la escritura, se revierte la operación y se informa el error. Un archivo corrupto se rechaza sin sobrescribirlo silenciosamente. El adaptador escribe primero un temporal y usa reemplazo atómico cuando el sistema lo permite. Usá un solo servidor por archivo de guardado.

Los guardados son locales y no se versionan en Git. Si no existe un archivo de guardado, la web permite configurar una partida nueva.

### Nuevas clases y contratos

- **IMatchService / MatchSession:** puerto de entrada y coordinación de sesión, IA, reloj y guardado.
- **IGameService / Game:** reglas de partida y puntaje por capturas.
- **IGameRepository:** puerto de salida para cargar y guardar.
- **FileGameRepository:** adaptador que implementa el contrato con un archivo Properties.
- **SavedMatch:** datos de la sesión, secuencia de movimientos y posición actual del historial.
- **IChessAi / MinimaxAi:** contrato de elección de jugada y estrategia local por nivel.
- **GameMode / Difficulty:** configuración explícita.
- **Clock:** abstracción estándar de Java inyectada para medir tiempo y probarlo.
- **PersistenceException:** comunica fallos de almacenamiento sin importar clases del adaptador.

La web entra por IMatchService. MatchSession llama a IGameService y a los puertos de salida. La consola sigue entrando directamente por IGameService y ofrece sólo el ajedrez básico; los modos, tutorial y guardado de sesión corresponden a la web.

La persistencia ahora justifica una interfaz de salida real y dobles de prueba. Se conservan Strategy, Simple Factory, Command, inyección por constructor y composición.

### Pruebas de la actualización

**53 pruebas con JUnit 5**: los 47 escenarios existentes más 4 de validación tipada y 2 de trayectorias inválidas. Se agregaron puntaje, corona, mate con un atacante, cinco niveles legales, separación de modos, repositorio de archivo, recuperación, al paso tras carga, reloj y reversión ante fallos de guardado.

El repositorio en memoria, el reloj controlado y la IA sustituible permiten probar sin infraestructura. Una prueba utiliza el repositorio de archivo real con una carpeta temporal aislada mediante `@TempDir`. Tanto `mvn test` como `run.ps1 -Test` ejecutan todas las suites.

La comprobación opcional **tools/verify-features.cjs** usa Node.js, Playwright y Chrome. Prueba tutorial, puntos, corona, cronómetro, recarga, cinco niveles, deshacer/rehacer y ancho de 390 px. **Reinicia la partida**, así que debe usarse con un servidor de prueba, por ejemplo:

    java -cp target/classes ar.edu.ajedrez.app.WebServer 8082 build/ui-test-match.properties

En otra terminal, establecé CHESS_URL en http://127.0.0.1:8082 antes de ejecutar los scripts de prueba. Nunca los ejecutes sobre una partida personal que quieras conservar.

## Cómo jugar

**Requisito:** JDK 21, con `java` y `javac` disponibles en PATH. Se utiliza Maven para compilar y JUnit 5 para ejecutar las pruebas. La primera compilación puede necesitar Internet para descargar dependencias; jugar después es local y no necesita npm ni base de datos.

1. Hacé doble clic en **Jugar.bat**.
2. Se abrirá una ventana de comandos: esperá a que Maven compile y aparezca “Ajedrez de bloques listo”. En VS Code, abrir `Jugar.bat` en el editor muestra su código; para ejecutarlo, usá el Explorador de archivos de Windows.
3. Abrí **http://127.0.0.1:8080** en Chrome, Edge o Firefox.
4. Seguí el tutorial y elegí modo y nivel. Seleccioná una pieza y después una casilla marcada. Empiezan las blancas.
5. Dejá abierta la consola mientras jugás. Para detener el servidor, presioná Ctrl+C.

También se puede ejecutar desde PowerShell:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1
```

Si el puerto 8080 está ocupado:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1 -Port 8081
```

Abrí entonces http://127.0.0.1:8081. Si aparece “javac no se reconoce”, revisá la instalación del **JDK**, que incluye el compilador, y volvé a abrir la terminal.

### Interfaz alternativa de consola

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1 -Console
```

Ingresá movimientos como `e2 e4`, o los comandos `deshacer`, `rehacer` y `salir`. Para elegir una promoción, por ejemplo: `a7 a8 KNIGHT`. Sin tercer argumento, promociona a reina.

La consola y la web usan el mismo núcleo. La consola inicia una partida nueva sin guardado de sesión. La web recupera la partida guardada si existe; si no, permite configurar una nueva. Las pestañas conectadas al mismo servidor comparten esa partida.

## Personajes

| Pieza | Personaje | Movimiento |
|---|---|---|
| Rey | Steve con corona y espada | Una casilla en cualquier dirección |
| Reina | Alex con corona | Rectas y diagonales |
| Torre | Gólem de hierro | Horizontal y vertical |
| Alfil | Bruja con sombrero y poción | Diagonales |
| Caballo | Caballo de Minecraft | En L, saltando piezas |
| Peón | Creeper | Avanza hacia adelante y captura en diagonal |

Los dos bandos usan los mismos personajes, como en la referencia. Se distinguen por las **bases claras con borde dorado** y **oscuras con borde rojizo**. El texto de turno y las etiquetas de las casillas también identifican el color.

Los modelos son dibujos voxel originales construidos con cubos en Canvas 2D. Se proyectan para mostrar volumen, caras iluminadas y sombras. No se descargan recursos externos ni se utilizan modelos oficiales. Es un proyecto académico inspirado en Minecraft, sin afiliación con Mojang o Microsoft.

## Funcionalidades implementadas

- Tablero de 8 × 8 y disposición inicial clásica.
- Movimientos de las seis piezas, obstáculos, capturas y alternancia de turnos.
- Destinos legales resaltados y ayuda contextual de la pieza seleccionada.
- Rechazo de jugadas que dejen al propio rey en jaque.
- Jaque, jaque mate y ahogado. El rey no se captura.
- Enroque corto y largo, con control de derechos, obstáculos y casillas amenazadas.
- Captura al paso, disponible únicamente en la jugada inmediata siguiente.
- Promoción a reina, torre, alfil o caballo, mediante un selector.
- Historial de movimientos y piezas capturadas.
- Deshacer y rehacer, incluyendo capturas, enroque, al paso, promoción y fin de partida.
- Nueva partida con confirmación y giro del tablero.
- Sonido opcional generado localmente, inicialmente apagado.
- Interfaz adaptable a pantallas angostas y controles utilizables con teclado.

**Teclado:** Tab para recorrer los controles, Enter o Espacio para activarlos, flechas entre casillas y Escape para quitar una selección o cerrar un diálogo.

### Alcance y simplificaciones

- Es una partida **local**, con IA opcional y cronómetro general. No tiene cuentas ni multijugador por Internet.
- Una instancia del servidor contiene una sola partida. Las pestañas comparten esa partida. Se comprueba una revisión del estado para rechazar acciones desactualizadas; no hay sincronización instantánea entre pestañas.
- La partida se recupera desde el archivo local incluso después de cerrar Java.
- La notación del historial es descriptiva, por ejemplo `e2 → e4`, `e4 × d5` y `O-O`. No se implementa importación/exportación PGN ni notación SAN completa.
- La triple repetición y los 50 movimientos por bando sin capturas ni movimientos de peón producen **tablas automáticas**, en lugar del mecanismo de reclamación de torneo.
- Se detecta material insuficiente en los casos básicos: rey contra rey, rey y una pieza menor contra rey, y posiciones con sólo reyes y alfiles del mismo color de casilla. No se pretende resolver todas las posibles posiciones muertas.
- No se incluyeron rendición ni tablas por acuerdo.

## Arquitectura: lo visto en clase

La organización sigue la separación **núcleo / adaptadores** de las unidades 00 y 01.

```text
Navegador (HTML, CSS, Canvas, JavaScript)
                 |
        WebServer: adaptador HTTP
                 |
       IMatchService / MatchSession
                 |
           IGameService  <--- ConsoleUI
                 |
                Game
         /       |        \
 MoveValidator  Board   MoveCommand
       |          |
 CheckDetector   Piece
                  |
          IMovementStrategy
                  |
       Seis estrategias concretas
```

- **Núcleo (`core`):** reglas, piezas, tablero, turnos, validación e historial.
- **Puerto de entrada (`IGameService`):** operaciones de la partida, sin HTTP ni elementos gráficos.
- **Adaptadores (app):** servidor web, consola y repositorio de archivo.
- **Punto de composición (`StandardGame`):** crea los objetos y conecta sus dependencias mediante constructores.
- **Presentación (`web`):** dibuja y envía solicitudes. No decide si una jugada es legal.

Es una aplicación monolítica pequeña con una frontera de puertos y adaptadores. No hay microservicios ni contenedor de inyección. El núcleo no importa clases del servidor web, del navegador o de la consola.

### Flujo de una jugada

1. El usuario selecciona origen y destino.
2. El navegador pide a WebServer que realice el movimiento mediante IMatchService; MatchSession coordina las reglas y el guardado.
3. MatchSession construye la operación de sesión y llama a IGameService.move; después solicita guardar mediante IGameRepository.
4. `Game` consulta a `MoveValidator`.
5. La estrategia comprueba el movimiento propio de la pieza. El validador simula la jugada para verificar que el rey quede protegido.
6. Si es legal, se ejecuta un `MoveCommand`, se guarda en el historial y cambia el turno.
7. `Game` determina si hay jaque o final de partida.
8. El adaptador devuelve el estado y la interfaz lo muestra.

Una jugada rechazada conserva tablero, turno, historial y posibilidad de rehacer.

## Patrones de diseño utilizados

### 1. Strategy: movimientos de piezas

**Qué:** `Piece` recibe un `IMovementStrategy` por constructor. `PawnMovement`, `RookMovement`, `KnightMovement`, `BishopMovement`, `QueenMovement` y `KingMovement` implementan el contrato.

**Por qué:** cada pieza tiene un algoritmo distinto y podemos probarlo por separado. `Piece` delega el comportamiento sin tener un gran condicional que conozca todas las reglas.

**Ejemplo real:** `Piece.canMove` llama a `movement.canMove`. El peón redefine también `canAttack`, porque avanzar y atacar son cosas distintas.

**Cuándo cambiarlo:** si hubiera un único comportamiento fijo, separar estrategias podría resultar innecesario. Agregar una pieza nueva requiere su estrategia y su configuración de creación y presentación; no significa que todo el sistema se extienda sin tocar ningún archivo.

### 2. Simple Factory: creación de piezas

**Qué:** `PieceFactory.create(type, color)` elige la estrategia que corresponde al tipo solicitado.

**Por qué:** la decisión de construcción aparece al armar el tablero y al promocionar peones. Centralizarla evita duplicar esa selección.

**Ejemplo real:** la reina recibe una `QueenMovement` construida con los comportamientos de torre y alfil. Las estrategias no tienen estado y se comparten dentro de la fábrica.

**Cuándo cambiarlo:** si sólo se creara un objeto de tipo fijo, usar directamente su constructor sería más claro.

Se trata de una **fábrica simple**, en el sentido de centralización de creación visto en clase. No es una jerarquía de Factory Method ni Abstract Factory.

### 3. Command: deshacer y rehacer

**Qué:** `MoveCommand` implementa `ICommand`, con `execute()` y `undo()`. Contiene la jugada y una copia del tablero anterior.

**Por qué:** necesitamos revertir exactamente los cambios, incluyendo capturas y jugadas especiales. `Game` administra una lista de comandos ejecutados y una pila de comandos para rehacer.

**Ejemplo real:** al deshacer una captura al paso vuelven ambos peones a su lugar, se restaura la posibilidad de captura al paso y vuelve el turno anterior. Una jugada válida nueva elimina la rama de rehacer.

**Decisión de simplicidad:** guardamos una copia de las 64 referencias del tablero, los datos de al paso y el contador de medias jugadas. Las piezas son inmutables, por lo que no hace falta copiar cada objeto. Para un tablero pequeño es una solución fácil de explicar.

**Cuándo cambiarlo:** para tableros muy grandes o historiales enormes se podría almacenar sólo la diferencia de cada jugada, después de medir el costo real.

`Move` es únicamente una solicitud de datos. El patrón Command está en `MoveCommand`, no en el nombre de la solicitud.

### Patrones que no forzamos

- **State:** el flujo actual tiene pocos estados estables; se resuelve con el color del turno y un estado calculado. No hay una clase por estado.
- **Observer:** el núcleo no necesita suscripciones. La interfaz actualiza la pantalla al recibir una respuesta. Usar eventos del navegador no se presenta como una implementación propia de Observer en Java.
- **Object Pool:** no hay evidencia de que la creación de objetos justifique un pool.
- **Singleton:** no se agregó un acceso global a la partida.

Estas decisiones siguen el criterio de la materia: **qué, por qué y cuándo cambiar la decisión**, evitando abstracciones sin una necesidad real.

## SOLID, composición e inyección de dependencias

| Concepto | Aplicación concreta |
|---|---|
| **S: responsabilidad única** | `Board` guarda estado; las estrategias describen movimientos; `CheckDetector` detecta amenazas; `MoveValidator` valida una jugada; `Game` coordina la partida; los adaptadores presentan y traducen entradas. |
| **O: abierto/cerrado** | `Piece` trabaja con un contrato de movimiento. Puede recibir otra estrategia sin modificar su código. La composición y los gráficos sí deben configurarse al agregar un tipo nuevo. |
| **L: sustitución** | Las estrategias cumplen el mismo contrato: responden si pueden mover/atacar sin modificar el tablero. La sustitución se prueba también con una estrategia stub. |
| **I: segregación de interfaces** | `IMovementStrategy` contiene movimiento y ataque; `ICommand`, ejecutar y deshacer; `IGameService`, operaciones cohesivas de una partida. No mezclan sonido, dibujo ni HTTP. |
| **D: inversión de dependencias** | WebServer recibe IMatchService y ConsoleUI recibe IGameService. MatchSession recibe los contratos de repositorio e IA. `Piece` recibe `IMovementStrategy`. El núcleo no depende de clases de presentación. Los colaboradores internos estables permanecen concretos. |
| **Inyección por constructor** | `Game` recibe tablero, turno, validador, detector y fábrica. `StandardGame` hace la conexión explícita. No se usa un framework de DI. |
| **Composición sobre herencia** | `Piece` **tiene** una estrategia. `QueenMovement` **tiene** los movimientos de torre y alfil. No hay una jerarquía de subclases de piezas. |
| **Encapsulamiento** | Los campos se mantienen privados. `getBoard()` devuelve una copia y el historial expone `MoveRecord` inmutables, nunca comandos que la UI pueda ejecutar. |
| **KISS y DRY** | Matriz 8 × 8, colecciones del lenguaje, lógica compartida y compilación con Maven y JDK 21. Sin capas o dependencias para problemas que este proyecto no tiene. |

## Testing

Ejecutá:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1 -Test
```

Hay **53 escenarios de prueba**: 31 en `ChessTest.java`, 16 en `FeatureTest.java`, 4 en `ValidationTest.java` y 2 en `BoardPathTest.java`. Usan @Test, @DisplayName y aserciones de JUnit 5; una condición fallida hace fallar la construcción. JUnit 5 ejecuta cada escenario por separado mediante Maven y publica los informes en target/surefire-reports. No necesitan navegador ni servidor.

Aplicamos **Arrange / Act / Assert**: preparar un tablero, ejecutar una acción y comprobar el resultado. La suite incluye pruebas unitarias de estrategias y pruebas de integración entre los objetos del dominio.

Cobertura principal:

- Reglas y obstáculos de las seis piezas, límites y turnos.
- Ataque diagonal del peón, piezas clavadas, jaque y reyes adyacentes.
- Capturas, mate y ahogado.
- Enroque de ambos lados y colores, y sus restricciones.
- Al paso, vencimiento de ese derecho y un caso donde descubriría jaque.
- Las cuatro promociones, promoción negra y promoción con captura.
- Comparación del estado antes y después de ejecutar/deshacer.
- Historial, rehacer, creación de una nueva rama y copias defensivas.
- Tablas, contadores y repetición después de deshacer/rehacer.
- **Perft inicial:** 20, 400 y 8.902 posiciones legales a profundidades 1, 2 y 3.

Un **stub** de `IMovementStrategy` comprueba que `Piece` use el comportamiento inyectado. La sesión agrega un repositorio en memoria, un reloj controlado y una IA sustituible como dobles de prueba.

Después de integrar la versión web y migrar a Maven, se ejecutaron los 47 tests de JUnit 5 sin fallos y se comprobó por HTTP la partida inicial, destinos legales, movimiento, cambio de turno, deshacer, rehacer y entrega del HTML. Esta verificación no incluye una revisión visual completa ni la ejecución de toda la suite de navegador tras la migración.

El script opcional `tools/verify-ui.cjs` requiere Node.js, Playwright y Chrome; **no son necesarios para jugar ni para ejecutar las pruebas Java**. Las pruebas de UI reinician la partida, así que deben correrse sobre un servidor dedicado a pruebas.

La prueba HTTP opcional se ejecuta con Node.js 18 o superior y el servidor activo: `node tools/verify-api.mjs`. Comprueba el contrato del adaptador, incluyendo rechazos sin mutación y revisiones desactualizadas. También reinicia la partida.

No se afirma haber seguido TDD estricto, Scrum o pair programming. Tener pruebas no demuestra por sí solo que se escribieron antes de la implementación.

## CPU y memoria: unidades 04

### Decisiones aplicadas

- **Acceso directo:** `Piece[8][8]` permite consultar una casilla en O(1).
- **Especialización:** las estrategias separan los algoritmos de cada pieza.
- **Corte temprano:** la validación rechaza primero los errores simples, antes de copiar el tablero. La búsqueda de jugadas se detiene en cuanto encuentra una al comprobar si la partida puede continuar.
- **Caché de gráficos:** los seis personajes, para dos colores, se dibujan una sola vez por página. Se reutilizan hasta 12 imágenes.
- **Cálculo a demanda:** se piden los destinos legales cuando se selecciona una pieza.
- **Render por eventos:** el tablero se actualiza al interactuar, sin un bucle permanente de animación.
- **Compartir datos inmutables:** las copias de tablero comparten `Piece` y las estrategias sin modificarlas.
- **Liberación de referencias:** una nueva partida reemplaza el estado anterior; una rama nueva vacía la pila de rehacer. Los nodos de sonido se desconectan al terminar.

### Costos y límites

Si llamamos **n** a la cantidad de casillas, buscar el rey y recorrer atacantes es O(n), más el recorrido de un rayo de ataque. La búsqueda simple de jugadas puede examinar hasta n × n pares origen/destino, cada uno con su validación. Con un tablero fijo de 64 casillas es un costo acotado, aunque no sería un motor competitivo para tableros enormes.

Cada comando guarda O(n) referencias de tablero y el historial consume O(m × n), con **m** movimientos conservados. No se describe como memoria constante. Las imágenes permanecen en la caché hasta cerrar la página y Java administra sus objetos mediante el garbage collector.

Las pruebas reportan el tiempo observado de la suite; eso **no reemplaza un profiler** ni garantiza rendimiento en todas las máquinas. Para investigar un problema se usarían VisualVM/JFR para Java y Performance/Memory de DevTools para la interfaz. No se fuerza el GC ni se agregó pooling sin mediciones.

## Otros temas de clase

- **Requisitos funcionales y no funcionales:** definidos en [docs/requisitos.md](docs/requisitos.md).
- **Diagramas y relaciones entre clases:** [docs/arquitectura.md](docs/arquitectura.md).
- **Ágil:** se incluye un backlog sencillo por funcionalidades verificables. Es una guía para continuar el TPO, no evidencia de ceremonias de Scrum realizadas.
- **BPO y SLA:** no contratamos un proveedor ni consumimos un servicio externo. La relación útil es la idea de contrato: `IGameService` establece qué operaciones ofrece la partida. Los objetivos de calidad locales no se presentan como un SLA contractual.
- **Material consultado:** [docs/material-de-clase.md](docs/material-de-clase.md) relaciona el material de clase con decisiones concretas del código.

## Estructura

```text
src/main/java/ar/edu/ajedrez/
  core/
    board/        Board y Position
    pieces/       Piece, PieceType, PieceColor y PieceFactory
    movement/     IMovementStrategy y seis implementaciones
    validation/   MoveValidator y CheckDetector
    game/         IGameService, Game, Move, MoveResult,
                  ICommand, MoveCommand y MoveRecord
  core/ai/        IChessAi, MinimaxAi, Difficulty
  core/session/   IMatchService, MatchSession, IGameRepository, SavedMatch
  app/            StandardGame, WebServer y ConsoleUI
  app/persistence/ FileGameRepository
src/test/java/ar/edu/ajedrez/
                  ChessTest y FeatureTest
web/              index.html, styles.css, app.js, pieces.js,
                  tutorial.js y favicon.svg
docs/             arquitectura, requisitos y relación con las clases
tools/            comprobación opcional de interfaz
pom.xml           Java 21, JUnit 5, compilador y Surefire de Maven
run.ps1           compilación, ejecución y pruebas
Jugar.bat         entrada para Windows
```

### Para explicar el trabajo en una defensa

1. Mostrar `Piece` y cambiar conceptualmente su estrategia: **Strategy y composición**.
2. Seguir `StandardGame.create()`: **inyección por constructor y Factory**.
3. Mover, capturar, deshacer y rehacer: **Command**.
4. Ejecutar las pruebas sin abrir la web: **núcleo independiente**.
5. Mostrar `ConsoleUI`: el mismo contrato sirve para otra presentación.
6. Justificar por qué no se agregaron State, Observer ni un contenedor de DI.




La prueba opcional node tools/verify-restart.mjs inicia y cierra un servidor propio de prueba en 8083 para comprobar recuperación real. No usa el guardado personal.

## Maven y JUnit 5

Requisitos: JDK 21 y Maven. El script run.ps1 utiliza Maven del PATH o una distribución local previamente descargada por el IDE.

Desde la raíz del proyecto:

```powershell
mvn test
mvn package
```

La configuración está en pom.xml. Las pruebas se ejecutan con Surefire; los informes están en target/surefire-reports. El test del repositorio real utiliza @TempDir para aislar sus archivos.

Para jugar se puede usar Jugar.bat o run.ps1. El script primero ejecuta mvn compile y luego inicia Java con target/classes. La carpeta web debe permanecer junto al proyecto: el JAR no es un paquete autónomo con la interfaz embebida.

Esta integración mantiene el historial del repositorio original. Los tests antiguos ligados a su API fueron sustituidos por los 47 escenarios de la versión web, ahora ejecutados individualmente por JUnit 5. Las pruebas manuales y de navegador continúan siendo complementarias.

## Revisión final del 16 de octubre de 2026

Se conserva el tablero estándar 8×8 y el catálogo `PieceType`, según el alcance acordado con el docente. No se implementaron variantes ni un registro dinámico de piezas. La extensión se explica en [arquitectura](docs/arquitectura.md).

`MoveValidator.validate` devuelve el enum `ValidationResult`: `VALID` o un motivo semántico. `Game` propaga el rechazo en `MoveResult.getValidationResult()` y `MoveMessages`, en `app`, lo traduce para web y consola. Los mensajes de otras operaciones de sesión y las etiquetas del historial conservan su contrato previo; no se afirma haber internacionalizado todo el núcleo.

`Board.pathClear` rechaza extremos inválidos y trayectorias que no son rectas ni diagonales. Esto evita un bucle infinito ante una llamada incorrecta. Las estrategias estándar ya filtraban esas trayectorias, por lo que las reglas de juego se conservan.

La verificación final con Maven mantiene los 47 escenarios originales y agrega 6. El estado inicial limpio fue 47/47; una primera ejecución sin `clean` detectó clases compiladas obsoletas en `target`, no tests fuente actuales. No se ejecutó una nueva revisión de navegador en esta tarea.

Entregables coherentes con el código:

- [UML editable en PlantUML](docs/UML-Ajedrez-TPO.puml) y [PDF del UML](docs/UML-Ajedrez-TPO.pdf), divididos en seis vistas.
- [Arquitectura y decisiones para la defensa](docs/arquitectura.md).
- [Requisitos y aceptación](docs/requisitos.md).
- [Informe de arquitectura](docs/Informe%20arquitectura%20Ajedrez%20Minecraft.docx).
- [Presentación final](docs/BLOCKMATE-final.pptx).

El UML muestra los 37 tipos públicos de producción, su API pública declarada y las relaciones principales. Omite métodos privados y miembros heredados o generados por Java. `Move` y `Position` son clases finales, `ValidationResult` es un enum, y sólo `PawnMovement` redefine `canAttack`.

### Actualización del cronómetro de tiempo activo

Verificación de esta actualización: 62 pruebas Java aprobadas (las 53 existentes, con el escenario de reloj actualizado, más 9 casos de actividad). Cubren pausa y reanudación, varias vistas, vencimiento de actividad, reinicio, reinicio del tiempo, recuperación ante fallos de guardado y migración del formato anterior. Una prueba de navegador con partida aislada verificó salida y regreso a la página, pausa tras reiniciar Java y rechazo de solicitudes de actividad de otro origen. Los informes históricos de otras secciones corresponden a sus respectivas revisiones.

## Preparación de la presentación del 6 de noviembre de 2026

Avance documental en `RAMA-VICTORIA`, con base de código `d90061e81c85b010f1a691bd7797cd20f55fc16d`:

- [Etapa 1: alcance, calendario propuesto y criterios de entrega](docs/alcance-y-criterios.md).
- [Etapa 2: revisión de patrones y contraste del UML con el programa](docs/revision-de-patrones.md).
- [Etapa 3: políticas de trabajo, respaldo, recuperación, IA y registro de riesgos](docs/gobernanza-y-riesgos.md).

El UML editable y su PDF incorporan `presence`, `isClockRunning`, `elapsedMillis` y el constructor vigente de SavedMatch. Se contrastaron los 37 tipos públicos con su API compilada y se revisaron las seis vistas del PDF. [Registro de verificación](docs/evidencias/2026-10-31-revision-uml.md).

Estos cambios no modifican el juego ni agregan patrones. Las políticas, responsables, fechas internas y riesgos son propuestas para revisar con el grupo; la presentación del 6 de noviembre y el requisito de correspondencia de los diagramas fueron informados por Victoria. No se ejecutó una nueva suite Maven ni una nueva medición de rendimiento en este avance.

## Etapa 4 medida el 31 de octubre de 2026

Se ejecutó `mvn -B -ntp clean test` sobre `83903e22eec288bd0823b7172d951b78610d9fdc`: **62 pruebas aprobadas, 0 fallos, 0 errores y 0 omitidas**, en Windows 11, JDK 21.0.6 y Maven 3.9.16.

- [Calidad y métricas](docs/calidad-y-metricas.md): fórmulas, fuentes, metas propuestas y resultados.
- [Matriz de requisitos y pruebas](docs/matriz-requisitos-pruebas.md): catálogo de 13 grupos de reglas y diez escenarios de recuperación automatizada, todos aprobados; requisitos parciales o no ejecutados visibles.
- [Evidencia de la ejecución](docs/evidencias/2026-10-31-etapa4-83903e2/README.md): informes Surefire, salida Maven, entorno, commit y manifiesto de fuentes.
- [Registro de incidencias y pendientes](docs/registro-incidencias.md): 0 críticos registrados en esta campaña; verificaciones de UI, respaldo manual y presentación todavía pendientes.

Esta medición Java no constituye una aprobación integral de la entrega ni una medición de rendimiento. No se cambió código ni se eliminaron pruebas. Los registros de las etapas anteriores conservan su contexto histórico.
