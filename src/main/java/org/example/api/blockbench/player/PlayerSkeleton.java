package org.example.api.blockbench.player;

import java.util.HashMap;
import java.util.Map;

/**
 * Иерархия костей стандартного тела игрока Minecraft.
 *
 * Мировые пивоты (ноги = Y 0):
 *   root     : (0, 0, 0)
 *   torso    : (0, 0.75, 0)   — таз
 *   head     : (0, 1.5, 0)    — шея
 *   right_arm: (0.375, 1.5, 0) — правое плечо
 *   left_arm : (-0.375, 1.5, 0)
 *   right_leg: (0.125, 0.75, 0) — правое бедро
 *   left_leg : (-0.125, 0.75, 0)
 *
 * Кости head/arms задаются относительно torso-пивота.
 */
public class PlayerSkeleton {

    public final BoneNode root;
    public final BoneNode torso;
    public final BoneNode head;
    public final BoneNode rightArm;
    public final BoneNode leftArm;
    public final BoneNode rightLeg;
    public final BoneNode leftLeg;

    private final Map<String, BoneNode> boneMap = new HashMap<>();

    public PlayerSkeleton() {
        root = new BoneNode("root", 0, 0, 0);

        // torso: 0.75 выше ног
        torso = new BoneNode("torso", 0, 0.75f, 0);
        root.addChild(torso);

        // head: 0.75 выше torso-пивота (итого 1.5 от земли)
        head = new BoneNode("head", 0, 0.75f, 0);
        torso.addChild(head);

        // arms: на уровне шеи, по бокам от торса
        rightArm = new BoneNode("right_arm",  0.375f, 0.75f, 0);
        leftArm  = new BoneNode("left_arm",  -0.375f, 0.75f, 0);
        torso.addChild(rightArm);
        torso.addChild(leftArm);

        // legs: у таза
        rightLeg = new BoneNode("right_leg",  0.125f, 0, 0);
        leftLeg  = new BoneNode("left_leg",  -0.125f, 0, 0);
        torso.addChild(rightLeg);
        torso.addChild(leftLeg);

        register(root);
    }

    private void register(BoneNode b) {
        boneMap.put(b.name, b);
        b.children.forEach(this::register);
    }

    public BoneNode getBone(String name) { return boneMap.get(name); }

    public void resetAll() { boneMap.values().forEach(BoneNode::resetRotation); }

    /** Пересчитать все мировые трансформы. Вызывать каждый тик. */
    public void update(float[] rootWorldPos) {
        root.updateWorld(rootWorldPos);
    }
}