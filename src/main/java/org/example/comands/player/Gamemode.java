package org.example.comands.player;

public enum Gamemode {
    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(3);  // ← ;

    private final int value;

    Gamemode(int value) {
        this.value = value;
    }
}