package ar.edu.ajedrez.app;

import java.util.Objects;

import ar.edu.ajedrez.core.game.IGameService;

/**
 * Adaptador de consola: muestra el tablero, lee las jugadas y traduce cada
 * RejectionReason a un mensaje. Las reglas del juego permanecen en el núcleo.
 */

public class ConsoleUI {
    private final IGameService game;

    public ConsoleUI(IGameService game) {
        this.game = Objects.requireNonNull(game, "game");
    }

    public void run() {
        throw new UnsupportedOperationException("Pendiente: se implementa con TDD");
    }
}
