# Calidad y métricas de BLOCKMATE

> Actualización de seguimiento: las etapas 5 y 6 ya incorporan mediciones, pruebas de UI/consola/HTTP, un simulacro automatizado de respaldo y documentos revisados. Los pendientes descritos en la campaña original se conservan como registro histórico; consultar el [estado vigente de incidencias](registro-incidencias.md) y el [cierre técnico](integridad-y-cierre.md). La revisión humana y el ensayo del grupo siguen pendientes.

Etapa 4, ejecutada el **31 de octubre de 2026** sobre el commit `83903e22eec288bd0823b7172d951b78610d9fdc` de `RAMA-VICTORIA`.

**Resultado:** `mvn -B -ntp clean test` terminó con **62 pruebas aprobadas, 0 fallos, 0 errores y 0 omitidas**. Las fuentes y el POM utilizados coincidieron con los blobs de ese commit antes y después de ejecutar Maven. No se modificó el programa, las pruebas ni sus aserciones.

Esta medición comprueba los escenarios Java existentes. No acredita por sí sola la interfaz, todas las posiciones posibles de ajedrez, la recuperación manual desde un respaldo ni el rendimiento de la presentación.

## Ejecución y evidencia

- Inicio: 31/10/2026, 12:18:43; final: 12:19:01, hora de Argentina (UTC−03:00).
- Entorno: Windows 11, build 26200, AMD64; Oracle JDK 21.0.6; Maven 3.9.16; JUnit 5.11.4; Surefire 3.5.2.
- Ejecución en una carpeta aislada con los 37 archivos de producción, 5 de pruebas y `pom.xml`. Las pruebas de disco usan carpetas temporales; no se inició un servidor sobre una partida personal.
- Resultado de proceso: código 0, BUILD SUCCESS.
- Tiempo total informado por Maven: 15,420 segundos, incluyendo el proceso de construcción. No es una medición de latencia del juego ni de consumo de CPU.

| Suite | Ejecutadas | Aprobadas | Fallos | Errores | Omitidas |
|---|---:|---:|---:|---:|---:|
| ActivityClockTest | 9 | 9 | 0 | 0 | 0 |
| BoardPathTest | 2 | 2 | 0 | 0 | 0 |
| ChessTest | 31 | 31 | 0 | 0 | 0 |
| FeatureTest | 16 | 16 | 0 | 0 | 0 |
| ValidationTest | 4 | 4 | 0 | 0 | 0 |
| **Total** | **62** | **62** | **0** | **0** | **0** |

Se conservan el [registro de ejecución](evidencias/2026-10-31-etapa4-83903e2/maven-clean-test.txt), los cinco informes XML de Surefire, el [resumen calculado](evidencias/2026-10-31-etapa4-83903e2/resumen.json) y el [manifiesto de fuentes](evidencias/2026-10-31-etapa4-83903e2/fuentes-verificadas.json). El [README de evidencia](evidencias/2026-10-31-etapa4-83903e2/README.md) explica reproducción y anonimización. Los métodos y resultados de los XML se conservaron; se retiraron propiedades del entorno que exponían rutas locales e identificadores de equipo o usuario.

Las cifras anteriores del repositorio (47, 53 y 62 pruebas) pertenecen a distintas revisiones. Esta ejecución queda identificada por su propio commit, fecha y entorno; no sustituye retroactivamente esos registros.

## Indicadores medidos

Las metas, responsables por rol y periodicidad siguientes son propuestas para que el grupo las adopte. Los resultados de esta ejecución sí fueron obtenidos de evidencia real.

| Indicador | Cálculo en esta ejecución | Resultado | Alcance |
|---|---|---|---|
| KPI Pruebas exitosas | 62 / 62 × 100 | **100 %** | Suite Java completa existente |
| KPI Reglas verificadas | 13 / 13 × 100 | **100 %** | Catálogo operativo v1 de reglas L01–L13, con todos sus escenarios asociados aprobados |
| KPI Recuperación automatizada | 10 / 10 × 100 | **100 %** | Diez escenarios de persistencia, restauración, migración o reversión seleccionados explícitamente |
| KRI Defectos críticos pendientes | Conteo del registro de esta campaña | **0 registrados** | No equivale a ausencia de defectos fuera del alcance probado |

### KPI Pruebas exitosas

**Meta:** detectar regresiones antes de integrar. **Pregunta:** ¿qué proporción de escenarios ejecutados terminó sin fallo ni error? **Fórmula:** aprobadas / ejecutadas × 100; ejecutadas = descubiertas − omitidas. Las omitidas se informan aparte y no pueden ocultarse para declarar una entrega lista. Si no se ejecutó ninguna, informar “sin medición”.

