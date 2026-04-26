package org.example.comands.worldedit;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;
import java.util.List;

public class Selection {
    private Point pos1;
    private Point pos2;
    private InstanceContainer instance;

    public void setPos1(Point pos, InstanceContainer instance) {
        this.pos1 = pos;
        this.instance = instance;
    }

    public void setPos2(Point pos, InstanceContainer instance) {
        this.pos2 = pos;
        this.instance = instance;
    }

    public boolean isComplete() {
        return pos1 != null && pos2 != null;
    }

    public List<Point> getBlocks() {
        List<Point> blocks = new ArrayList<>();
        if (!isComplete()) return blocks;

        int minX = (int) Math.min(pos1.x(), pos2.x());
        int minY = (int) Math.min(pos1.y(), pos2.y());
        int minZ = (int) Math.min(pos1.z(), pos2.z());
        int maxX = (int) Math.max(pos1.x(), pos2.x());
        int maxY = (int) Math.max(pos1.y(), pos2.y());
        int maxZ = (int) Math.max(pos1.z(), pos2.z());

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                for (int z = minZ; z <= maxZ; z++)
                    blocks.add(new Vec(x, y, z));

        return blocks;
    }

    public Point getPos1() { return pos1; }
    public Point getPos2() { return pos2; }
    public InstanceContainer getInstance() { return instance; }
}