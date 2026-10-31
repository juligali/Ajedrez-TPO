# Etapa 5 — Rendimiento y memoria

Medición del **31/10/2026**, rama `RAMA-VICTORIA`, commit de aplicación `d37cbe4c39bd434915e2f822d06195bae3e95a69`. La aplicación no recibió optimizaciones en esta etapa. Los instrumentos se incorporan con este informe.

## Entorno y alcance

| Elemento | Valor registrado |
|---|---|
| Equipo | Intel Core i7-1255U, 10 núcleos, 12 procesadores lógicos |
| RAM física | 16.832.090.112 bytes (aproximadamente 16 GB) |
| Sistema | Windows 11, build 26200, AMD64 |
| Java | Oracle JDK 21.0.6, `-Xms128m -Xmx512m -Djava.awt.headless=true` |
| Medición Java | `System.nanoTime`, `ThreadMXBean` y JFR, configuración `profile` |
| Interfaz | Chrome 155.0.8059.86, Node 24.22.1, Playwright 1.62.1, Chrome DevTools Protocol |
| Pantallas | 1440 × 1100; comprobación adicional de 390 × 844 |
| Aislamiento de datos | Servidor dedicado en 18086 y `build/stage56-match.properties`; sin partida personal |

Es una campaña diagnóstica sobre un equipo, sin control del plan de energía ni exclusión de otros procesos del sistema. Java y navegador se midieron en campañas separadas. No se aplicó ralentización artificial de CPU ni de red. JFR estaba activo durante toda la campaña Java y puede introducir sobrecosto. No es un benchmark JMH ni una garantía de rendimiento en otros equipos.

## Escenarios reproducibles

1. **Consulta Java:** `StandardGame.create()`, apertura `e2-e4 e7-e5 g1-f3 b8-c6 f1-b5 a7-a6`, blancas al turno. Consultar `Game.legalMoves(b5)`, 500 calentamientos y 1.000 muestras. La posición permanece igual.
2. **IA máxima:** misma posición, `Difficulty.EXTREME`, un `Random(20261031)` nuevo por búsqueda. Tres calentamientos y diez muestras. Se valida cada jugada elegida; las diez fueron legales y devolvieron `b5-c6`. El tablero preparado queda fuera del intervalo cronometrado. El resultado puede variar con límites de tiempo en otro equipo.
3. **Memoria Java:** 50 ciclos de calentamiento; después cinco bloques de 100 partidas. Cada ciclo crea una partida, aplica las seis jugadas, deshace seis y rehace seis. Se descarta la referencia fuerte y se observa con referencias débiles. En cada corte se fuerza GC bajo las mismas condiciones y sin conservar una partida activa.
4. **Consulta web:** posición inicial, seleccionar `e2`, esperar el destino legal `e4` y deseleccionar. Diez calentamientos y treinta muestras. El tiempo de pared incluye automatización, HTTP, esperas y dibujo; no mide solo el motor ni solo renderizado.
5. **Memoria web:** cinco ciclos de calentamiento y quince medidos. Cada ciclo aplica las seis jugadas, deshace una, rehace una y abre una partida nueva desde la UI. Después de 0/5/10/15 ciclos se solicita GC y se toman heap JS, documentos, nodos y listeners con DevTools. Todos los cortes dejan la partida nueva en posición inicial.

El GC forzado se limita a los instrumentos de diagnóstico; no se agregó al programa. La memoria Java excluye historial largo creciente y la del navegador no es el RSS total de Chrome.

## Resultados

Tiempos en milisegundos. El p95 es el rango más cercano (`ceil(0,95 × n)`); con diez observaciones coincide con el máximo.

| Escenario | n | Mediana | p95 | Mínimo | Máximo |
|---|---:|---:|---:|---:|---:|
| Java, destinos legales de b5 | 1.000 | 0,0221 | 0,0426 | 0,0133 | 0,1358 |
| Java, IA EXTREME | 10 | 1.374,79 | 1.610,63 | 1.255,05 | 1.610,63 |
| Web, destinos de e2, tiempo de pared | 30 | 82,88 | 154,87 | 62,73 | 184,29 |
| Web, incremento de TaskDuration | 30 | 14,95 | 19,92 | 11,56 | 32,02 |

El CPU del hilo Java de IA tuvo mediana **1.343,75 ms**. El reloj de CPU observado tiene resolución gruesa (saltos de 15,625 ms): los ceros de las consultas cortas no significan que no consuman CPU. Por eso se usa tiempo de pared para esas consultas. DevTools `TaskDuration` mide trabajo del navegador y excluye CPU Java. Los quince ciclos web acumularon 4.400,657 ms de TaskDuration; no es duración de la partida ni porcentaje de CPU de todo el equipo.

