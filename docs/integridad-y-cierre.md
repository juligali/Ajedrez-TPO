# Etapa 6 — Integridad y cierre de la revisión

Fecha de evidencia: **31/10/2026**. Presentación confirmada: **6/11/2026**. Rama de trabajo: **RAMA-VICTORIA**. Base revisada: `d37cbe4c39bd434915e2f822d06195bae3e95a69`.

La preparación técnica y documental de las seis etapas queda disponible para revisión. La revisión por otro integrante, la aceptación de responsables y el ensayo de exposición siguen pendientes. Este documento no los reemplaza ni presenta la revisión asistida como una auditoría independiente.

## Uso declarado de inteligencia artificial

Se utilizó **Codex como asistencia generativa** para leer el repositorio, contrastar el UML con clases compiladas, redactar y corregir documentación, preparar instrumentos de medición y ejecutar verificaciones. La IA local del juego es otra cosa: `MinimaxAi` implementa búsqueda minimax con poda alfa-beta, sin un servicio generativo.

| Trabajo asistido | Comprobación realizada | Límite que debe explicar el grupo |
|---|---|---|
| UML y arquitectura | 37 tipos y 194 miembros públicos declarados contrastados con `javap`; seis vistas | Se omiten miembros privados, heredados y generados; no es una especificación de variantes |
| Calidad | 62 pruebas Java aprobadas, informes Surefire y manifiesto de fuentes | La suite cubre escenarios concretos, no todos los requisitos de manera exhaustiva |
| Rendimiento | CSV, JFR y DevTools en escenarios repetibles | Un equipo; memoria creciente no prueba por sí sola una fuga; no hay mejora antes/después |
| Recuperación | Simulacro automatizado con copia en frío, restauración y comparación del estado | No certifica todos los fallos de disco ni reemplaza un ensayo humano |
| Informe y presentación | Contenido contrastado y revisión visual de las páginas y diapositivas exportadas | El grupo debe comprender y defender las decisiones; no se presume aceptación |

No se atribuye a integrantes del grupo una revisión que no realizaron. No se inventaron firmas, aprobaciones, responsables nominales ni resultados. Los roles y fechas internas propuestos en las etapas 1 y 3 requieren acuerdo. El repositorio contiene el alcance, instrumentos, resultados y limitaciones que permiten verificar las afirmaciones.

## Registro de revisión técnica asistida

Revisor de esta campaña: **Codex, revisión asistida solicitada por Victoria**. No equivale a revisión por pares humanos. Los responsables de seguimiento son roles propuestos, no asignaciones aceptadas.

| Criterio | Evidencia | Resultado | Pendiente / responsable propuesto / seguimiento |
|---|---|---|---|
| Clases y UML coincidentes | `UML-Ajedrez-TPO.puml`, PDF y evidencia de revisión UML | Conforme con la API pública contrastada; código de producción sin cambios posteriores | Revisor del grupo: recorrer seis vistas antes del 5/11 |
| Reglas y sesión | Campaña de etapa 4: 62/62, 0 fallos, errores u omisiones | Conforme en los escenarios ejecutados | Revisor de reglas: elegir ejemplos para la defensa |
| Rendimiento Java y web | `rendimiento.md`, CSV y resumen JFR | Medido, sin optimización del motor | Referente de rendimiento: repetir en equipo final; ampliar memoria JS si persiste la tendencia |
| UI funcional | `verify-features.cjs`, `ui-result.txt` | Tutorial, puntos, mate, reloj finalizado, guardado, recarga, cinco niveles, undo/redo solo y móvil aprobados | Referente UI: ensayo manual del flujo completo antes del 5/11 |
| Teclado y consola | `browser-results.json`, `console-session.txt` | Enter sobre e2/e4 y mover/deshacer/rehacer/salir comprobados | No se afirma auditoría integral de accesibilidad |
| Respaldo y HTTP | `verify-backup.cjs`, `backup-result.json` | Restauración exacta; redo conserva captura y punto; 403 de origen y 409 de revisión | Referente de persistencia: ensayar recuperación en equipo final |
| Dependencias y recursos | `dependencias-y-recursos.md`, metadatos de licencias | Inventario preparado; sin dependencia externa de producción en el POM | Autores: confirmar licencia del proyecto y atribuciones antes de redistribuir |
| Documentos actuales | README, requisitos, arquitectura, Word y presentación | Cifras y reloj corregidos; resultados y limitaciones incorporados | Documentación: revisión por otro integrante antes del 5/11 |
| Revisión humana y defensa | Lista de cierre siguiente | Pendiente | Coordinación: acordar personas, fecha y observaciones |

