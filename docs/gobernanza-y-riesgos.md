# Gobernanza y riesgos de BLOCKMATE

Actualizado el 2/11/2026 con las campañas disponibles del 31/10 y la integración en main. Las políticas, roles y estimaciones siguientes son propuestas del trabajo; falta adopción y aceptación grupal. El [estado de revisión](integridad-y-cierre.md) concentra acciones pendientes.

## Cambios y revisión

1. Registrar problema, alcance, criterio de aceptación y evidencia esperada.
2. Identificar el commit base y trabajar en una rama con cambios acotados.
3. Ejecutar verificaciones pertinentes; cambios de API requieren actualizar UML y PDF. Conservar resultados fechados.
4. Solicitar revisión a una persona distinta del autor y registrar observaciones.
5. Integrar tras la decisión correspondiente. No forzar ni reescribir el historial compartido para ocultar fallos.
6. Ante una regresión, identificar el cambio, proponer una reversión trazable y verificarla.

La consulta de GitHub del 31/10 registró main y RAMA-VICTORIA sin protección; no se presenta como configuración actual ni como garantía de revisión obligatoria. La integración autorizada del 2/11 no acredita revisión independiente.

## Roles propuestos

Autor del cambio, revisor distinto del autor, coordinación, núcleo/pruebas, persistencia, interfaz, entorno/rendimiento y documentación. El grupo acuerda las personas y puede combinar roles sin aprobar el propio cambio. No hay asignaciones nominales registradas.

## Respaldo y recuperación

Git conserva código e historial, pero `.gitignore` excluye `data/`, `build/`, `target/` y logs. No respalda la partida.

1. Guardar y detener el servidor antes de copiar `data/current-match.properties`, o la ruta configurada.
2. Conservar la copia fuera de las carpetas limpiadas por las herramientas, con fecha, versión, tamaño y preferentemente hash. Mantener al menos las dos últimas copias verificadas.
3. Ante un fallo, preservar el archivo problemático y su temporal sin sobrescribirlos.
4. Probar una copia del respaldo con versión compatible, puerto libre y archivo separado; comprobar tablero, turno, historial, modo, nivel, puntos, redo y tiempo.
5. Solo después de validar, detener el servidor y restaurar la copia a la ruta de uso. Registrar pérdidas observadas y conservar el archivo fallido para investigar.

Frecuencia propuesta: al terminar una sesión relevante, antes de pruebas que reinicien partidas y antes de cambiar versión. Usar un solo servidor por archivo. El reemplazo atómico reduce fallos de escritura, pero no sustituye una copia independiente. Hay un [simulacro automatizado](integridad-y-cierre.md); no consta adopción del procedimiento manual.

## Uso de IA generativa

Identificar asistencia, fuentes, verificaciones y limitaciones; no aceptar código o conclusiones sin comprenderlos. No publicar claves, partidas personales ni datos innecesarios. No atribuir revisiones humanas a la herramienta. [Declaración y estado](integridad-y-cierre.md).

## Escala de riesgos

P e I usan una escala propuesta de 1 a 3: probabilidad baja/media/alta e impacto bajo/medio/alto. Prioridad = P × I: 1–2 baja, 3–4 media y 6–9 alta. No expresa probabilidades exactas. Un defecto crítico se atiende aunque la puntuación sea menor.

La valoración inicial corresponde al escenario sin los controles. El residual conserva las estimaciones provisionales de la campaña original: las nuevas evidencias se incorporan, pero no se reducen puntuaciones automáticamente ni se inventa aceptación grupal. El objetivo solo orienta una reevaluación futura.

| ID | Riesgo y causa | Consecuencia | Inicial P/I/total | Residual provisional | Objetivo | Control y evidencia disponibles | Tratamiento y responsable propuesto |
|---|---|---|---|---|---|---|---|
| R01 | Pérdida/corrupción por corte, archivo incorrecto o múltiples servidores | Pérdida de jugadas o imposibilidad de recuperar | 2/3/6 | 2/3/6 | 1/3/3 | Temporal, reemplazo atómico, reversión, tests y copia en frío restaurada | Mitigar con copias independientes y procedimiento manual; persistencia |
| R02 | Regresión de validación o restauración | Partida, turno o puntos incorrectos | 2/3/6 | 1/3/3 | 1/3/3 | Validador, Command y suite histórica de 62 y suite de Memento de 65 aprobada el 2/11 | Mitigar con escenarios y revisión de reglas; núcleo/pruebas |
| R03 | Regresión CSS/JS o diferencias entre navegadores | Flujo de interfaz inutilizable | 2/2/4 | 2/2/4 | 1/2/2 | Flujos web, teclado parcial y 390 px comprobados | Mitigar con revisión manual y tamaños/navegadores requeridos; interfaz |
| R04 | JDK/Maven ausente, dependencias, puerto o carpeta web | Fallo al iniciar el juego | 2/3/6 | 2/3/6 | 1/3/3 | Instrucciones y campañas ejecutadas en entorno registrado | Mitigar verificando arranque offline en equipo de uso; entorno |
| R05 | Búsqueda costosa o equipo lento | Espera excesiva | 3/2/6 | 2/2/4 | 1/2/2 | Profundidad/nodos/tiempo acotados y medición repetida de IA | Mitigar midiendo escenarios requeridos; usar nivel menor como contingencia; rendimiento |
| R06 | Documentos o UML de otra versión | Describir contratos inexistentes | 3/3/9 | 2/3/6 | 1/3/3 | Contraste UML y consolidación documental del 2/11 | Mitigar con revisión independiente y actualización tras cambios de API; documentación |
| R07 | Base antigua, sobrescritura o integración sin revisar | Pérdida de cambios o regresión | 2/3/6 | 2/3/6 | 1/3/3 | Historial Git, merge fast-forward sin conflictos y pruebas posteriores | Mitigar adoptando revisión cruzada; coordinación |
| R08 | Aceptar IA sin comprender o contrastar | Afirmaciones o soluciones erróneas | 2/3/6 | 2/3/6 | 1/3/3 | Asistencia declarada y registros contrastables | Mitigar mediante revisión por otra persona y comprensión del código; autor/revisor |

Estado de R01–R08: **abiertos a reevaluación y aceptación**, con roles por asignar. Ninguna campaña demuestra riesgo cero. Persisten casos no cubiertos, diferencias de equipo, fallos operativos y límites de diagnóstico de memoria.

Fuentes: [calidad](calidad-y-metricas.md), [matriz](matriz-requisitos-pruebas.md), [rendimiento](rendimiento.md), [incidencias](registro-incidencias.md) y [registro UML](evidencias/2026-10-31-revision-uml.md).

## Reevaluación

Revisar ante cambios, incidencias y nuevas comprobaciones. Registrar por riesgo: evidencia y commit, P/I residual, decisión y justificación, persona responsable, revisor y próxima fecha. El grupo debe aceptar el residual o indicar un tratamiento adicional; no reducirlo solo por haber redactado un documento o vencido una fecha.
