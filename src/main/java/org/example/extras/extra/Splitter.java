package org.example.extras.extra;

import com.google.gson.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class Splitter {

    private static final Path INPUT_DIR = Path.of(
            "/home/mihail/IdeaProjects/minestome/characters/models"
    );

    private static final Path PACK_ROOT = Path.of(
            "/home/mihail/IdeaProjects/minestome/resourcepack/pack/assets"
    );

    private static final Path MODEL_DIR = PACK_ROOT.resolve("minecraft/models/item");
    private static final Path ITEMS_DIR = PACK_ROOT.resolve("minecraft/items");
    private static final Path TEXTURE_DIR = PACK_ROOT.resolve("minecraft/textures/item");

    // ДВА paper.json
    private static final Path PAPER_MODEL_PATH = MODEL_DIR.resolve("paper.json"); // старый
    private static final Path PAPER_ITEM_PATH = ITEMS_DIR.resolve("paper.json"); // новый

    private final Set<String> models = new HashSet<>();
    private static int customModelData = 10;

    public Splitter() throws Exception {
        Files.createDirectories(MODEL_DIR);
        Files.createDirectories(ITEMS_DIR);
        Files.createDirectories(TEXTURE_DIR);

        JsonObject paperModel = createBasePaper();
        JsonArray overrides = new JsonArray();

        JsonArray cases = new JsonArray();

        try (Stream<Path> stream = Files.walk(INPUT_DIR)) {
            stream.filter(Files::isRegularFile)
                    .filter(p -> {
                        String name = p.getFileName().toString();
                        return name.endsWith(".json") && !name.startsWith(".");
                    })
                    .forEach(path -> processModel(path, overrides, cases));
        }

        // --- старый paper.json ---
        paperModel.add("overrides", overrides);
        Files.writeString(PAPER_MODEL_PATH, gson().toJson(paperModel));

        // --- новый paper.json ---
        JsonObject itemsPaper = createItemsPaper(cases);
        Files.writeString(PAPER_ITEM_PATH, gson().toJson(itemsPaper));

        System.out.println("Done! Generated both paper.json files.");
    }

    private JsonObject createBasePaper() {
        JsonObject base = new JsonObject();
        base.addProperty("parent", "minecraft:item/generated");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "minecraft:item/paper");
        base.add("textures", textures);

        return base;
    }

    private JsonObject createItemsPaper(JsonArray cases) {
        JsonObject root = new JsonObject();

        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:select");
        model.addProperty("property", "minecraft:custom_model_data");

        JsonObject fallback = new JsonObject();
        fallback.addProperty("type", "model");
        fallback.addProperty("model", "minecraft:item/paper");

        model.add("fallback", fallback);
        model.add("cases", cases);

        root.add("model", model);
        return root;
    }

    private void processModel(Path path, JsonArray overrides, JsonArray cases) {
        try {
            JsonObject root = JsonParser.parseString(Files.readString(path)).getAsJsonObject();

            if (!root.has("elements") || !root.get("elements").isJsonArray()) {
                return;
            }

            String modelName = stripExt(path.getFileName().toString());
            models.add(modelName.toUpperCase());
            copyTexture(path, modelName);

            JsonArray elements = root.getAsJsonArray("elements");

            for (JsonElement e : elements) {
                JsonObject part = e.getAsJsonObject();
                if (!part.has("name")) continue;

                String partName = part.get("name").getAsString().replace(" ", "_");
                JsonObject partCopy = part.deepCopy();
                if (partCopy.has("rotation")) {
                    JsonObject rotation = partCopy.getAsJsonObject("rotation");
                    if (rotation.has("origin")) {
                        JsonArray origin = rotation.getAsJsonArray("origin");
                        // меняем [8, 0, 8] → [8, 8, 8] (центр ItemDisplay)
                        origin.set(0, new JsonPrimitive(0.0));
                        origin.set(1, new JsonPrimitive(0.0));
                        origin.set(2, new JsonPrimitive(0.0));
                    }
                }

                if (partCopy.has("rotation")) {
                    JsonObject rotation = partCopy.getAsJsonObject("rotation");
                    if (rotation.has("origin")) {
                        shiftSafe(rotation.getAsJsonArray("origin"));
                    }
                }
                String itemName = modelName + "/" + partName;

                JsonObject model = new JsonObject();
                model.addProperty("format_version", root.has("format_version")
                        ? root.get("format_version").getAsString()
                        : "1.21.11");

                JsonArray textureSize = new JsonArray();
                if (root.has("texture_size") && root.get("texture_size").isJsonArray()) {
                    JsonArray src = root.getAsJsonArray("texture_size");
                    textureSize.add(src.size() > 0 ? src.get(0).getAsInt() : 64);
                    textureSize.add(src.size() > 1 ? src.get(1).getAsInt() : 64);
                } else {
                    textureSize.add(64);
                    textureSize.add(64);
                }
                model.add("texture_size", textureSize);

                JsonObject textures = new JsonObject();
                textures.addProperty("0", "minecraft:item/" + modelName);
                model.add("textures", textures);

                JsonArray arr = new JsonArray();
                arr.add(partCopy);
                model.add("elements", arr);

                model.add("display", createDisplay());

                Path modelFile = MODEL_DIR.resolve(modelName).resolve(partName + ".json");
                Files.createDirectories(modelFile.getParent());
                Files.writeString(modelFile, gson().toJson(model));

                // ---------- OLD (overrides) ----------
                JsonObject override = new JsonObject();
                JsonObject predicate = new JsonObject();
                predicate.addProperty("custom_model_data", customModelData);

                override.add("predicate", predicate);
                override.addProperty("model", "minecraft:item/" + itemName);

                overrides.add(override);

                // ---------- NEW (items select) ----------
                JsonObject caseObj = new JsonObject();
                caseObj.addProperty("when", String.valueOf(customModelData));

                JsonObject modelObj = new JsonObject();
                modelObj.addProperty("type", "model");
                modelObj.addProperty("model", "item/" + itemName);

                caseObj.add("model", modelObj);
                cases.add(caseObj);

                customModelData++;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void copyTexture(Path jsonPath, String modelName) {
        try {
            Path source = jsonPath.resolveSibling(modelName + ".png");
            if (!Files.exists(source)) {
                source = INPUT_DIR.resolve(modelName + ".png");
            }
            if (!Files.exists(source)) {
                System.out.println("No texture for: " + modelName);
                return;
            }

            Path target = TEXTURE_DIR.resolve(modelName + ".png");
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Copied texture: " + modelName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonObject createDisplay() {
        JsonObject display = new JsonObject();

        JsonObject hand = new JsonObject();
        hand.add("translation", vec(0, 3, 1));
        hand.add("scale", vec(0.6, 0.6, 0.6));

        display.add("thirdperson_righthand", hand);
        display.add("firstperson_righthand", hand);

        return display;
    }

    private JsonArray vec(double x, double y, double z) {
        JsonArray arr = new JsonArray();
        arr.add(x);
        arr.add(y);
        arr.add(z);
        return arr;
    }

    private void shiftSafe(JsonArray arr) {
        double x = arr.get(0).getAsDouble() - 8.0;
        double y = arr.get(1).getAsDouble();       // Y не трогаем
        double z = arr.get(2).getAsDouble() - 8.0;
        arr.set(0, new JsonPrimitive(Math.round(x * 100.0) / 100.0));
        arr.set(1, new JsonPrimitive(Math.round(y * 100.0) / 100.0));
        arr.set(2, new JsonPrimitive(Math.round(z * 100.0) / 100.0));
    }

    private String stripExt(String name) {
        int dot = name.lastIndexOf('.');
        return dot == -1 ? name : name.substring(0, dot);
    }

    private Gson gson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }
}