# Evidencia de las etapas 5 y 6

Campaña ejecutada el 31/10/2026 sobre `d37cbe4c39bd434915e2f822d06195bae3e95a69`, rama `RAMA-VICTORIA`. No se modificaron las clases de producción. [Resultados y reproducción](../../rendimiento.md). [Revisión y pendientes humanos](../../integridad-y-cierre.md).

| Archivo | Contenido y alcance |
|---|---|
| `resumen.json` | Equipo, versiones, flags, estadísticos y cortes de memoria |
| `java-samples.csv` | 1.000 consultas legales y 10 búsquedas EXTREME; tiempo de pared y CPU del hilo |
| `java-memory.csv` | Cortes tras GC y referencias débiles de 500 partidas |
| `jfr-summary.txt` | Eventos de CPU y estimación de asignación extraídos del JFR real; no son tiempos exactos ni heap retenido |
| `jfr-recording-metadata.json` | Tamaño y SHA-256 del JFR conservado localmente; se publica resumen y script reproducible |
| `browser-results.json` | 30 consultas, 15 ciclos, heap JS, nodos y listeners; teclado, móvil y errores |
| `profile-desktop.png`, `profile-mobile.png` | Capturas reales del escenario de prueba a 1440 y 390 px |
| `ui-result.txt` | Resultado de `verify-features.cjs` después de corregir su selector móvil |
| `console-session.txt` | Entrada probada: e2 e4, deshacer, rehacer, salir; salida del adaptador de consola |
| `backup-result.json` | Simulacro de respaldo, SHA-256, estados comparados y HTTP 403/409 |
| `fuentes-verificadas.json` | Huellas Git de 51 archivos: fuentes Java, tests, POM y web. Identidad del Java con la campaña de etapa 4 |
| `dependencias-metadata.json` | Coordenadas, campos de licencia, padres y huellas de los POMs consultados |
| `documentos-verificados.json` | Huellas y comprobación del Word de 9 páginas y presentación de 13 diapositivas |

Los instrumentos están en `tools/ProfileChess.java`, `tools/profile-browser.cjs` y `tools/verify-backup.cjs`. Los números no son simulados. No se ejecutaron las mediciones sobre la partida personal. Los resultados de JUnit se conservan en la carpeta de la etapa 4 y no se suman a los flujos del navegador como si fueran pruebas del mismo catálogo.

La primera ejecución de `verify-features.cjs` terminó con timeout en móvil al buscar el botón de escritorio oculto. Se corrigió para usar `#compact-new` visible y se repitió la ejecución completa, que pasó. Se registra el fallo del instrumento sin atribuirlo al motor de ajedrez ni ocultarlo tras el resultado final.

La memoria del navegador aumentó ligeramente. No se tomaron heap snapshots con análisis de retenedores: no se concluye ausencia de fugas. El tiempo Java de consultas pequeñas no tiene resolución suficiente en el contador de CPU del hilo; consultar el tiempo de pared y sus límites en el informe.

La revisión de documentos usó Word y Poppler para las nueve páginas del informe (LibreOffice no estaba disponible), y Artifact Tool para las trece diapositivas. Se verificaron estructura, geometría, fuente Arial y reimportación del PPTX, más inspección visual. Esto no certifica funcionamiento en el equipo de exposición ni constituye revisión por otro integrante.
