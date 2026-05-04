package org.example.api.character;

import com.google.gson.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Загружает анимации из JSON-файлов.
 *
 * Формат (пример animations/walk.json):
 * {
 *   "name": "walk",
 *   "length": 1.0,
 *   "loop": true,
 *   "bones": {
 *     "left_leg": {
 *       "keyframes": [
 *         { "time": 0.0, "rx":  30, "ry": 0, "rz": 0 },
 *         { "time": 0.5, "rx": -30, "ry": 0, "rz": 0 },
 *         { "time": 1.0, "rx":  30, "ry": 0, "rz": 0 }
 *       ]
 *     },
 *     "right_leg": {
 *       "keyframes": [
 *         { "time": 0.0, "rx": -30, "ry": 0, "rz": 0 },
 *         { "time": 0.5, "rx":  30, "ry": 0, "rz": 0 },
 *         { "time": 1.0, "rx": -30, "ry": 0, "rz": 0 }
 *       ]
 *     }
 *   }
 * }
 *
 * Доступные имена костей:
 *   root, torso, head, right_arm, left_arm, right_leg, left_leg
 */
public class AnimationLoader {

    public static Animation load(String filePath) throws IOException {
        return parse(Files.readString(Path.of(filePath)));
    }

    public static Animation load(Path path) throws IOException {
        return parse(Files.readString(path));
    }

    public static Animation parse(String json) {
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

        String  name   = obj.get("name").getAsString();
        float   length = obj.get("length").getAsFloat();
        boolean loop   = obj.has("loop") && obj.get("loop").getAsBoolean();

        Map<String, AnimationTrack> tracks = new HashMap<>();

        JsonObject bonesObj = obj.getAsJsonObject("bones");
        if (bonesObj != null) {
            for (Map.Entry<String, JsonElement> e : bonesObj.entrySet()) {
                List<Keyframe> kfList = new ArrayList<>();
                JsonArray kfArray = e.getValue().getAsJsonObject().getAsJsonArray("keyframes");
                for (JsonElement kfEl : kfArray) {
                    JsonObject kf = kfEl.getAsJsonObject();
                    kfList.add(new Keyframe(
                            kf.get("time").getAsFloat(),
                            kf.has("rx") ? kf.get("rx").getAsFloat() : 0f,
                            kf.has("ry") ? kf.get("ry").getAsFloat() : 0f,
                            kf.has("rz") ? kf.get("rz").getAsFloat() : 0f
                    ));
                }
                kfList.sort(Comparator.comparingDouble(Keyframe::time));
                tracks.put(e.getKey(), new AnimationTrack(kfList));
            }
        }

        return new Animation(name, length, loop, tracks);
    }
}