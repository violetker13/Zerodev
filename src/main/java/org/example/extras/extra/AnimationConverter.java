package org.example.extras.extra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.file.*;
import java.util.Iterator;
import java.util.Map;

public class AnimationConverter {

    private static final ObjectMapper mapper = new ObjectMapper();

    // 👉 папки
    private static final Path INPUT_DIR = Paths.get("blockbench_animations");
    private static final Path OUTPUT_DIR = Paths.get("animations");

    /**
     * Главная точка запуска (вызывай из Main)
     */
    public static void run() {
        try {
            convertAll();
        } catch (Exception e) {
            System.err.println("❌ Ошибка при конвертации анимаций");
            e.printStackTrace();
        }
    }

    /**
     * Обрабатывает все файлы
     */
    private static void convertAll() throws Exception {
        if (!Files.exists(INPUT_DIR)) {
            System.err.println("❌ Папка не найдена: " + INPUT_DIR.toAbsolutePath());
            return;
        }

        try (var stream = Files.walk(INPUT_DIR)) {
            stream.filter(path -> path.toString().endsWith(".json"))
                    .forEach(AnimationConverter::processFile);
        }

        System.out.println("✅ Все анимации сконвертированы!");
    }

    /**
     * Обработка одного файла
     */
    private static void processFile(Path inputPath) {
        try {
            JsonNode input = mapper.readTree(inputPath.toFile());

            // 🔒 защита от кривого JSON
            JsonNode animations = input.get("animations");
            if (animations == null || animations.get("animation") == null) {
                System.err.println("❌ Неверный формат: " + inputPath);
                return;
            }

            JsonNode animation = animations.get("animation");

            ObjectNode output = mapper.createObjectNode();

            String fileName = inputPath.getFileName().toString().replace(".json", "");

            output.put("name", fileName);
            output.put("length", animation.path("animation_length").asDouble(1.0));
            output.put("loop", true);

            ObjectNode bonesOut = mapper.createObjectNode();
            JsonNode bones = animation.get("bones");

            if (bones != null) {
                Iterator<Map.Entry<String, JsonNode>> boneIter = bones.fields();

                while (boneIter.hasNext()) {
                    Map.Entry<String, JsonNode> boneEntry = boneIter.next();

                    String boneName = normalizeName(boneEntry.getKey());
                    JsonNode boneData = boneEntry.getValue();

                    ObjectNode boneOut = mapper.createObjectNode();
                    ArrayNode keyframes = mapper.createArrayNode();

                    if (boneData.has("rotation")) {
                        JsonNode rotation = boneData.get("rotation");

                        // один vector
                        if (rotation.has("vector")) {
                            ArrayNode vec = (ArrayNode) rotation.get("vector");
                            keyframes.add(createKeyframe(0.0, vec));
                        } else {
                            Iterator<Map.Entry<String, JsonNode>> rotIter = rotation.fields();

                            while (rotIter.hasNext()) {
                                Map.Entry<String, JsonNode> rotEntry = rotIter.next();

                                double time;
                                try {
                                    time = Double.parseDouble(rotEntry.getKey());
                                } catch (NumberFormatException e) {
                                    continue; // пропускаем мусор
                                }

                                JsonNode vecNode = rotEntry.getValue().get("vector");
                                if (vecNode == null || !vecNode.isArray()) continue;

                                ArrayNode vec = (ArrayNode) vecNode;
                                keyframes.add(createKeyframe(time, vec));
                            }
                        }
                    }

                    boneOut.set("keyframes", keyframes);
                    bonesOut.set(boneName, boneOut);
                }
            }

            output.set("bones", bonesOut);

            // 📁 сохраняем структуру папок
            Path relative = INPUT_DIR.relativize(inputPath);
            Path outputPath = OUTPUT_DIR.resolve(relative);

            Files.createDirectories(outputPath.getParent());

            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(outputPath.toFile(), output);

            System.out.println("✔ " + inputPath + " -> " + outputPath);

        } catch (Exception e) {
            System.err.println("❌ Ошибка в файле: " + inputPath);
            e.printStackTrace();
        }
    }

    /**
     * Создание keyframe
     */
    private static ObjectNode createKeyframe(double time, ArrayNode vec) {
        ObjectNode kf = mapper.createObjectNode();
        kf.put("time", time);
        kf.put("rx", vec.get(0).asDouble());
        kf.put("ry", vec.get(1).asDouble());
        kf.put("rz", vec.get(2).asDouble());
        return kf;
    }

    /**
     * left arm -> left_arm
     */
    private static String normalizeName(String name) {
        return name.toLowerCase().replace(" ", "_");
    }
}