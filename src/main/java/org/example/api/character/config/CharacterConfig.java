package org.example.api.character.config;

import net.minestom.server.coordinate.Vec;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record CharacterConfig(
        Vec spawnOffset,
        String id,
        List<BoneConfig> bones,
        Map<String, Integer> boneCmds,  // ← было List<PartConfig> parts
        String orientationBone,// ← новое
        String headBone
) {
    public static Builder builder(String id) { return new Builder(id); }

    public static final class Builder {
        private final String id;
        private final List<BoneConfig>      bones    = new ArrayList<>();
        private final Map<String, Integer>  boneCmds = new LinkedHashMap<>();
        private String orientationBone = "root";
        private String headBone        = "head";
        private Vec spawnOffset = Vec.ZERO;
        private Builder(String id) { this.id = id; }

        public Builder bone(String name, String parent, float px, float py, float pz) {
            bones.add(new BoneConfig(name, parent, px, py, pz));
            return this;
        }
        public Builder spawnOffset(Vec offset) { this.spawnOffset = offset; return this; }

        public Builder cmd(String bone, int customModelData) {  // ← было part()
            boneCmds.put(bone, customModelData);
            return this;
        }

        public Builder orientationBone(String name) { this.orientationBone = name; return this; }
        public Builder headBone(String name)        { this.headBone = name;        return this; }

        public CharacterConfig build() {
            return new CharacterConfig(
                    spawnOffset,
                    id,
                    List.copyOf(bones),
                    Map.copyOf(boneCmds),
                    orientationBone,
                    headBone);
        }
    }
}