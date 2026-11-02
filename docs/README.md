# Documentación de BLOCKMATE

La documentación describe la copia de trabajo basada en main (`38f2635`), con Memento implementado el 2/11/2026. Las campañas del 31/10 mantienen sus commits de referencia; no se atribuyen sus mediciones a la implementación nueva.

| Documento | Contenido |
|---|---|
| [Alcance y criterios](alcance-y-criterios.md) | Qué incluye el TP y cómo se comprueba |
| [Requisitos](requisitos.md) | Requisitos funcionales, restricciones y trabajos futuros |
| [Arquitectura](arquitectura.md) | Dependencias, contratos HTTP, sesión y persistencia |
| [Patrones](revision-de-patrones.md) | Strategy, Command, Simple Factory y alternativas evaluadas |
| [UML editable](UML-Ajedrez-TPO.puml) / [PDF](UML-Ajedrez-TPO.pdf) | Seis vistas de tipos, API pública y relaciones |
| [Informe técnico](Informe%20arquitectura%20Ajedrez%20Minecraft.docx) | Explicación continua de clases, decisiones y resultados |
| [Calidad y métricas](calidad-y-metricas.md) | Ejecución de pruebas, indicadores y reproducción |
| [Matriz de requisitos y pruebas](matriz-requisitos-pruebas.md) | Escenarios que respaldan cada requisito y sus límites |
| [Rendimiento](rendimiento.md) | Metodología, CPU, memoria y resultados Java/web |
| [Gobernanza y riesgos](gobernanza-y-riesgos.md) | Políticas propuestas, respaldo y registro de riesgos |
| [Incidencias](registro-incidencias.md) | Hallazgos y correcciones identificados |
| [Integridad y estado de revisión](integridad-y-cierre.md) | Uso de IA, integración y única lista de pendientes vigentes |
| [Dependencias y recursos](dependencias-y-recursos.md) | Herramientas, fuentes y atribuciones |
| [Material de clase](material-de-clase.md) | Relación de las decisiones con las unidades estudiadas |

## Evidencias

- [Memento y suite actual](evidencias/2026-11-02-memento/README.md): 65 pruebas y fuentes identificadas por hash; API pública de 38 tipos.

### Campañas anteriores

- [Contraste del UML](evidencias/2026-10-31-revision-uml.md): clases compiladas y correcciones de firmas.
- [Campaña Java](evidencias/2026-10-31-etapa4-83903e2/README.md): informes de 62 pruebas, catálogo y manifiesto de fuentes.
- [Campaña de rendimiento y recuperación](evidencias/2026-10-31-etapas5-6-d37cbe4/README.md): muestras, profiler, navegador, consola y respaldo.

Las evidencias son registros de ejecuciones concretas; no describen por sí solas el estado actual de todos los requisitos. Sus referencias a ramas y materiales anteriores se conservan para mantener la trazabilidad. Los materiales de exposición se prepararán aparte.
