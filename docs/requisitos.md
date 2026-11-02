# Requisitos de BLOCKMATE

Catálogo vigente del juego local. Los resultados y los límites de verificación se consultan en la [matriz de requisitos y pruebas](matriz-requisitos-pruebas.md); este documento define el comportamiento esperado.

## Requisitos funcionales

| ID | Requisito | Criterio de aceptación |
|---|---|---|
| RF01 | Partida estándar | Tablero 8 × 8, 32 piezas, blancas primero y reina en d1/d8 |
| RF02 | Movimientos y capturas | Seis movimientos, obstáculos, ataques y turnos correctos |
| RF03 | Seguridad del rey | No se captura al rey ni se acepta una jugada que lo deje expuesto |
| RF04 | Finales | Distingue mate, ahogado, triple repetición, 50 movimientos y material insuficiente básico |
| RF05 | Jugadas especiales | Enroque, al paso y promoción a reina, torre, alfil o caballo |
| RF06 | Deshacer y rehacer | Command conserva un Memento opaco de Board; restaura tablero, derechos, contadores, turno e historial; una jugada nueva descarta el futuro |
| RF07 | Personajes | Steve, Alex, gólem, bruja, caballo y creeper en ambos equipos |
| RF08 | Información de partida | Turno, historial, capturas y destinos legales visibles |
| RF09 | Nueva partida | Confirmación antes de reemplazar la partida; reinicia historial, puntos y reloj |
| RF10 | Consola | Mismo núcleo para mover, deshacer, rehacer y salir sin navegador |
| RF11 | Rechazo semántico | ValidationResult expresa el motivo y el rechazo conserva estado e historial |
| RF12 | Traducción del rechazo | MoveMessages traduce el motivo para web y consola |
| RF13 | Puntaje | Capturas 1/3/3/5/9; rey sin valor; deshacer/rehacer corrige los puntos |
| RF14 | Ganador | Corona solo por mate; deshacer el final quita el ganador; mate con un atacante si es legal |
| RF15 | Reloj activo | Suma solo con vistas activas, pausa al ocultar/cerrar, se detiene al terminar y no duplica pestañas |
| RF16 | Persistencia | Recupera tablero, turno, modo, nivel, capturas, historial, redo y tiempo acumulado |
| RF17 | Tutorial | Explica las seis piezas y reglas con ejemplos interactivos sin modificar la partida |
| RF18 | Modos | Humano blanco contra IA negra o dos humanos sin IA; undo/redo agrupado en solo |
| RF19 | Dificultad | Cinco niveles locales; movimientos legales y búsqueda acotada en los niveles superiores |

Los identificadores RF13–RF19 normalizan las funcionalidades antes listadas como agregados y A01–A09; no añaden funciones al programa.

## Requisitos no funcionales y documentales

| ID | Requisito | Criterio |
|---|---|---|
| RNF01 | Estabilidad y testabilidad | Suite Java ejecutable sin navegador; escenarios y resultados identificados |
| RNF02 | Terminación de trayectorias | Board.pathClear rechaza extremos inválidos y recorridos no alineados |
| RNF03 | Separación de dependencias | Núcleo sin importaciones de adaptadores, HTTP ni archivos; colaboradores por constructor |
| RNF04 | Ejecución local | Sin servicios externos de juego; dependencias de construcción disponibles antes de usar offline |
| RNF05 | Accesibilidad básica | Botones, etiquetas, foco, teclado y avisos de estado |
| RNF06 | Adaptabilidad | Controles y tablero utilizables a 390 px, sin desplazamiento horizontal |
| RNF07 | Rendimiento | Consultas e IA medidas en escenarios reproducibles; no hay un SLA ni umbral contractual acordado |
| DOC01 | UML | Seis vistas que corresponden a los tipos y API pública del programa |
| DOC02 | Documentación técnica | Arquitectura, decisiones, pruebas y límites coherentes con la versión del código |

## Restricciones

- Una partida por servidor y un servidor por archivo. Las pestañas comparten estado, con revisión para rechazar acciones desactualizadas; no hay sincronización instantánea entre pestañas.
- La consola ofrece ajedrez básico; los modos, tutorial, reloj y persistencia de sesión corresponden a la web.
- Triple repetición y 50 movimientos producen tablas automáticas. Material insuficiente cubre rey contra rey, rey con una pieza menor contra rey y reyes con alfiles del mismo color de casilla; no resuelve toda posición muerta.
- Historial descriptivo, sin SAN completa ni PGN. Sin cuentas, juego por Internet, rendición o tablas por acuerdo.
- La IA es didáctica, no un motor profesional. Los presupuestos de búsqueda no garantizan latencia total en cualquier equipo.
- El JAR necesita la carpeta web externa. Una primera construcción puede requerir Internet.

## Trabajo futuro fuera del alcance actual

PGN, reclamaciones de torneo, variantes de tablero y piezas, plugins e internacionalización completa requieren requisitos propios. No se agregan anticipadamente para cumplir una cantidad de patrones. Los pendientes de revisión del trabajo actual se mantienen únicamente en [integridad y estado de revisión](integridad-y-cierre.md).
