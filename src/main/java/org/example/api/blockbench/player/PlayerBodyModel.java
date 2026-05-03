package org.example.api.blockbench.player;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class PlayerBodyModel {
    private final List<HeadPartModel> parts = new ArrayList<>();
    private boolean spawned = false;
    public PlayerBodyModel() {
        for (BodyPartEntry entry : BodyLayout.LAYOUT) {
            parts.add(new HeadPartModel(entry, Pos.ZERO));
        }
    }
    public void spawn(Instance instance, Pos origin) {
        if (spawned) return;
        spawned = true;
        for (HeadPartModel part : parts) {

            part.spawn(instance, origin);
        }
    }
    public void remove() {
        if (!spawned) return;
        spawned = false;
        for (HeadPartModel part : parts) {
            part.remove();
        }
    }
    public void teleport(Pos newOrigin) {
        for (HeadPartModel part : parts) {
            part.getEntity().teleport(newOrigin);
        }
    }
    public List<HeadPartModel> getParts() {
        return Collections.unmodifiableList(parts);
    }
    public List<HeadPartModel> getParts(Part part) {
        return parts.stream()
                .filter(prt -> prt.getPart() == part)
                .toList();
    }
    public List<Entity> getEntities() {
        return parts.stream().map(HeadPartModel::getEntity).toList();
    }

    public boolean isSpawned() {
        return spawned;
    }
}