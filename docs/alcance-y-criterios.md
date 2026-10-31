# Alcance y criterios de entrega de BLOCKMATE

Fecha de preparación: 31 de octubre de 2026. Rama: `RAMA-VICTORIA`.

## Acuerdos confirmados

- Presentación: **6 de noviembre de 2026**, según lo informado por Victoria.
- Criterio comunicado por el docente: **los diagramas deben coincidir con las clases del programa**.
- Base técnica: `d90061e81c85b010f1a691bd7797cd20f55fc16d`, tomado de `main` en GitHub al crear esta rama.
- Trabajo de esta rama: comenzar las etapas 1, 2 y 3 del Plan de trabajo grupal para BLOCKMATE, revisar patrones y corregir la correspondencia del UML.

La copia local previamente mencionada estaba en otro commit. Esta documentación describe la base de GitHub indicada arriba. El plan orienta el trabajo; no sustituye una rúbrica completa del docente. Los acuerdos adicionales, responsables y fechas internas que siguen son propuestas para el grupo.

## Alcance propuesto

Conservar el ajedrez estándar de 8 × 8, sus reglas, la IA local, los modos de uno y dos jugadores, el guardado, el cronómetro de tiempo activo y los adaptadores web y consola. Revisar cómo están implementados Strategy, Command y Simple Factory, y documentar si otro patrón resuelve una necesidad concreta.

En este avance se modifica documentación y UML. No se incorporan nuevas reglas, variantes, cuentas, servicios de IA externos ni optimizaciones sin mediciones. No se presenta una revisión del equipo como certificación o auditoría independiente.

## Entregables y criterios verificables

| ID | Entregable | Criterio de aceptación | Estado de este avance |
|---|---|---|---|
| E1-01 | Este documento | Registra fecha de presentación, commit base, alcance y pendientes | Preparado; alcance propuesto pendiente de revisión grupal |
| E1-02 | Calendario y reparto | El grupo confirma fechas internas y responsables | Propuesto; responsables pendientes |
| E2-01 | [Revisión de patrones](revision-de-patrones.md) | Cada patrón existente tiene evidencia de código y pruebas localizables | Revisión técnica preparada |
| E2-02 | Evaluación de patrones nuevos | Explica problema, alternativa sencilla, beneficio, costo y decisión | Preparada; no se propone agregar uno ahora |
| E2-03 | [UML editable](UML-Ajedrez-TPO.puml) y [PDF](UML-Ajedrez-TPO.pdf) | Tipos, firmas y relaciones representadas coinciden con el código de la base; ambos formatos coinciden | Actualizados y verificados |
| E2-04 | Revisión por otro integrante | Otra persona contrasta decisiones y UML con la misma versión y registra observaciones | Pendiente |

Para E2-03 se incluyen los 37 tipos públicos de producción y su API pública declarada, incluidos constructores implícitos. Las vistas omiten miembros privados, métodos heredados y métodos generados de enums; los tipos externos del JDK y referencias entre vistas se usan como contexto. Las omisiones no deben confundirse con clases inexistentes. Si cambia el código antes de presentar, repetir el contraste y regenerar el PDF.

## Calendario propuesto para el grupo

Solo la presentación del 6 de noviembre está confirmada. Las fechas intermedias son una propuesta, no compromisos ya aceptados.

| Fecha de 2026 | Trabajo | Resultado esperado |
|---|---|---|
| 31 de octubre al 1 de noviembre | Etapas 1 y 2 | Acordar alcance y revisar patrones y UML |
| 2 de noviembre | Etapa 3 | Políticas breves y registro de riesgos |
| 3 de noviembre | Etapa 4 | Pruebas ejecutadas, matriz de requisitos y métricas con evidencia |
| 4 de noviembre | Etapa 5 | Mediciones reproducibles de CPU y memoria |
| 5 de noviembre | Etapa 6 | Revisión cruzada, documentación coherente y ensayo |
| 6 de noviembre | Presentación | Defensa de la versión acordada |

La documentación de la etapa 3 está preparada en [gobernanza y riesgos](gobernanza-y-riesgos.md), pendiente de adopción grupal. Las etapas 4 a 6 quedan planificadas, no realizadas por este avance. La medición de pruebas y rendimiento no debe inferirse de esta revisión documental.

## Lista de tareas

- [x] Crear `RAMA-VICTORIA` en GitHub desde la base identificada.
- [x] Registrar la presentación y el criterio comunicado por el docente.
- [x] Revisar los patrones existentes y evaluar alternativas.
- [x] Contrastar los 37 tipos públicos con el UML y corregir diferencias.
- [x] Exportar y revisar visualmente las seis vistas del PDF.
- [x] Preparar las políticas y el registro de riesgos de la etapa 3.
- [ ] Confirmar el alcance propuesto con el grupo y contrastarlo con la consigna completa.
- [ ] Acordar responsables y fechas internas.
- [ ] Obtener revisión de otro integrante antes de integrar en `main`.
- [ ] Ejecutar la etapa de calidad y conservar evidencia con fecha, entorno y commit.
- [ ] Antes de presentar, revisar también los diagramas que aparezcan en Word o en las diapositivas.

## Cierre de las etapas 1 y 2

La preparación técnica queda disponible para revisión. No se declara que el grupo haya aprobado decisiones que todavía no revisó. Para cerrar estas etapas, registrar aquí quién revisó, en qué fecha y qué observaciones quedaron pendientes.

| Revisión | Responsable | Fecha | Resultado |
|---|---|---|---|
| Alcance y fechas internas | A acordar | A acordar | Pendiente |
| Patrones y correspondencia de UML con el código | Otro integrante a acordar | A acordar | Pendiente |

Cada cambio futuro debe indicar su evidencia y actualizar el commit de referencia. Integrar mediante una revisión de la rama; no reemplazar resultados históricos por cifras de otra versión.
