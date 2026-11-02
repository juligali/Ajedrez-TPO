# Matriz de requisitos y pruebas

Suite Java del 31/10/2026 sobre `83903e22eec288bd0823b7172d951b78610d9fdc`. Fuente de requisitos: [requisitos.md](requisitos.md). Resultados: [evidencia de la etapa 4](evidencias/2026-10-31-etapa4-83903e2/README.md).

**Aprobado en el alcance** significa que los escenarios nombrados pasaron; no demuestra todos los comportamientos posibles. **Parcial** indica que hay una comprobación Java pero falta verificar otra parte del requisito. **No ejecutado** no significa fallo.

## Reglas del catálogo operativo v1

El catálogo propuesto consta de 13 grupos, que permanecen visibles en el denominador. Cada grupo se aprueba solo si todos los métodos asociados fueron ejecutados y aprobados. El catálogo está pendiente de aceptación grupal; no equivale a una rúbrica docente.

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

## Requisitos de interfaz, sesión y documentación

Estado consolidado con la suite de Memento del 2/11/2026 y las campañas anteriores. Los 62 métodos anteriores y los tres nuevos pasaron en la copia de trabajo; las verificaciones web conservan su fecha del 31/10 y no fueron repetidas tras este cambio. “Parcial” conserva los límites del escenario y no significa fallo.

| Requisito | Evidencia | Estado y límite |
|---|---|---|
| RF07 Personajes | Capturas y tutorial automatizado de seis piezas | Parcial: no consta inspección individual de cada personaje y bando |
| RF08 Información visible | ChessTest scenario10/12/13/31 y flujos web | Comprobado en escenarios; no es cobertura visual exhaustiva |
| RF09 Nueva partida | FeatureTest scenario15, ActivityClockTest y creación desde UI | Reinicio comprobado; no se declara cobertura de todas las cancelaciones del diálogo |
| RF10 Consola | console-session.txt: mover, deshacer, rehacer, salir | Flujo básico comprobado |
| RF11 Rechazos semánticos | Primeros tres métodos de ValidationTest; L03/L04 | Aprobado en escenarios Java |
| RF12 Traducción | ValidationTest adaptersTranslateEveryReasonAndKeepExistingSuccessMessages | Traductor probado; no se verificó cada mensaje en ambos adaptadores |
| RF13 Puntaje | FeatureTest scenario1/2 y flujo web | Aprobado en escenarios de captura y undo/redo |
| RF14 Ganador | FeatureTest scenario3/4/5 y corona web | Mate y reversión comprobados en posiciones concretas |
| RF15 Reloj activo | FeatureTest scenario13 y nueve ActivityClockTest | Sesión comprobada; reloj finalizado en web. No se atribuye a esta campaña una prueba exhaustiva de eventos reales de visibilidad |
| RF16 Persistencia | Diez métodos del KPI y backup-result.json | Recuperación automatizada comprobada; procedimiento manual no acreditado |
| RF17 Tutorial | verify-features.cjs y ui-result.txt | Seis piezas y flujos automatizados comprobados |
| RF18 Modos | FeatureTest scenario8/9 y undo/redo solo en web | Separación de modos y flujos comprobados |
| RF19 Dificultad | FeatureTest scenario6/7 y cinco niveles en web | Legalidad, no fuerza ajedrecística ni garantía general de latencia |
| RNF01 Estabilidad | 65 métodos JUnit aprobados | Escenarios de la suite; no garantía de ausencia de regresiones |
| RNF02 Trayectorias | Ambos métodos de BoardPathTest | Aprobado en escenarios ejecutados |
| RNF03 Dependencias | Fronteras y colaboradores descritos en arquitectura | Contraste estructural; no es una métrica de cobertura |
| RNF04 Uso local | Ejecución Java/web en campañas registradas | No se ensayó arranque offline en todos los equipos |
| RNF05 Teclado | Enter sobre e2/e4 en browser-results.json | Parcial; no es evaluación integral de accesibilidad |
| RNF06 Adaptabilidad | verify-features.cjs y captura de 390 × 844 | Escenario móvil comprobado; otros tamaños/navegadores no garantizados |
| RNF07 Rendimiento | CSV, JFR y DevTools | Medido en un equipo; ver metodología y límites |
| DOC01 UML | API compilada: 38 tipos y 196 miembros; fuente y PDF actualizados con Memento | Board.Memento es un tipo anidado, no un archivo superior nuevo |
| DOC02 Documentación | Informe y documentación técnica | Contenido actualizado; revisión independiente pendiente |

Fuentes: [campaña Java](evidencias/2026-10-31-etapa4-83903e2/README.md), [campaña web y recuperación](evidencias/2026-10-31-etapas5-6-d37cbe4/README.md), [UML](evidencias/2026-10-31-revision-uml.md) y [rendimiento](rendimiento.md). Los pendientes se mantienen en [integridad](integridad-y-cierre.md).

## Contrato de Memento y RF06

| Método nuevo | Comprobación | Resultado 2/11 |
|---|---|---|
| BoardMementoTest#restoresPiecesMovedFlagsAndRuleMetadata | Piezas, marcas, casillas vacías, al paso y contador | Aprobado |
| BoardMementoTest#independentSnapshotsCanBeRestoredRepeatedly | Capturas independientes, sin alias de matriz y reutilizables | Aprobado |
| BoardMementoTest#nullSnapshotIsRejectedBeforeChangingTheBoard | Rechazo antes de mutar | Aprobado |

Los escenarios L07–L10 de Command y jugadas especiales también pasaron con Memento. Los diez métodos del KPI histórico de recuperación se mantienen sin inflar su denominador con estas pruebas nuevas. [Evidencia actual](evidencias/2026-11-02-memento/README.md).

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
