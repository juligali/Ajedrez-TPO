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
- Cronómetro persistente desde la creación, sin límite y detenido al terminar.
- Guardado de tablero, historial, turno, modos y posibilidad de rehacer al cerrar Java.
- Tutorial interactivo antes de crear la partida.
- Modo de un jugador con IA negra, o dos humanos sin IA.
- Cinco niveles con estrategias de azar, capturas y búsqueda acotada.
- Mate por una sola pieza atacante cuando sea legal.
- Destinos legales marcados y deshacer/rehacer conservados.
- Informe Word de núcleo, puertos de entrada/salida y adaptadores.

Todo lo anterior quedó implementado y cubierto por pruebas de Java y comprobaciones del navegador. La IA es didáctica, no un motor profesional.
