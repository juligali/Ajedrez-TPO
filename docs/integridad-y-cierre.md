# Integridad y estado de revisión de BLOCKMATE

Actualizado el 2/11/2026. Este documento reúne el estado de integración, la asistencia utilizada y los pendientes vigentes. Las campañas históricas conservan su propia fecha y commit.

## Implementación actual de Memento

Sobre la base integrada `38f2635`, la copia de trabajo incorpora Board.Memento y su uso por MoveCommand. El 2/11/2026, `mvn -B -ntp clean test` aprobó 65 pruebas, incluidas las tres nuevas de snapshot, con 0 fallos, errores y omisiones. El código nuevo está identificado por hashes en la [evidencia](evidencias/2026-11-02-memento/README.md); todavía no tiene un commit propio. El UML actual incluye el tipo anidado y los métodos de captura/restauración. Los resultados de rendimiento y UI anteriores no se presentan como campañas repetidas sobre esta versión.

## Integración previa y campañas históricas

Los tres commits de RAMA-VICTORIA se integraron por fast-forward en main el 2/11/2026 y se publicaron en GitHub. Main quedó en `38f2635`. No hubo conflictos ni modificaciones de producción, pruebas o POM respecto de la base `d90061e`.

Después del merge, `mvn -B -ntp test` aprobó 62 pruebas, sin fallos, errores u omisiones. Los registros de la campaña limpia del 31/10 siguen en [evidencias Java](evidencias/2026-10-31-etapa4-83903e2/README.md). Esta repetición no reemplaza sus informes ni acredita una nueva campaña web o de rendimiento.

| Área | Evidencia disponible | Límite |
|---|---|---|
| Diseño y UML | API actual: 38 tipos y 196 miembros; seis vistas con Memento | API pública declarada; omite privados, heredados y miembros generados |
| Reglas y sesión | 65 pruebas y matriz de escenarios | No cubre todas las posiciones o interacciones posibles |
| Rendimiento | CSV, JFR y DevTools del 31/10 | Un equipo y escenarios acotados; sin optimización antes/después |
| UI y consola | Tutorial, cinco niveles, puntos, mate, recarga, undo/redo solo, móvil, Enter y consola básica | Automatización de flujos, no accesibilidad ni inspección visual exhaustivas |
| Respaldo y HTTP | Copia en frío, restauración, redo y respuestas 403/409 | Simulacro automatizado; falta procedimiento manual por un integrante |
| Documentación | Índice, documentos temáticos e informe técnico | La revisión asistida no equivale a aceptación independiente del grupo |

Los detalles pertenecen a [calidad](calidad-y-metricas.md), [matriz](matriz-requisitos-pruebas.md), [rendimiento](rendimiento.md) y [registro UML](evidencias/2026-10-31-revision-uml.md).

## Asistencia de inteligencia artificial

Codex asistió en lectura de código, contraste de UML, documentación, instrumentos de medición y verificaciones. La campaña del 31/10 fue solicitada por Victoria; la integración y ordenamiento documental del 2/11 se realizaron a pedido del usuario. Se contrastaron afirmaciones con fuentes, ejecución y registros identificados. La IA local del juego usa minimax con poda alfa-beta y no consume un servicio generativo.

No se atribuyen firmas, revisiones o acuerdos a personas que no los registraron. La integración fue autorizada, pero no sustituye la revisión por otra persona. Los roles de gobernanza y metas de calidad continúan como propuestas donde no hay aceptación registrada.

## Restauración reproducible

`tools/verify-backup.cjs` utiliza `build/backup-drill/` y el puerto 18087, que debe estar libre. Crea una captura, la deshace y guarda con redo disponible. Detiene su servidor, copia el archivo, reemplaza la partida de prueba, restaura y compara SHA-256 y estado completo salvo la revisión HTTP. Finalmente rehace la captura y comprueba un punto. No usa partidas personales; el reloj permanece pausado al no abrir vistas.

```powershell
# Requiere clases compiladas, Java 21 y Node.
$env:JAVA_EXE='java'
node tools/verify-backup.cjs
```

El [resultado histórico](evidencias/2026-10-31-etapas5-6-d37cbe4/backup-result.json) comprueba una restauración concreta. No certifica todos los fallos de disco ni reemplaza un respaldo independiente.

## Pendientes vigentes

| ID | Acción pendiente | Responsable propuesto | Criterio de cierre |
|---|---|---|---|
| P01 | Revisión por un integrante distinto del autor | Revisor por acordar | Nombre, fecha, evidencia y observaciones registrados |
| P02 | Acordar catálogo de reglas, metas, políticas y roles | Grupo | Acuerdos y personas registrados; no basta publicar propuestas |
| P03 | Ejecutar inicio y recuperación manual en el equipo de uso | Entorno y persistencia, por asignar | Arranque y restauración sobre una copia de prueba registrados |
| P04 | Revisar licencia del código propio y atribuciones antes de redistribuir | Autores | Decisión explícita y avisos correspondientes |
| P05 | Ampliar diagnóstico si persiste el crecimiento de heap JS | Rendimiento, por asignar | Campaña más larga y análisis de retenedores si continúa la tendencia |
| P06 | Reevaluar y aceptar riesgo residual con las evidencias existentes | Grupo | Decisión por riesgo, justificación y responsable registrados |

No hay fechas internas ni asignaciones personales aceptadas en los registros revisados. La organización de una exposición queda fuera de esta documentación técnica.

## Registro de revisión independiente

| Fecha | Persona revisora | Criterio y evidencia | Observación | Acción y responsable acordado |
|---|---|---|---|---|
| Por registrar | Por acordar | Código, UML, pruebas y documentos | Pendiente | Por acordar |
