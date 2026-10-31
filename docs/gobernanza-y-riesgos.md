# Gobernanza y riesgos de BLOCKMATE

> Actualización de seguimiento: las etapas 5 y 6 ya incorporan mediciones, pruebas de UI/consola/HTTP, un simulacro automatizado de respaldo y documentos revisados. Los pendientes descritos en la campaña original se conservan como registro histórico; consultar el [estado vigente de incidencias](registro-incidencias.md) y el [cierre técnico](integridad-y-cierre.md). La revisión humana y el ensayo del grupo siguen pendientes.

Etapa 3. Preparado el 31 de octubre de 2026 para la presentación del **6 de noviembre de 2026**. Rama de trabajo: `RAMA-VICTORIA`. Base de código revisada: `d90061e81c85b010f1a691bd7797cd20f55fc16d`.

Este documento propone cómo organizar cambios, revisar entregas y tratar los riesgos del juego. Distingue controles observados en el código de procedimientos todavía pendientes de adoptar o ejecutar. Publicarlo no significa que el grupo haya aprobado las políticas ni que los riesgos estén resueltos.

## Responsabilidades y decisiones

Cualquier integrante puede proponer un cambio, indicando el problema, su alcance, el criterio de aceptación y la evidencia esperada. Los nombres se acuerdan en el grupo; el nombre de la rama no asigna automáticamente todas las tareas a Victoria.

| Rol propuesto | Responsabilidad | Asignación |
|---|---|---|
| Autor de cada cambio | Implementar o documentar, explicar decisiones y aportar evidencia | Integrante que tome la tarea |
| Revisor de cada cambio | Contrastar resultado, código y evidencia; debe ser distinto del autor | A acordar antes de integrar |
| Coordinación de entrega | Confirmar versión de presentación, pendientes y aceptación de riesgos | A acordar |
| Referente de núcleo y pruebas | Seguir riesgos de reglas, sesión y calidad | A acordar |
| Referente de persistencia y respaldo | Preparar copias y comprobar recuperación | A acordar |
| Referente de interfaz | Comprobar pantallas y flujos de usuario | A acordar |
| Referente de entorno y rendimiento | Preparar ejecución y mediciones de IA | A acordar |
| Referente de documentación | Mantener UML, informe y presentación consistentes | A acordar |

Una persona puede asumir más de un rol, pero no aprobar su propio cambio. Si no hay otro revisor disponible, el cambio queda pendiente de integración. La aceptación de riesgos que afecten la entrega debe quedar registrada por la coordinación con el acuerdo del grupo.

## Política de cambios y revisión

1. Registrar la tarea y su criterio de aceptación en el backlog o descripción del cambio; vincular el riesgo si corresponde.
2. Trabajar en una rama y comprobar el commit de partida. Esta entrega documental se prepara en `RAMA-VICTORIA`.
3. Mantener los cambios acotados y describir qué cambió, por qué y cómo se verificó. No mezclar resultados de una copia local antigua con los de GitHub.
4. Para código, ejecutar pruebas pertinentes; antes de entregar, la etapa 4 debe ejecutar `mvn clean test` sobre la versión acordada. Para documentación, verificar referencias y afirmaciones. Un cambio de API requiere revisar UML y regenerar su PDF.
5. Pedir revisión a otra persona mediante un pull request o registro equivalente. Resolver observaciones o dejarlas como pendientes explícitos con responsable.
6. Integrar en `main` únicamente tras esa revisión y el acuerdo correspondiente. No reescribir el historial compartido ni forzar cambios para ocultar errores.
7. Ante una regresión integrada, identificar el commit, proponer una reversión trazable y verificarla antes de volver a entregar. Conservar el historial del incidente.

**Situación observada:** GitHub informó `protected: false` para `main` y `RAMA-VICTORIA` al consultar ambas ramas el 31/10/2026. Estas políticas son organizativas; no se afirma que una regla técnica de GitHub obligue a cumplirlas. No se cambió la configuración del repositorio.

## Condiciones propuestas de entrega

