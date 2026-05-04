package org.example.api.character;

import java.util.ArrayList;
import java.util.List;

public class BoneNode {

    public final String name;
    final float[] pivotLocal;

    /** Вращение от ориентации (yaw тела, pitch головы) */
    public Quat baseRot = Quat.IDENTITY;
    /** Вращение от анимации */
    public Quat animRot = Quat.IDENTITY;

    float[] worldPivot;
    Quat    worldRot;

    BoneNode parent;
    final List<BoneNode> children = new ArrayList<>();

    public BoneNode(String name, float px, float py, float pz) {
        this.name       = name;
        this.pivotLocal = new float[]{px, py, pz};
        this.worldPivot = new float[]{px, py, pz};
        this.worldRot   = Quat.IDENTITY;
    }

    public void addChild(BoneNode child) {
        child.parent = this;
        children.add(child);
    }

    public void resetAnimRot() { animRot = Quat.IDENTITY; }
    public void resetBaseRot() { baseRot = Quat.IDENTITY; }

    public void updateWorld(float[] rootWorldPos) {
        // Итоговое вращение = ориентация * анимация
        Quat effective = baseRot.mul(animRot);

        if (parent == null) {
            worldPivot = new float[]{
                    rootWorldPos[0] + pivotLocal[0],
                    rootWorldPos[1] + pivotLocal[1],
                    rootWorldPos[2] + pivotLocal[2]
            };
            worldRot = effective;
        } else {
            float[] rp = parent.worldRot.rotate(pivotLocal[0], pivotLocal[1], pivotLocal[2]);
            worldPivot = new float[]{
                    parent.worldPivot[0] + rp[0],
                    parent.worldPivot[1] + rp[1],
                    parent.worldPivot[2] + rp[2]
            };
            worldRot = parent.worldRot.mul(effective);
        }

        for (BoneNode child : children) child.updateWorld(rootWorldPos);
    }
}