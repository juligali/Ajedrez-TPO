# Matriz de requisitos y pruebas

Medición del 31/10/2026 sobre `83903e22eec288bd0823b7172d951b78610d9fdc`. Fuente de requisitos: [requisitos.md](requisitos.md). Resultados: [evidencia de la etapa 4](evidencias/2026-10-31-etapa4-83903e2/README.md).

**Aprobado en el alcance** significa que los escenarios nombrados pasaron; no demuestra todos los comportamientos posibles. **Parcial** indica que hay una comprobación Java pero falta verificar otra parte del requisito. **No ejecutado** no significa fallo.

## Reglas del catálogo operativo v1

El catálogo propuesto consta de 13 grupos, que permanecen visibles en el denominador. Cada grupo se aprueba solo si todos los métodos asociados fueron ejecutados y aprobados. El grupo debe validar que este catálogo representa las reglas comprometidas para la presentación.

| ID | Regla | Requisito | Métodos ejecutados | Resultado |
|---|---|---|---|---|
| L01 | Disposición inicial estándar | RF01 | `ChessTest#scenario10`, `ChessTest#scenario31` | Aprobado |
| L02 | Movimientos, ataques y obstáculos de las seis piezas | RF02 | `ChessTest#scenario2`, `ChessTest#scenario3`, `ChessTest#scenario4`, `ChessTest#scenario5`, `ChessTest#scenario6`, `ChessTest#scenario7`, `ChessTest#scenario8` | Aprobado |
| L03 | Capturas, turnos y rechazo sin mutación | RF02 / RF11 | `ChessTest#scenario11`, `ChessTest#scenario12`, `ValidationTest#basicRejectionsHaveSemanticReasons`, `ValidationTest#gamePropagatesReasonWithoutChangingHistoryTurnOrRedo` | Aprobado |
| L04 | Seguridad del rey, jaque y prohibición de capturarlo | RF03 | `ChessTest#scenario14`, `ChessTest#scenario15`, `ValidationTest#kingSafetyCaptureAndPromotionHaveDistinctReasons` | Aprobado |
| L05 | Jaque mate y ganador | RF04 | `ChessTest#scenario16`, `FeatureTest#scenario3`, `FeatureTest#scenario4` | Aprobado |
| L06 | Ahogado distinto de mate | RF04 | `ChessTest#scenario17` | Aprobado |
| L07 | Enroque y sus restricciones | RF05 | `ChessTest#scenario18`, `ChessTest#scenario19`, `ChessTest#scenario20` | Aprobado |
| L08 | Captura al paso, vencimiento y seguridad del rey | RF05 | `ChessTest#scenario21`, `ChessTest#scenario22`, `ChessTest#scenario23` | Aprobado |
| L09 | Cuatro promociones y promoción con captura | RF05 | `ChessTest#scenario24`, `ChessTest#scenario25` | Aprobado |
| L10 | Deshacer, rehacer y eliminación de la rama futura | RF06 | `ChessTest#scenario21`, `ChessTest#scenario24`, `ChessTest#scenario26`, `ChessTest#scenario27`, `FeatureTest#scenario2`, `FeatureTest#scenario9` | Aprobado |
| L11 | Triple repetición automática | RF04 | `ChessTest#scenario28` | Aprobado |
| L12 | Material insuficiente en los casos comprometidos | RF04 | `ChessTest#scenario29`, `FeatureTest#scenario5` | Aprobado |
| L13 | Tablas por 50 movimientos y reinicio del contador | RF04 | `ChessTest#scenario30` | Aprobado |

Las pruebas están en [ChessTest](../src/test/java/ar/edu/ajedrez/ChessTest.java), [FeatureTest](../src/test/java/ar/edu/ajedrez/FeatureTest.java) y [ValidationTest](../src/test/java/ar/edu/ajedrez/ValidationTest.java). La relación máquina-legible se conserva en [catalogo-indicadores.json](evidencias/2026-10-31-etapa4-83903e2/catalogo-indicadores.json).

## Requisitos fuera del porcentaje de reglas