- Versión identificada por commit y documentación que describa esa misma versión.
- Diagramas consistentes con las clases, según el criterio comunicado por el docente.
- Revisión por otra persona registrada; pruebas comprometidas aprobadas con informes de la etapa 4.
- Sin defectos críticos abiertos: se consideran críticos la pérdida no recuperable de datos, una regla que impide jugar correctamente o la imposibilidad de iniciar la demostración acordada.
- Riesgos altos con tratamiento y decisión explícita; no se cierran por el solo hecho de redactar una política.
- Limitaciones conocidas explicadas durante la defensa, sin presentar objetivos o mediciones históricas como resultados actuales.

## Respaldo del proyecto y las partidas

Git conserva archivos versionados y su historial. **No respalda automáticamente la partida:** [.gitignore](../.gitignore) excluye `data/`, `build/`, `target/` y `*.log`.

Procedimiento propuesto para el referente de respaldo:

1. Antes de una prueba que reinicie partidas o de un cambio de versión, guardar la partida y detener el servidor.
2. Copiar `data/current-match.properties` a una carpeta de respaldo separada del proyecto y de las carpetas que limpian las herramientas. Si se configuró otra ruta, respaldar esa ruta. No subir partidas personales a GitHub.
3. Conservar nombre con fecha, versión del programa y tamaño; cuando sea posible, registrar un hash para detectar cambios. Confirmar que la copia existe y puede leerse.
4. Para el proyecto, confirmar que los commits necesarios están en el remoto y conservar una copia independiente del código de la versión de entrega y de los archivos de trabajo todavía no versionados.
5. Mantener al menos las dos últimas copias verificadas hasta después de la presentación. Antes de reemplazar una copia buena, verificar la nueva.
6. Registrar fecha, responsable y resultado sin publicar rutas personales ni contenidos innecesarios.

Frecuencia propuesta: al terminar una sesión de trabajo con cambios relevantes, antes de pruebas destructivas para la partida y antes de presentar. La escritura atómica del adaptador reduce algunos fallos de guardado, pero no sustituye este respaldo.

## Recuperación ante un fallo

1. Detener el servidor y anotar el error, la versión y la operación que lo disparó.
2. Preservar por separado el archivo problemático y, si existe, su temporal. No borrarlos ni sobrescribirlos para intentar arrancar.
3. Elegir la última copia conocida y probar **una copia de ella**, con una versión compatible del programa, un puerto libre y una ruta de guardado de prueba. No usar el archivo personal como escenario de comprobación.
4. Comprobar tablero, turno, historial, modo, dificultad, puntos, deshacer/rehacer y tiempo acumulado. Registrar qué estado se recuperó y qué movimientos se perdieron, si los hubo.
5. Solo tras comprobar la recuperación y con el servidor detenido, restaurar la copia validada a la ruta de uso. Conservar el archivo fallido para investigar.
6. Si no hay copia válida, registrar la pérdida y acordar con el usuario de esa partida si inicia otra. No prometer reconstruir movimientos que no quedaron guardados.
7. Abrir o actualizar el riesgo y volver a evaluar su nivel con evidencia.

Este procedimiento todavía no se ejecutó como simulacro en esta etapa. Las pruebas de archivo existentes y sus resultados históricos no equivalen a comprobar este proceso de respaldo y restauración.

## Uso de IA generativa

- Usar la asistencia para proponer explicaciones, documentación, pruebas o código; un integrante debe comprender y contrastar el resultado antes de aceptarlo.
- Registrar herramienta, propósito, archivos afectados y verificación realizada. No atribuir una revisión humana, una prueba aprobada o una medición que no ocurrió.
- No introducir claves, credenciales, datos personales innecesarios ni material cuya autorización de uso no esté clara.
- Verificar dependencias, atribuciones y recursos externos que se incorporen; no asumir que una respuesta generada constituye evidencia.
- Si el grupo no puede explicar una solución durante la defensa, revisarla y simplificarla antes de presentarla.

**Asistencia registrada en este avance:** Codex asistió con los documentos de las etapas 1 a 3 y con el contraste y corrección del UML/PDF. Se compiló una copia temporal del código y se comparó su API pública; no se modificó la lógica del juego ni se ejecutó una nueva suite Maven. La revisión humana de estos entregables sigue pendiente.

## Escala de evaluación

La escala propuesta es ordinal: el producto sirve para priorizar, no representa una probabilidad exacta ni una pérdida estadística. El grupo debe validarla.

