package org.example.api.character.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minestom.server.coordinate.Vec;

import java.nio.file.Files;
import java.nio.file.Path;

public class CharacterConfigLoader {

    public static CharacterConfig load(String characterName, Path modelPath, int startCmd) throws Exception {
        JsonObject root = JsonParser.parseString(Files.readString(modelPath)).getAsJsonObject();

        CharacterConfig.Builder builder = CharacterConfig.builder(characterName)
                .orientationBone("root")
                .headBone("head")
                .spawnOffset(new Vec(0, -1.5, 0))
                .bone("root", null, 0, 0, 0); // root всегда добавляем вручную

        int cmd = startCmd;
        for (JsonElement e : root.getAsJsonArray("elements")) {
            JsonObject part = e.getAsJsonObject();
            String boneName = part.get("name").getAsString()
                    .toLowerCase().replace(" ", "_");

            // пивот из rotation origin
            float px = 0, py = 0, pz = 0;
            if (part.has("rotation")) {
                JsonArray origin = part.getAsJsonObject("rotation")
                        .getAsJsonArray("origin");
                px = (origin.get(0).getAsFloat() - 8f) / 16f;
                py = (origin.get(1).getAsFloat()) / 16f;
                pz = (origin.get(2).getAsFloat() - 8f) / 16f;
            }

            builder.bone(boneName, guessParent(boneName), px, py, pz);
            builder.cmd(boneName, cmd++);
        }

        return builder.build();
    }

    private static String guessParent(String name) {
        return switch (name) {
            case "root"                  -> null;
            case "head", "body"          -> "root";
            case "right_arm", "left_arm" -> "body";
            case "right_leg", "left_leg" -> "root";
            default                      -> "root";
        };
    }
}