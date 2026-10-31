# Requisitos y backlog

## Requisitos funcionales y aceptación

| ID | Requisito | Criterio de aceptación |
|---|---|---|
| RF01 | Partida estándar | 32 piezas, 8 × 8, blancas primero, reina en d1/d8 |
| RF02 | Movimientos y capturas | Seis estrategias y obstáculos verificados por las pruebas |
| RF03 | Seguridad del rey | No puede capturarse ni quedar expuesto después de una jugada |
| RF04 | Fin de partida | Distingue mate, ahogado y las tablas detalladas en el README |
| RF05 | Jugadas especiales | Enroque, al paso y las cuatro promociones funcionan |
| RF06 | Deshacer y rehacer | El estado completo vuelve a ser igual al anterior |
| RF07 | Personajes de referencia | Steve, Alex, gólem, bruja, caballo y creeper en ambos equipos |
| RF08 | Información visible | Turno, historial, capturas y destinos legales |
| RF09 | Nueva partida | Pide confirmación y borra tablero anterior e historial |
| RF10 | Alternativa de consola | El mismo núcleo puede usarse sin frontend |

## Requisitos no funcionales

- **Simplicidad:** Java del JDK, sin frameworks en el núcleo.
- **Testabilidad:** pruebas de dominio sin red ni navegador.
- **Portabilidad de presentación:** adaptadores dependen de IGameService.
- **Uso offline:** no hay fuentes, imágenes, scripts o servicios externos requeridos.
- **Accesibilidad básica:** botones reales, etiquetas de casillas, foco visible, teclado y avisos de estado.
- **Adaptabilidad:** sin desplazamiento horizontal a 390 px; tablero y controles accesibles en una columna.
- **Integridad:** un movimiento inválido no altera el estado.
- **Rendimiento:** el cálculo de destinos debe percibirse inmediato en un equipo de escritorio. Antes de fijar un umbral contractual habría que acordar hardware y metodología. Las comprobaciones actuales no constituyen un SLA.

## Backlog para continuar

Esto organiza funcionalidades entregadas y posibles trabajos futuros. No afirma que se haya seguido un sprint o celebrado reuniones de Scrum.

| Estado | Funcionalidad |
|---|---|
| Hecho | Completar el núcleo a partir de las clases vacías |
| Hecho | Probar movimientos, validaciones, resultados y jugadas especiales |
| Hecho | Incorporar Command y verificar restauración |
| Hecho | Conectar el núcleo al navegador y a la consola |
| Hecho | Dibujar personajes y construir la interfaz adaptable |
| Hecho | Probar flujos principales y documentar decisiones |
| Hecho | Guardado automático con puerto IGameRepository |
| Por hacer, fuera del alcance | Importar y exportar PGN |
| Hecho | IA local con cinco niveles y modo de dos jugadores |
| Por hacer, fuera del alcance | Reglas de reclamación de tablas de torneo |

Para trabajar en equipo: elegir una tarea, definir su criterio de aceptación, mantener pocas tareas simultáneas y pasar a Hecho sólo después de probarla. Este uso de un backlog se relaciona con Kanban sin requerir infraestructura adicional.


## Requisitos agregados

- Puntaje por capturas 1/3/3/5/9, con rey sin valor y restauración al deshacer.
- Corona del ganador únicamente en mate.
- Cronómetro persistente de tiempo activo, sin límite: pausa al cerrar u ocultar el juego, continúa al volver y se detiene al terminar. Varias pestañas no duplican el tiempo.
- Guardado de tablero, historial, turno, modos y posibilidad de rehacer al cerrar Java.
- Tutorial interactivo antes de crear la partida.
- Modo de un jugador con IA negra, o dos humanos sin IA.
- Cinco niveles con estrategias de azar, capturas y búsqueda acotada.
- Mate por una sola pieza atacante cuando sea legal.
- Destinos legales marcados y deshacer/rehacer conservados.
- Informe Word de núcleo, puertos de entrada/salida y adaptadores.

Todo lo anterior quedó implementado y cubierto por pruebas de Java y comprobaciones del navegador. La IA es didáctica, no un motor profesional.


## Criterios de la revisión final

Se conserva el alcance de ajedrez estándar, de acuerdo con la aclaración del docente. No se exige un tablero arbitrario ni agregar piezas sin tocar configuración central. La defensa debe explicar los puntos de cambio y justificar KISS, Strategy y composición.

| ID | Criterio | Evidencia |
|---|---|---|
| RF11 | Rechazo de jugada mediante motivo semántico, sin excepción normal | ValidationResult, MoveValidator y ValidationTest |
| RF12 | Web y consola muestran una traducción del rechazo | MoveMessages y adaptación de ambos puntos de presentación |
| RNF01 | Estabilidad de reglas y sesión | 62 pruebas aprobadas el 31/10/2026: 53 de reglas/sesión y 9 de actividad |
| RNF02 | Trayectorias inválidas terminan sin recorrer indefinidamente | BoardPathTest |
| DOC01 | UML coincide con los tipos y API pública actuales | Seis vistas PlantUML/PDF, 37 tipos públicos de producción |
| DOC02 | Decisiones justificadas y límites explícitos | arquitectura.md, informe y presentación final |

Fuera de alcance: motor genérico de variantes, carga dinámica de plugins, internacionalización completa, actualización estética de la UI y UML inferido de documentación antigua. La nueva validación tipada se limita a legalidad de movimientos; los resultados de sesión conservan sus mensajes existentes.

## Trazabilidad de la medición del 31 de octubre de 2026

La ejecución de la etapa 4 sobre `83903e22eec288bd0823b7172d951b78610d9fdc` aprobó **62 pruebas Java**. Las cifras 47/53 de secciones anteriores describen revisiones históricas.

La [matriz de requisitos y pruebas](matriz-requisitos-pruebas.md) distingue evidencia del núcleo, requisitos parcialmente comprobados y verificaciones no ejecutadas. El navegador, la consola interactiva, el respaldo manual y la revisión integral de documentos no se consideran aprobados por esta ejecución. Las [métricas y sus límites](calidad-y-metricas.md) se respaldan con informes fechados y asociados al commit probado.

## Actualización de etapas 5 y 6

La evidencia posterior complementa la campaña Java, sin modificar sus resultados históricos:

| Criterio | Resultado adicional | Límite |
|---|---|---|
| Destinos, tutorial y modos | Flujos de `verify-features.cjs` aprobados, cinco niveles y 390 px | Automatización de escenarios, no cobertura visual exhaustiva |
| Uso con teclado y consola | Enter para e2/e4; consola mover/deshacer/rehacer/salir | No se declara accesibilidad integral |
| Recuperación | Copia en frío restaurada; estado y redo comprobados | Simulacro automatizado con archivos de prueba |
| Rendimiento | Java, JFR y DevTools; mediana IA máxima 1,375 s | Un equipo y posición; memoria JS requiere seguimiento si crece |
| DOC01 | 37 tipos y API pública UML conservados | Producción sin cambios desde la verificación |
| DOC02 | Word y presentación actualizados y revisados visualmente | Revisión humana y ensayo final pendientes |

Ver [rendimiento](rendimiento.md), [integridad y cierre](integridad-y-cierre.md) y [evidencia](evidencias/2026-10-31-etapas5-6-d37cbe4/README.md). No se añadieron variantes ni patrones sin una necesidad medida.