### Memoria bajo condiciones equivalentes

| Partidas Java completadas | Heap usado tras GC (bytes) | Partidas anteriores vivas / observadas |
|---:|---:|---:|
| 0 | 3.376.184 | 0 / 0 |
| 100 | 3.381.360 | 0 / 100 |
| 200 | 3.386.592 | 0 / 200 |
| 300 | 3.390.280 | 0 / 300 |
| 400 | 3.394.216 | 0 / 400 |
| 500 | 3.397.416 | 0 / 500 |

El crecimiento fue de 21.232 bytes; el propio instrumento conserva una lista creciente de referencias débiles. Las partidas observadas sí fueron recolectadas. Esto no demuestra ausencia de toda fuga ni analiza todos los objetos del servidor. Un historial de una partida vigente retiene comandos por diseño y debe compararse con otro historial equivalente.

| Ciclos web | Heap JS usado (bytes) | Documentos | Nodos | Listeners |
|---:|---:|---:|---:|---:|
| 0 | 2.390.340 | 1 | 1.118 | 256 |
| 5 | 2.433.036 | 1 | 1.118 | 256 |
| 10 | 2.467.996 | 1 | 1.118 | 256 |
| 15 | 2.494.620 | 1 | 1.118 | 256 |

El heap JS creció 104.280 bytes (aproximadamente 4,36 %), mientras documentos, nodos y listeners permanecieron estables. La campaña corta no identifica retenedores ni permite atribuir el crecimiento a una fuga. Seguimiento propuesto: repetir una sesión más larga y comparar heap snapshots y rutas de retención si la tendencia continúa. No se declara esta observación como un defecto confirmado ni se oculta el incremento.

### Lectura del profiler y decisión

JFR registró 143 `ExecutionSample`. Entre los primeros frames del proyecto de cada pila predominan `MinimaxAi.legalMoves` (41), `Piece.canAttack` (38), `Piece.canMove` (28) y `MoveValidator.validate` (11). Son muestras estadísticas, no tiempos exactos por método. Los 5.806 eventos de asignación destacan `Position`, `Move` y arrays de piezas. Sus pesos estiman **asignación acumulada**, no bytes vivos ni consumo máximo.

No se observó un incumplimiento que justifique cambiar estructuras del motor en esta campaña. Se conserva la implementación simple, sin pooling ni cachés nuevas. No hay una comparación antes/después ni una mejora porcentual que atribuir. El presupuesto interno de 2.200 ms de EXTREME es un límite de búsqueda, no un SLA de respuesta HTTP. Las cinco dificultades fueron comprobadas funcionalmente; solo EXTREME tiene aquí una medición repetida específica.

## Reproducción

Desde la raíz del proyecto, con JDK 21 y Maven disponibles, en PowerShell:

```powershell
mvn -B -ntp clean test
javac -cp target/classes -d target/profile tools/ProfileChess.java
java -Xms128m -Xmx512m '-Djava.awt.headless=true' -cp 'target/classes;target/profile' ProfileChess build/profile
```

Se generan CSV y `build/profile/java-profile.jfr`, que puede abrirse con Java Mission Control. El resumen de eventos se genera leyendo ese mismo archivo con `jdk.jfr.consumer`. En sistemas con otro separador de classpath, adaptar `;` a `:`.

Para la web, iniciar un **servidor de prueba** con puerto libre y guardado separado:

```powershell
java -Xms128m -Xmx512m -cp target/classes ar.edu.ajedrez.app.WebServer 18086 build/stage56-match.properties
```

En otra terminal, con Node, Playwright 1.62.1 y Chrome instalados y disponibles para `require('playwright')`:

```powershell
$env:CHESS_URL='http://127.0.0.1:18086'
node tools/profile-browser.cjs
node tools/verify-features.cjs
```

Los scripts web reemplazan la partida de ese servidor. Ejecutarlos secuencialmente. Las dependencias del navegador son opcionales para las pruebas Java; ver [inventario](dependencias-y-recursos.md).

## Evidencias

[Carpeta de campaña](evidencias/2026-10-31-etapas5-6-d37cbe4/README.md): muestras originales, resumen JFR, cortes de memoria, entorno, capturas y resultados. Se conserva el resumen de eventos y la huella del JFR; el binario de diagnóstico permanece local porque puede incluir metadatos del entorno. Los scripts permiten regenerarlo.
