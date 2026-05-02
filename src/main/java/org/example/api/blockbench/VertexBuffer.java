package org.example.api.blockbench;

import net.minestom.server.coordinate.Pos;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VertexBuffer {
    private final List<Vertex> vertices = new ArrayList<>();

    public void add(Vertex vertex) {
        vertices.add(vertex);
    }

    public void add(double x, double y, double z) {
        vertices.add(new Vertex(new Pos(x, y, z)));
    }

    public Vertex get(int index) {
        return vertices.get(index);
    }

    public int size() {
        return vertices.size();
    }

    public void clear() {
        vertices.clear();
    }

    public List<Vertex> getAll() {
        return Collections.unmodifiableList(vertices);
    }


    public static VertexBuffer ObjLoad(String objContent) {
        VertexBuffer buffer = new VertexBuffer();
        for (String line : objContent.split("\n")) {
            if (!line.startsWith("v ")) continue;
            String[] parts = line.trim().split("\\s+");
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            buffer.add(x, y, z);
        }
        return buffer;
    }
}