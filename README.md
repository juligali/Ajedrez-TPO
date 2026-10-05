# Ajedrez TPO

Estructura inicial de clases Java, sin atributos, metodos ni logica.
Las estrategias implementan IMovementStrategy y Game implementa IGameService.
Las relaciones de composicion se agregaran al implementar los atributos.

## Carpetas

- core/board: Board, Position.
- core/pieces: Piece, PieceColor (enum vacio).
- core/movement: interfaz de movimiento y seis estrategias.
- core/game: interfaz de partida, Game, Move y MoveResult.
- core/validation: MoveValidator y CheckDetector.
- app: StandardGame, futuro punto de armado de la partida.

Cada archivo declara su package y una clase, interfaz o enum vacio.
Esta estructura todavia no implementa un juego ejecutable.