| Valor | Probabilidad estimada dentro de esta entrega | Impacto |
|---|---|---|
| 1 | Baja: escenario acotado y controles identificados | Bajo: corrección localizada sin afectar partida ni presentación |
| 2 | Media: escenario plausible con controles parciales o sin evidencia reciente | Medio: retrabajo o interrupción recuperable de una función |
| 3 | Alta: exposición frecuente o controles insuficientes | Alto: pérdida de partida, reglas esenciales incorrectas o entrega comprometida |

Prioridad = P × I: **1–2 baja**, **3–4 media**, **6–9 alta**. Un defecto crítico se atiende aunque su puntuación de riesgo sea menor.

La evaluación inicial considera el escenario sin los controles enumerados. El residual actual es una **estimación provisional después de considerar solo controles existentes**; no descuenta propuestas sin ejecutar. El objetivo residual indica el nivel a reevaluar después del tratamiento, no un resultado alcanzado.

## Registro resumido de riesgos

Todos los responsables son roles propuestos con integrante por asignar. Las justificaciones, tratamientos y evidencia se detallan debajo.

| ID | Riesgo | Inicial P/I/total | Residual actual P/I/total | Objetivo posterior P/I/total | Responsable propuesto | Estado |
|---|---|---|---|---|---|---|
| R01 | Pérdida o corrupción de guardado | 2/3/6 alta | 2/3/6 alta | 1/3/3 media | Persistencia y respaldo | Abierto; falta simulacro |
| R02 | Movimiento ilegal o estado inconsistente | 2/3/6 alta | 1/3/3 media | 1/3/3 media | Núcleo y pruebas | Suite Java aprobada en etapa 4; revisión integral pendiente |
| R03 | Regresión de interfaz | 2/2/4 media | 2/2/4 media | 1/2/2 baja | Interfaz | Abierto; falta revisión actual |
| R04 | No poder iniciar en el equipo de presentación | 2/3/6 alta | 2/3/6 alta | 1/3/3 media | Entorno y rendimiento | Abierto; falta ensayo en el equipo |
| R05 | Demora de respuesta de la IA | 3/2/6 alta | 2/2/4 media | 1/2/2 baja | Entorno y rendimiento | Control parcial; faltan mediciones |
| R06 | Diagramas o documentos de otra versión | 3/3/9 alta | 2/3/6 alta | 1/3/3 media | Documentación | UML revisado; falta revisión de la entrega completa |
| R07 | Integración incorrecta o pérdida de cambios | 2/3/6 alta | 2/3/6 alta | 1/3/3 media | Coordinación de entrega | Abierto; adopción del flujo pendiente |
| R08 | Afirmaciones o soluciones de IA sin verificar | 2/3/6 alta | 2/3/6 alta | 1/3/3 media | Autor y revisor de cada cambio | Abierto; falta revisión humana |

### R01 Pérdida o corrupción del guardado

**Causa:** corte durante una escritura, permisos insuficientes, archivo dañado, pruebas sobre la partida personal o dos servidores sobre el mismo archivo. **Consecuencia:** no poder reabrir la partida o perder jugadas.

**Control existente:** [FileGameRepository](../src/main/java/ar/edu/ajedrez/app/persistence/FileGameRepository.java) escribe un temporal y usa reemplazo atómico cuando se admite; rechaza formatos inválidos. [MatchSession](../src/main/java/ar/edu/ajedrez/core/session/MatchSession.java) restaura la sesión previa cuando recibe PersistenceException. [FeatureTest](../src/test/java/ar/edu/ajedrez/FeatureTest.java), escenarios 10, 11, 14 y 16, contiene comprobaciones de recuperación y fallos.

**Tratamiento:** mitigar mediante el procedimiento de respaldo, un simulacro de restauración y pruebas aisladas; un servidor por archivo. **Seguimiento propuesto:** 3 de noviembre. **Evidencia de cierre del tratamiento:** registro de copia y recuperación, versión, escenario, resultado y pérdida observada.

**Residual:** sigue alto por falta de respaldo verificado y posibilidad de errores operativos; los controles de código no cubren todos esos escenarios. Incluso tras el tratamiento puede perderse actividad posterior al último guardado.

### R02 Movimiento ilegal o estado inconsistente

**Causa:** regresión de validación o restauración de jugadas especiales. **Consecuencia:** partida inválida, puntos o turnos incorrectos.

