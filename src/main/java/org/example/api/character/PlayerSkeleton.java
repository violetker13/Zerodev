package org.example.api.character;

import org.example.api.character.config.BoneConfig;
import org.example.api.character.config.CharacterConfig;

import java.util.HashMap;
import java.util.Map;

public class PlayerSkeleton {

    private final Map<String, BoneNode> boneMap = new HashMap<>();
    private final BoneNode root;

    public PlayerSkeleton(CharacterConfig config) {
        // Строим кости в том порядке, в котором они объявлены
        // (родитель всегда идёт раньше ребёнка — это контракт конфига)
        for (BoneConfig bc : config.bones()) {
            BoneNode node = new BoneNode(bc.name(), bc.px(), bc.py(), bc.pz());
            boneMap.put(bc.name(), node);

            if (bc.parent() != null) {
                BoneNode parentNode = boneMap.get(bc.parent());
                if (parentNode == null)
                    throw new IllegalArgumentException(
                            "Кость «" + bc.parent() + "» не найдена. " +
                                    "Убедитесь, что родитель объявлен раньше ребёнка.");
                parentNode.addChild(node);
            }
        }

        root = boneMap.values().stream()
                .filter(b -> b.parent == null)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Скелет без корня"));
    }

    public BoneNode getBone(String name) {
        BoneNode b = boneMap.get(name);
        if (b == null) throw new IllegalArgumentException("Неизвестная кость: " + name);
        return b;
    }

    public void resetAll() {
        boneMap.values().forEach(b -> b.animRot = Quat.IDENTITY);
    }

    public void update(float[] rootWorldPos) {
        root.updateWorld(rootWorldPos);
    }
}