**Fuente:** XML Surefire y resumen de esta ejecución. **Responsable propuesto:** referente de núcleo y pruebas; persona por asignar. **Objetivo:** 100 % de la suite comprometida y 0 omitidas. **Periodicidad:** antes de integrar cambios relevantes y antes de la entrega. **Decisión asociada:** investigar cualquier fallo, error u omisión no justificada; no eliminar tests ni aserciones para mejorar el porcentaje.

### KPI Reglas verificadas

**Meta:** vincular requisitos de reglas con pruebas concretas. **Pregunta:** ¿cuántos grupos del catálogo tienen todos sus escenarios asociados aprobados? **Fórmula:** grupos de reglas con todos sus escenarios aprobados / grupos comprometidos en el catálogo × 100. Un escenario omitido o fallido impide aprobar su grupo.

**Fuente:** [matriz](matriz-requisitos-pruebas.md), catálogo L01–L13 y XML. **Responsable propuesto:** referente de núcleo y pruebas, con validación del revisor. **Objetivo:** 13/13 para este catálogo v1, sin reducir el denominador para ocultar pendientes. **Periodicidad:** al cambiar reglas o requisitos y antes de presentar. **Decisión asociada:** añadir o corregir escenarios ante un requisito sin evidencia.

El catálogo fue explicitado en esta etapa y necesita validación grupal; no se presenta como una rúbrica ya acordada con el docente. El 100 % no significa cobertura de líneas ni cumplimiento de todos los requisitos: UI, documentación y pruebas manuales aparecen aparte.

### KPI Recuperación automatizada

**Meta:** controlar que los escenarios de persistencia y restauración se mantengan. **Pregunta:** ¿cuántos escenarios seleccionados recuperan, migran o revierten el estado según sus aserciones? **Fórmula:** escenarios de recuperación aprobados / ejecutados × 100; informar también seleccionados y omitidos. Con denominador cero, “sin medición”.

**Fuente:** los diez métodos listados en la matriz y en [catalogo-indicadores.json](evidencias/2026-10-31-etapa4-83903e2/catalogo-indicadores.json). **Responsable propuesto:** referente de persistencia y respaldo. **Objetivo:** 10/10 y 0 omitidos. **Periodicidad:** al cambiar guardado o sesión y antes de entregar. **Decisión asociada:** no aceptar una regresión de recuperación; investigar antes de integrar.

Siete escenarios usan dobles en memoria y tres usan el adaptador de archivo con directorios temporales. No se ejecutó el simulacro de copia y restauración manual de la etapa 3; ese resultado sigue **sin medición**. No se incorpora artificialmente al denominador de esta suite.

### KRI Defectos críticos pendientes

**Meta:** evitar presentar una versión con defectos críticos conocidos. **Pregunta:** ¿cuántos defectos críticos confirmados siguen abiertos? **Fórmula:** conteo de entradas abiertas clasificadas como críticas en el registro.

**Fuente:** [registro de incidencias](registro-incidencias.md), resultado de la suite y consulta de issues abiertas de GitHub del 31/10/2026, que devolvió 0 issues abiertas. **Responsable propuesto:** coordinación de entrega con autor y revisor. **Objetivo:** 0 críticos abiertos. **Periodicidad:** ante una incidencia y antes de integrar o presentar. **Decisión asociada:** no liberar con un crítico abierto; acordar tratamiento y repetir la verificación afectada.

No se deduce la inexistencia de errores de un resultado verde o de una lista vacía de issues. La evaluación completa del producto sigue pendiente de los escenarios no ejecutados y de revisión humana.

## Efectividad y límites de los escenarios

Además de contar tests, se revisó qué comprueban: hay rechazos sin mutación, restauración de metadatos y capturas, promociones, restricciones del enroque, al paso que descubriría jaque, perft 20/400/8902 y rechazo de un archivo corrupto. Son controles más útiles que contar commits o líneas de código.

No se midió cobertura de código ni se ejecutó mutation testing. Un grupo de reglas puede tener pruebas aprobadas y aun así conservar casos no contemplados. Tampoco se probó un corte físico de energía: los escenarios de reinicio abrupto simulan reconstrucción desde el estado guardado.

Los tiempos por nivel que imprime FeatureTest son observaciones de una sola ejecución funcional. No se usan como garantía, KPI de rendimiento ni sustituto de la etapa 5.

## Pendientes para la entrega

- Revisión por otro integrante y confirmación de catálogo, metas y responsables.
- Comprobación actual de UI, teclado, pantallas de 390 px, tutorial y flujos HTTP/consola.
- Simulacro de respaldo manual sobre copias aisladas.
- Mediciones reproducibles de CPU y memoria en la etapa 5.
- Revisión integral de Word, presentación y equipo de demostración.

La etapa 4 aporta ejecución y métricas trazables del alcance Java. Los pendientes están registrados como brechas de verificación, no como pruebas aprobadas ni defectos confirmados.

