# Registro de incidencias y brechas de verificación

Campaña del 31/10/2026, commit `83903e22eec288bd0823b7172d951b78610d9fdc`, rama `RAMA-VICTORIA`.

## Defectos confirmados

No se registraron defectos funcionales nuevos en la ejecución de `mvn clean test`: 62 pruebas aprobadas, sin fallos, errores u omisiones. La consulta de issues abiertas de [Ajedrez-TPO](https://github.com/juligali/Ajedrez-TPO/issues) del 31/10/2026 devolvió 0 entradas.

**KRI: 0 defectos críticos registrados en el alcance de esta campaña.** No significa que el producto completo esté libre de defectos ni reemplaza la revisión del grupo. El registro empieza con esta campaña; no inventa un historial de incidencias anterior.

Criterio crítico tomado de la etapa 3: pérdida no recuperable de datos, regla esencial que impide jugar correctamente o imposibilidad de iniciar la demostración acordada.

Para una nueva incidencia, registrar ID, fecha, commit, pasos de reproducción, esperado/observado, gravedad, evidencia, estado, responsable y verificación del cierre. No cerrar solo porque otra prueba distinta pasó.

## Brechas pendientes, no defectos confirmados

| ID | Pendiente | Riesgo relacionado | Responsable propuesto | Seguimiento propuesto | Criterio para resolver |
|---|---|---|---|---|---|
| Q01 | UI, teclado, 390 px, tutorial, HTTP y consola sin ejecución actual | R03 | Referente de interfaz | 3/11/2026 | Flujos ejecutados en entorno aislado, con resultados y capturas |
| Q02 | Simulacro de respaldo y restauración manual no realizado | R01 | Referente de persistencia | 3/11/2026 | Copia validada y restauración comprobada sin usar la partida personal |
| Q03 | Mediciones CPU/memoria y respuesta de IA pendientes | R05 | Referente de rendimiento | 4/11/2026 | Escenario reproducible, entorno, repeticiones y resultados |
| Q04 | Ensayo en equipo de presentación y revisión de Word/diapositivas | R04 / R06 | Coordinación y documentación | 5/11/2026 | Versión acordada iniciada y documentos contrastados |
| Q05 | Aceptación de catálogo, metas, roles y revisión por otra persona | R07 / R08 | Coordinación y revisor | Antes de integrar o presentar | Decisión y observaciones registradas |

Las personas y fechas internas requieren acuerdo del grupo. Estas brechas no incrementan artificialmente el conteo de defectos críticos, pero impiden interpretar el KRI como una habilitación automática de la entrega completa.

## Seguimiento de las etapas 5 y 6 — 31/10/2026

Esta tabla actualiza las brechas anteriores; no altera los resultados históricos de etapa 4.

| ID | Estado actual | Evidencia y límite | Seguimiento propuesto |
|---|---|---|---|
| Q01 | Comprobaciones técnicas realizadas | UI, 390 px, tutorial y cinco niveles; Enter e2/e4; consola; HTTP 403/409. No es auditoría completa de accesibilidad | Referente UI: ensayo manual en equipo final antes del 5/11 |
| Q02 | Simulacro automatizado realizado | Copia con servidor detenido, SHA-256, restauración del estado y redo. No usa datos personales | Persistencia: repetir procedimiento por un integrante en el equipo final |
| Q03 | Campaña reproducible realizada | JFR, CPU del hilo, memoria y DevTools; ver rendimiento.md | Rendimiento: ampliar análisis de retenedores si persiste crecimiento JS |
| Q04 | Documentos actualizados; ensayo pendiente | Word de 9 páginas y presentación de 13 diapositivas revisados visualmente | Coordinación: probar el equipo de exposición antes del 5/11 |
| Q05 | Pendiente del grupo | No hay revisión humana ni aceptación de roles registrada | Grupo: completar registro antes de integrar o presentar |

### Incidencias observadas y corregidas

| ID | Hallazgo y reproducción | Gravedad | Cambio y verificación | Estado |
|---|---|---|---|---|
| I01 | `verify-features.cjs` fallaba al crear partida a 390 px por pulsar `#new-button` oculto | Media, instrumento de prueba | Selector según visibilidad: `#compact-new` en móvil. Ejecución completa posterior aprobada | Cerrada técnicamente |
| I02 | Word y PowerPoint describían 53 pruebas y reloj durante cierres | Media, documentación contradictoria | Actualizados a 62 y tiempo activo; revisión de las 9 páginas y 13 diapositivas | Cerrada técnicamente; revisión humana pendiente |

**KRI de esta ampliación: 0 defectos críticos nuevos confirmados en los escenarios ejecutados.** I01/I02 no eran críticos bajo el criterio de la etapa 3. El crecimiento de heap JS de 104.280 bytes se registra como observación de rendimiento, no como fuga confirmada.

Responsable de las correcciones ejecutadas: revisión asistida de Codex solicitada por Victoria. Personas responsables del seguimiento: por acordar. Ver [evidencia](evidencias/2026-10-31-etapas5-6-d37cbe4/README.md) y [registro de revisión](integridad-y-cierre.md).