**Control existente:** [MoveValidator](../src/main/java/ar/edu/ajedrez/core/validation/MoveValidator.java), CheckDetector y Command centralizan validación y restauración. [ChessTest](../src/test/java/ar/edu/ajedrez/ChessTest.java), [ValidationTest](../src/test/java/ar/edu/ajedrez/ValidationTest.java) y [BoardPathTest](../src/test/java/ar/edu/ajedrez/BoardPathTest.java) contienen escenarios de reglas e invariantes.

**Tratamiento:** mitigar ejecutando la suite sobre el commit de entrega y relacionando requisitos con escenarios; investigar cualquier fallo antes de integrar. **Seguimiento propuesto:** 3 de noviembre. **Evidencia:** informes Maven fechados y matriz de requisitos de la etapa 4.

**Residual:** estimación media por validación central y pruebas existentes, sin afirmar que pasaron hoy. Permanece la posibilidad de escenarios no cubiertos; no se espera llegar a riesgo cero.

### R03 Regresión de interfaz

**Causa:** cambios de CSS o JavaScript que afecten pantallas angostas, tutorial, diálogos o controles. **Consecuencia:** no poder completar un flujo o mostrarlo correctamente.

**Control existente:** scripts [verify-ui.cjs](../tools/verify-ui.cjs) y [verify-features.cjs](../tools/verify-features.cjs); este último incluye un viewport de 390 px. Su existencia no certifica la interfaz de la versión actual.

**Tratamiento:** mitigar con ejecución aislada y revisión visual en escritorio y 390 px; comprobar tutorial, promoción, guardado y undo/redo. Los scripts crean partidas y no deben apuntar a una partida personal. **Seguimiento propuesto:** 3 de noviembre. **Evidencia:** capturas y resultados con versión de navegador, viewport y commit.

**Residual:** media hasta completar esa comprobación; persistirán diferencias entre navegadores o equipos no ensayados.

### R04 Fallo al iniciar la demostración

**Causa:** JDK o Maven ausentes, dependencias no descargadas, puerto ocupado o carpeta web faltante. **Consecuencia:** no poder presentar el juego.

**Control existente:** [README](../README.md), [pom.xml](../pom.xml) y [run.ps1](../run.ps1) describen y configuran Java 21/Maven. El JAR no incluye por sí solo toda la interfaz web.

**Tratamiento:** mitigar ensayando la versión acordada en el equipo de presentación, con la carpeta web, dependencias descargadas y un puerto disponible. Preparar una copia comprobada del proyecto; no depender de una descarga de último momento. **Seguimiento propuesto:** 5 de noviembre. **Evidencia:** registro de inicio y una jugada, entorno y commit.

**Residual:** alto mientras no haya ensayo en el equipo real. La compilación temporal realizada al revisar UML no demuestra que ese equipo esté listo.

### R05 Demora de la IA

**Causa:** búsqueda de jugadas en una posición costosa o equipo lento. **Consecuencia:** espera excesiva o interrupción de la demostración.

**Control existente:** [MinimaxAi](../src/main/java/ar/edu/ajedrez/core/ai/MinimaxAi.java) limita nodos, profundidad y presupuesto de búsqueda, con poda alfa-beta. Los presupuestos no son una garantía estricta del tiempo total.

**Tratamiento:** mitigar midiendo una posición reproducible en nivel alto; acordar equipo, repeticiones y umbral antes de juzgar el resultado. Como contingencia de demostración, usar un nivel menor o dos jugadores, explicando la limitación. **Seguimiento propuesto:** 4 de noviembre. **Evidencia:** metodología, tiempos y registros de la etapa 5.

**Residual:** media por los límites existentes; la reducción a baja requiere mediciones satisfactorias. No se promete rendimiento universal.

### R06 Documentación y UML de otra versión

**Causa:** cambios de clases sin actualizar diagramas o uso de una copia local antigua. **Consecuencia:** incumplir el criterio del docente y defender una API que no corresponde.

**Control existente en este avance:** [revisión de patrones](revision-de-patrones.md), commit base identificado y UML/PDF contrastados con los 37 tipos públicos. La corrección incluye los métodos de presencia y los datos del cronómetro.

**Tratamiento:** mitigar revisando también Word y presentación, y repetir el contraste tras cualquier cambio de API. **Seguimiento propuesto:** 5 de noviembre. **Evidencia:** lista de documentos y diagramas comprobados, versión y revisión por otro integrante.

