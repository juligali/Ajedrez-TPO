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
