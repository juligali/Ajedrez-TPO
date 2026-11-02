# Registro de incidencias de BLOCKMATE

Hallazgos de la campaña del 31/10/2026 e incidencias documentales identificadas el 2/11. Este registro conserva correcciones; los pendientes de revisión se mantienen únicamente en [integridad](integridad-y-cierre.md).

## Incidencias identificadas

| ID | Hallazgo | Gravedad | Corrección | Estado |
|---|---|---|---|---|
| I01 | El test móvil pulsaba #new-button oculto a 390 px | Media, instrumento de prueba | Usar #compact-new cuando es visible; la ejecución completa posterior pasó | Corregida en la campaña del 31/10 |
| I02 | Informe y materiales anteriores indicaban 53 pruebas y reloj durante cierres | Media, documentación | Se actualizaron a 62 y tiempo activo en la campaña del 31/10 | Corrección histórica registrada |
| I03 | README y tablas mezclaban cifras y pendientes de distintas etapas | Media, documentación | Se consolidó el estado, se normalizaron requisitos y se separaron registros históricos | Corregida documentalmente el 2/11; revisión por otro integrante pendiente |

La campaña Java del 31/10 registró 62 pruebas aprobadas y 0 defectos críticos confirmados en su alcance. La consulta de issues de esa fecha devolvió 0 abiertas; no se presenta como una consulta actual. El 2/11 se repitió la suite después del merge con el mismo resultado. La suite posterior con Memento aprobó 65 pruebas; ver [evidencia](evidencias/2026-11-02-memento/README.md). Esto no demuestra ausencia de errores fuera de los escenarios probados.

El crecimiento de heap JavaScript de 104.280 bytes es una observación de rendimiento, no una fuga confirmada. Su contexto está en [rendimiento](rendimiento.md).

## Registrar un nuevo hallazgo

Anotar ID, fecha, commit, reproducción, resultado esperado y observado, gravedad, evidencia, responsable, estado y comprobación del cierre. Se considera crítico perder datos sin recuperación, romper una regla esencial o impedir iniciar el juego. No cerrar una incidencia porque pasó una prueba ajena al problema.