**Residual:** alta para el conjunto de la entrega, porque no se comprobó nuevamente cada documento y diapositiva. No se cierra este riesgo por haber corregido solo el UML independiente.

### R07 Integración o pérdida de cambios

**Causa:** editar una versión antigua, sobrescribir trabajo ajeno o integrar sin revisión. **Consecuencia:** regresiones, pérdida de trabajo y documentación mezclada.

**Control existente:** historial Git y rama separada. Las ramas consultadas no estaban protegidas; no se supone aprobación automática ni revisión obligatoria configurada.

**Tratamiento:** mitigar aplicando la política de ramas y revisión; comprobar la base, conservar commits y evitar reescribir el historial compartido. **Seguimiento propuesto:** antes de cada integración. **Evidencia:** cambio identificado, revisión y decisión de integración.

**Residual:** alta mientras el proceso no esté adoptado; la existencia de Git permite rastrear cambios, pero no evita por sí sola una mala integración.

### R08 Uso de IA sin verificación

**Causa:** aceptar código, explicaciones, resultados o referencias generadas sin entenderlos ni contrastarlos. **Consecuencia:** errores, afirmaciones falsas o imposibilidad de defender decisiones.

**Control existente:** en este avance se distingue revisión estática de ejecución de tests y se identifican las fuentes del código y los límites del UML. Eso no sustituye la comprensión del grupo.

**Tratamiento:** mitigar con la política de IA, revisión por otra persona y explicación de cada decisión en el ensayo. **Seguimiento propuesto:** 5 de noviembre. **Evidencia:** registro de asistencia, archivos revisados y observaciones del revisor.

**Residual:** alto hasta la revisión humana; no basta que la herramienta declare que verificó su propia respuesta.

## Seguimiento y aceptación del riesgo residual

Revisar este registro ante un cambio relevante, después de cada comprobación y antes de presentar. El responsable registra la evidencia; otro integrante contrasta el resultado. La coordinación acuerda la aceptación residual, o conserva el riesgo abierto con un tratamiento adicional. No reducir P o I solo porque venció la fecha.

| ID de riesgo | Evidencia y commit | P/I residual reevaluado | Decisión y justificación | Responsable y revisor | Próxima fecha |
|---|---|---|---|---|---|
| R01 a R08 | Pendiente por cada riesgo | Conservar la estimación provisional hasta aportar evidencia | Sin aceptación grupal registrada | A asignar | Según los seguimientos propuestos |

La etapa documental queda preparada. Para adoptarla y cerrarla como trabajo del grupo faltan: asignar personas a los roles, acordar políticas y escala, revisar estimaciones y registrar aprobación. La ejecución de pruebas, mediciones y revisión final corresponde a las etapas 4, 5 y 6; no se presenta como realizada aquí.


## Seguimiento posterior de la etapa 4

El 31/10/2026 se ejecutó la suite Java del commit `83903e22eec288bd0823b7172d951b78610d9fdc`: 62 pruebas aprobadas, sin fallos, errores u omisiones. Ver [calidad y métricas](calidad-y-metricas.md), [matriz](matriz-requisitos-pruebas.md) y [registro de incidencias](registro-incidencias.md).

- **R01:** diez escenarios automatizados de persistencia, restauración, migración o reversión aprobados; el respaldo manual y su simulacro continúan pendientes. Se mantiene residual 2/3/6.
- **R02:** los escenarios actuales de reglas e invariantes fueron ejecutados y aprobados. Se mantiene residual 1/3/3 por posibles casos no cubiertos; no se cierra el riesgo ni se registra aceptación grupal.
- **R03 y R04:** no se ejecutó una revisión nueva del navegador, consola interactiva ni del equipo de presentación. Se mantienen sus estimaciones.
- **R05:** los tiempos impresos por los tests no reemplazan el profiler. No se reduce su residual.
- **R06, R07 y R08:** revisión documental integral, adopción del flujo y revisión humana siguen pendientes.

No se usó el porcentaje de tests aprobados para disminuir automáticamente todos los riesgos. Los responsables personales y la aceptación de riesgos siguen a cargo del grupo. Las notas anteriores de “sin ejecución” describen la situación de la etapa 3; este apartado registra la comprobación posterior.