## Correcciones de esta etapa

- El Word y el PowerPoint todavía describían 53 pruebas y tiempo acumulado durante los cierres. Ahora documentan **62 pruebas y tiempo activo**, con pausas y límites de persistencia.
- Se actualizó la fecha de presentación al 6 de noviembre y se incorporaron rendimiento, integridad y pendientes del grupo.
- `verify-features.cjs` intentaba pulsar `#new-button` oculto a 390 px. Se cambió el selector para usar `#compact-new` cuando es visible. La ejecución completa corregida pasó. No fue necesario modificar el código del juego.
- Se incorporaron instrumentos reproducibles de rendimiento y restauración. No se agregaron dependencias de producción ni se cambiaron las clases; el UML conserva su validez.
- `tools/create_report.py` queda identificado como generador histórico: escribe una salida separada para evitar sobrescribir el informe revisado con contenido anterior.

## Simulacro de respaldo ejecutado

`tools/verify-backup.cjs` usa únicamente `build/backup-drill/` y el puerto 18087. Rechaza iniciar si el puerto está ocupado. Crea e2-e4, d7-d5, e4-d5; deshace la captura y guarda, dejando redo disponible. Detiene su proceso, copia el archivo, inicia y reemplaza la partida de prueba, detiene y restaura el respaldo. Comprueba SHA-256, reinicia y compara el estado completo salvo la revisión HTTP, que se reinicia por diseño. Finalmente rehace y verifica una captura y un punto.

La comparación incluye tablero, turno, estado, modo, dificultad, historial, capturas, puntos, tiempo acumulado y redo. Como el simulacro no abre vistas, el reloj permanece pausado; el avance del reloj corresponde a las pruebas de actividad, no a este escenario. La partida personal no se utilizó.

```powershell
# Después de compilar las clases. Requiere Java 21 y Node.
$env:JAVA_EXE='java'
node tools/verify-backup.cjs
```

El resultado está en `docs/evidencias/2026-10-31-etapas5-6-d37cbe4/backup-result.json`. Una copia atómica no sustituye un respaldo independiente: aquí se probó específicamente restaurar una copia con el servidor detenido.

## Lista de cierre humano

- [ ] Otro integrante revisa código, UML y evidencia, y deja nombre, fecha y observaciones.
- [ ] El grupo acepta responsables, fechas internas y criterios propuestos.
- [ ] Cada integrante explica una decisión, un patrón, una prueba y una limitación sin depender de una respuesta generada.
- [ ] Se reparten las partes de la exposición y se ensaya antes del 6/11.
- [ ] Se inicia la versión acordada en el equipo de presentación y se comprueba una restauración sobre una partida de prueba.
- [ ] Se registra la decisión de integrar o presentar esta rama; no se da por realizado un merge.

Para registrar esa revisión, completar una fila por observación:

| Fecha | Persona revisora | Criterio y evidencia | Resultado u observación | Acción y responsable acordado | Seguimiento |
|---|---|---|---|---|---|
| Pendiente | Pendiente del grupo | UML / reglas / rendimiento / recuperación / documentación | Pendiente | Por acordar | Propuesto: antes del 5/11/2026 |

La aprobación humana y la comprensión del trabajo no son métricas que pueda certificar una herramienta. La evidencia preparada facilita esa revisión y deja visibles sus pendientes.
