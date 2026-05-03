package org.example.api.blockbench.player;

import java.util.ArrayList;
import java.util.List;

/**
 * Одна кость в иерархии скелета.
 *
 * pivotLocal — позиция пивота кости относительно пивота родителя
 *              в родительском локальном пространстве (bind-поза).
 * localRot   — текущее локальное вращение (задаётся анимацией).
 * worldPivot — вычисленная мировая позиция пивота (обновляется каждый тик).
 * worldRot   — вычисленное мировое вращение (обновляется каждый тик).
 */
public class BoneNode {

    public final String name;

    final float[] pivotLocal; // bind-поза: смещение пивота от родителя
    public  Quat  localRot;   // текущее вращение, задаётся AnimationPlayer

    // Вычисляемые мировые значения
    float[] worldPivot;
    Quat    worldRot;

    BoneNode parent;
    final List<BoneNode> children = new ArrayList<>();

    public BoneNode(String name, float px, float py, float pz) {
        this.name       = name;
        this.pivotLocal = new float[]{px, py, pz};
        this.localRot   = Quat.IDENTITY;
        this.worldPivot = new float[]{px, py, pz};
        this.worldRot   = Quat.IDENTITY;
    }

    public void addChild(BoneNode child) {
        child.parent = this;
        children.add(child);
    }

    public void resetRotation() {
        localRot = Quat.IDENTITY;
    }

    /**
     * Рекурсивно пересчитывает мировые трансформы (top-down).
     * @param rootWorldPos мировая позиция корня скелета (ноги игрока)
     */
    public void updateWorld(float[] rootWorldPos) {
        if (parent == null) {
            worldPivot = new float[]{
                    rootWorldPos[0] + pivotLocal[0],
                    rootWorldPos[1] + pivotLocal[1],
                    rootWorldPos[2] + pivotLocal[2]
            };
            worldRot = localRot;
        } else {
            float[] rp = parent.worldRot.rotate(pivotLocal[0], pivotLocal[1], pivotLocal[2]);
            worldPivot = new float[]{
                    parent.worldPivot[0] + rp[0],
                    parent.worldPivot[1] + rp[1],
                    parent.worldPivot[2] + rp[2]
            };
            worldRot = parent.worldRot.mul(localRot);
        }
        for (BoneNode child : children) child.updateWorld(rootWorldPos);
    }
}