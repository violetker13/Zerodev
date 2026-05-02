package org.example.api.blockbench;

import net.minestom.server.coordinate.Pos;

public class Vertex {
    private Pos position;

    public Vertex(Pos position) {
        this.position = position;
    }

    public Pos getPosition() {
        return position;
    }
    public void setPosition(Pos position) {
       this.position = position;
    }
}