| Requisito | Evidencia de esta campaña | Estado y límite |
|---|---|---|
| RF07 Personajes de referencia | No se ejecutó navegador | No ejecutado; requiere inspección de los seis personajes y bandos |
| RF08 Turno, historial, capturas y destinos visibles | ChessTest scenario10/12/13/31 comprueba datos del núcleo | Parcial; presentación visual pendiente |
| RF09 Nueva partida con confirmación | FeatureTest scenario15 y ActivityClockTest newGameResetsTimeAndKeepsTheVisibleView | Parcial; reinicio de sesión aprobado, diálogo de confirmación no ejecutado |
| RF10 Consola alternativa | Se compiló ConsoleUI con el proyecto | Parcial; no se ejecutó un flujo interactivo de consola |
| RF11 Motivo semántico de rechazo | Los tres primeros métodos de ValidationTest | Aprobado en escenarios Java; incluido en L03/L04 |
| RF12 Traducción del rechazo en web y consola | ValidationTest adaptersTranslateEveryReasonAndKeepExistingSuccessMessages | Parcial; traductor probado, visualización real en ambos adaptadores pendiente |
| RNF01 Estabilidad de reglas y sesión | 62 métodos aprobados | Aprobado en escenarios de la suite, no garantía de ausencia de regresiones fuera de ellos |
| RNF02 Trayectorias inválidas terminan | Ambos métodos de BoardPathTest | Aprobado en escenarios ejecutados |
| DOC01 Correspondencia UML y programa | Evidencia documental anterior del 31/10; no hubo cambios Java desde esa revisión | Verificación histórica reutilizable sobre el mismo código; no se cuenta como test JUnit |
| DOC02 Decisiones e informe | Documentos de etapas 1–3 disponibles | Revisión humana integral pendiente; fuera de esta campaña de tests |
| Accesibilidad y ancho 390 px | No se ejecutaron verify-ui ni verify-features | No ejecutado |
| Uso offline y portabilidad de ejecución | Maven pudo compilar y probar en este entorno | Parcial; no se ensayó el juego sin conexión en el equipo de presentación |
| Rendimiento percibido | La suite imprime tiempos funcionales puntuales | Sin medición de rendimiento reproducible; etapa 5 |

## Funcionalidades agregadas

Los identificadores A01–A10 son auxiliares de esta matriz, no nuevos requisitos del docente.

| ID | Funcionalidad existente | Métodos o evidencia | Estado y límite |
|---|---|---|---|
| A01 | Puntaje y valores de piezas | FeatureTest scenario1/2 | Aprobado en el núcleo |
| A02 | Corona solo para el ganador por mate | FeatureTest scenario3/4 comprueba ganador y reversión | Parcial; dibujo de corona pendiente de navegador |
| A03 | Cronómetro de tiempo activo | FeatureTest scenario13 y los nueve métodos de ActivityClockTest | Aprobado en sesión; eventos reales de visibilidad del navegador pendientes |
| A04 | Guardado, recuperación e historial futuro | FeatureTest scenario10/11/12/14/16; ActivityClockTest fileFormatPreservesAccumulatedTime y checkpointSurvivesAbruptRestartWithoutAClosingEvent | Aprobado en escenarios automatizados; respaldo manual no ejecutado |
| A05 | Tutorial interactivo | Sin ejecución de navegador | No ejecutado |
| A06 | Uno o dos jugadores | FeatureTest scenario8/9 | Aprobado en sesión; selector visual pendiente |
| A07 | Cinco niveles de IA | FeatureTest scenario6/7 | Legalidad, no mutación y mate sencillo aprobados; no mide fuerza ajedrecística ni garantiza tiempo |
| A08 | Mate con una sola pieza atacante cuando es legal | FeatureTest scenario3/4/5 | Aprobado en posiciones del test; conserva material insuficiente |
| A09 | Destinos resaltados y undo/redo | ChessTest scenario10/26/27 y FeatureTest scenario9 | Parcial; reglas comprobadas y resaltado visual pendiente |
| A10 | Informe Word | Artefacto existente en docs | Revisión documental integral pendiente |

## Diez escenarios del KPI de recuperación

| Método | Qué comprueba | Medio | Resultado |
|---|---|---|---|
| FeatureTest#scenario10 | Historial, capturas, turno y redo tras reconstrucción | Repositorio en memoria | Aprobado |
| FeatureTest#scenario11 | Derecho de al paso tras reabrir | Repositorio en memoria | Aprobado |
| FeatureTest#scenario12 | Turno pendiente de IA y dificultad | Repositorio en memoria | Aprobado |
| FeatureTest#scenario14 | Fallo de guardado revierte jugada y estado | Fallo simulado en memoria | Aprobado |
| FeatureTest#scenario16 | Escritura, lectura y rechazo de corrupción | Archivo en @TempDir | Aprobado |
| ActivityClockTest#closeAndReopenExcludeOfflineTime | Recuperación del tiempo sin sumar cierre | Repositorio en memoria y reloj controlado | Aprobado |
| ActivityClockTest#checkpointSurvivesAbruptRestartWithoutAClosingEvent | Reconstrucción desde el último checkpoint | Repositorio en memoria y reloj controlado | Aprobado |
| ActivityClockTest#failedMovePreservesClockAndPresence | Fallo de persistencia conserva reloj y actividad | Fallo simulado en memoria | Aprobado |
| ActivityClockTest#fileFormatPreservesAccumulatedTime | Tiempo acumulado después de leer un archivo | Archivo en @TempDir | Aprobado |
| ActivityClockTest#migratesLegacySaveOnlyUntilItsLastWrite | Migración del formato anterior y nuevo guardado | Archivo en @TempDir | Aprobado |

El catálogo tiene 10 métodos distintos. No son diez recuperaciones de una partida personal ni diez ensayos de backup. Un mismo método puede respaldar varios requisitos, pero se cuenta una sola vez dentro de este KPI.
