# Dependencias, recursos y atribución

Inventario de la etapa 6, 31/10/2026, sobre `d37cbe4c39bd434915e2f822d06195bae3e95a69`. Se distingue lo que necesita el juego de las herramientas utilizadas para probar o preparar documentación. No se incorporan binarios de estas herramientas al repositorio.

## Aplicación y construcción

| Componente | Uso y versión registrada | Fuente y licencia declarada |
|---|---|---|
| Java / JDK | El proyecto requiere Java 21; campaña con Oracle JDK 21.0.6. Incluye `jdk.httpserver`, JFR y herramientas de diagnóstico | El archivo LICENSE de esa instalación indica Oracle No-Fee Terms and Conditions. Registrar el proveedor y sus términos si se utiliza otra distribución |
| Maven | Construcción; campaña con 3.9.16 | Herramienta externa al juego; conservar sus avisos y documentación al distribuirla |
| Maven Compiler Plugin | 3.13.0, compilación | Declarado en `pom.xml`. Su POM local remite a un padre y no declara licencia directamente; el inventario no inventa ese campo |
| Maven Surefire Plugin | 3.5.2, ejecución JUnit | Declarado en `pom.xml`; mismo límite de metadatos directos que Compiler |
| JUnit Jupiter | 5.11.4, dependencia con alcance `test`; agregador, API, params y engine | POMs: [Eclipse Public License 2.0](https://www.eclipse.org/legal/epl-v20.html) |
| JUnit Platform | commons y engine 1.11.4, componentes de pruebas | POMs: [Eclipse Public License 2.0](https://www.eclipse.org/legal/epl-v20.html) |
| OpenTest4J | 1.3.0, soporte de aserciones | POM: [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt) |
| API Guardian | 1.1.2, anotaciones de API usadas en pruebas | POM: [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt) |

`pom.xml` no declara bibliotecas externas de **producción**. El núcleo usa la biblioteca estándar de Java. Este inventario de componentes declarados y principales componentes de prueba no es un SBOM exhaustivo de todas las dependencias transitivas internas de Maven. Los POMs consultados, sus coordenadas, padres, campos de licencia y huellas están en [dependencias-metadata.json](evidencias/2026-10-31-etapas5-6-d37cbe4/dependencias-metadata.json).

## Verificación y documentación

| Herramienta o recurso | Función en la campaña | Observación |
|---|---|---|
| Node 24.22.1 | Scripts opcionales de prueba web y respaldo | No se necesita para ejecutar los tests Java |
| Playwright 1.62.1 | Automatización de Chrome y DevTools | Su `package.json` declara Apache-2.0; no se agregó como dependencia del juego |
| Chrome 155.0.8059.86 | Navegador de la campaña | Instalación externa; registrar versión al repetir |
| JFR y `jdk.jfr.consumer` | Perfil de Java y resumen de eventos | Incluidos en el JDK utilizado; no se añadió un agente al programa |
| PlantUML 1.2026.9 / Smetana | Render del UML en la etapa 2 | Herramienta de documentación; el `.puml` es la fuente editable y el JAR no se publica en el proyecto |
| Python, python-docx, ReportLab, svglib y Poppler | Edición/verificación de documentos y generación del PDF UML | Herramientas externas de autoría; no forman parte del ejecutable Java |
| Artifact Tool y Microsoft Word | Edición/renderizado de diapositivas y comprobación visual del informe | Herramientas de preparación; no son requisitos del juego |
| Material de clase | Fundamento de arquitectura, patrones, calidad y rendimiento | Atribuido en [material-de-clase.md](material-de-clase.md); no se presenta como producción propia del equipo |
| Codex | Asistencia generativa de esta revisión | Uso y límites declarados en [integridad-y-cierre.md](integridad-y-cierre.md) |

Los generadores y renderizadores conservan sus propios términos. No se interpreta que producir un documento otorgue permiso para redistribuir las herramientas ni los materiales docentes.

## Recursos gráficos y autoría

`web/pieces.js` construye dibujos voxel con Canvas 2D; el código inspeccionado no descarga modelos o sprites oficiales. La interfaz usa los archivos locales de `web/`. Se conserva la atribución del README: proyecto académico inspirado en Minecraft, sin afiliación con Mojang o Microsoft. Las referencias a nombres y personajes no equivalen a una autorización general de sus titulares.

No se encontró un archivo LICENSE del proyecto en el árbol revisado. Se deja **pendiente de decisión de sus autores** la licencia del código propio; esta revisión no asigna una ni declara libre redistribución de todo el contenido. Antes de distribuir componentes de terceros, el grupo debe revisar los términos de las versiones efectivamente incluidas y conservar los avisos que correspondan. Este registro identifica fuentes y pendientes; no es una certificación legal.
