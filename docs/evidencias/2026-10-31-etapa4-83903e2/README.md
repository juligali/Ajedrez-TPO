# Evidencia de la etapa 4

- Commit probado: `83903e22eec288bd0823b7172d951b78610d9fdc`.
- Rama al seleccionar la versión: `RAMA-VICTORIA`.
- Ejecución: 31/10/2026 de 12:18:43 a 12:19:01, Argentina (UTC−03:00).
- Comando: `mvn -B -ntp clean test`.
- Entorno: Windows 11 build 26200 AMD64, Oracle JDK 21.0.6, Maven 3.9.16, JUnit 5.11.4 y Surefire 3.5.2.
- Resultado: BUILD SUCCESS, código 0, 62 aprobadas, 0 fallos, 0 errores, 0 omitidas.

## Archivos

- [maven-clean-test.txt](maven-clean-test.txt): salida de la ejecución; rutas locales sustituidas por marcadores.
- [resumen.json](resumen.json): resultados por suite y por método, indicadores, entorno y hashes de informes.
- [catalogo-indicadores.json](catalogo-indicadores.json): los 13 grupos de reglas y diez métodos de recuperación seleccionados.
- [fuentes-verificadas.json](fuentes-verificadas.json): 43 archivos de entrada (37 producción, 5 tests y POM) con su SHA de blob Git, comprobados antes y después de ejecutar.
- [ActivityClockTest](TEST-ar.edu.ajedrez.ActivityClockTest.xml), [BoardPathTest](TEST-ar.edu.ajedrez.BoardPathTest.xml), [ChessTest](TEST-ar.edu.ajedrez.ChessTest.xml), [FeatureTest](TEST-ar.edu.ajedrez.FeatureTest.xml) y [ValidationTest](TEST-ar.edu.ajedrez.ValidationTest.xml): informes Surefire de esta ejecución.

## Tratamiento de los informes

Se conservaron todos los casos, resultados, duraciones y salidas de las pruebas. De los XML publicados se retiró únicamente el bloque properties, que incluía rutas locales e identificadores del entorno. El entorno relevante se registró explícitamente arriba. El resumen distingue el SHA-256 del XML original y el del publicado; no se presentan como archivos idénticos.

Los originales permanecen en la carpeta temporal de ejecución. No se publicaron partidas ni se ejecutaron scripts de navegador que reinicien una partida personal.

## Reproducción

En una copia separada del repositorio, con JDK 21 y Maven disponibles:

```powershell
git switch --detach 83903e22eec288bd0823b7172d951b78610d9fdc
mvn -version
mvn -B -ntp clean test
```

Usar una copia de trabajo sin cambios pendientes. Los resultados nuevos aparecen en `target/surefire-reports`; conservarlos con su propia fecha y entorno. La primera ejecución puede necesitar red para descargar dependencias.

Para comprobar las cifras, sumar tests/failures/errors/skipped de los cinco XML. Aprobadas = tests − failures − errors − skipped; ejecutadas = tests − skipped. Para cada regla, verificar que todos los métodos del catálogo figuren aprobados. Para recuperación, contar una sola vez cada uno de los diez métodos seleccionados. Si el denominador es cero, reportar “sin medición”.

El commit que publique esta evidencia cambia documentación y evidencia, no el código probado. No atribuir los resultados a una revisión posterior que sí cambie fuentes o configuración sin volver a ejecutar.

## Límites

Esta carpeta no contiene prueba visual de interfaz, ensayo real de corte eléctrico, simulacro manual de backup, cobertura de líneas ni profiler. Los tiempos que aparecen en la salida son observaciones de la ejecución y no una garantía de rendimiento. La revisión humana y aceptación del catálogo operativo siguen pendientes.
