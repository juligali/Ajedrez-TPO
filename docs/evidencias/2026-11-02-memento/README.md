# Evidencia de Memento

Ejecución del 2/11/2026 sobre la copia de trabajo basada en `38f2635`, con Board.Memento, MoveCommand actualizado y BoardMementoTest. No hay un commit nuevo asignado: `resumen.json` identifica las fuentes y el POM por SHA-256, junto con entorno y resultados.

`mvn -B -ntp clean test`: **65 aprobadas, 0 fallos, 0 errores y 0 omitidas**, seis suites. Los seis XML Surefire conservan métodos, resultados y duraciones; solo se retiraron propiedades del entorno con rutas locales. No se modificaron las aserciones de las 62 pruebas anteriores.

BoardMementoTest comprueba restauración de piezas, marcas y metadatos; independencia y reutilización de capturas; y rechazo de null antes de mutar. ChessTest conserva las comprobaciones de Command, enroque, al paso, promociones, undo/redo y perft 20/400/8902. Las pruebas de sesión y persistencia siguen formando parte de la suite.

`api-publica.txt` registra la salida real de `javap -public`: **38 tipos públicos y 196 miembros declarados**, excluidos los métodos generados values/valueOf. Hay 37 archivos de producción; el tipo adicional es la clase pública anidada Board.Memento, sin constructor ni métodos públicos.

Para reproducir, usar las fuentes cuyos hashes figuran en el resumen, JDK 21 y Maven, y ejecutar el mismo comando. Los nuevos resultados deben conservar su propia fecha y entorno. No se ejecutaron nuevamente el profiler ni la campaña web; las mediciones del 31/10 corresponden a la implementación anterior.
