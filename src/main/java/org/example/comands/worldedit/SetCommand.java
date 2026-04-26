package org.example.comands.worldedit;

import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator;
import de.articdive.jnoise.pipeline.JNoise;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;

import java.util.*;

public class SetCommand extends Command {

    public SetCommand() {
        super("/set");

        var blockArg = ArgumentType.StringArray("block");

        blockArg.setSuggestionCallback((sender, context, suggestion) -> {
            String raw = context.getRaw("block");
            if (raw == null) raw = "";

            // Определяем что сейчас печатается
            // Последний блок после запятой или скобки
            String current = raw;
            int lastComma = raw.lastIndexOf(",");
            int lastBracket = raw.lastIndexOf("[");
            int lastColon = raw.lastIndexOf(":");

            // Определяем позицию с которой начинается текущий ввод блока
            int lastSep = Math.max(lastComma, Math.max(lastBracket, lastColon));
            if (lastSep >= 0) {
                current = raw.substring(lastSep + 1);
            }
            current = current.toLowerCase().trim();

            // Подсказки паттернов если ввод пустой или начинается с #
            if (raw.isEmpty() || raw.equals("#")) {
                suggestion.addEntry(new SuggestionEntry("#perlin[10:90][stone,dirt,air]",
                        Component.text("Перлин шум")));
                suggestion.addEntry(new SuggestionEntry("#random[stone,dirt,air]",
                        Component.text("Случайные блоки")));
                suggestion.addEntry(new SuggestionEntry("#weighted[stone:70,dirt:20,air:10]",
                        Component.text("Блоки с весами %")));
                suggestion.addEntry(new SuggestionEntry("#layer[stone,dirt,grass_block]",
                        Component.text("Слои по Y")));
                return;
            }

            // Подсказки блоков — везде где ожидается название блока
            boolean expectingBlock =
                    !raw.startsWith("#") ||          // обычный блок
                            raw.contains(",") ||             // перечисление через запятую
                            raw.contains("[") && !raw.endsWith("]"); // внутри скобок паттерна

            if (expectingBlock && !current.isEmpty()) {
                final String filter = current;
                for (Block block : Block.values()) {
                    String name = block.key().value();
                    if (name.startsWith(filter)) {
                        // Строим полную подсказку заменяя последнее слово
                        String prefix = raw.substring(0, lastSep + 1);
                        suggestion.addEntry(new SuggestionEntry(
                                prefix + name,
                                Component.text(name)
                        ));
                    }
                }
            }
        });

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) return;

            var selection = WorldEdit.getSelection(player);
            if (!selection.isComplete()) {
                player.sendMessage(Component.text("Сначала выдели область!", NamedTextColor.RED));
                return;
            }

            String input = String.join(" ", context.get(blockArg)).trim();
            var instance = selection.getInstance();
            var blocks = selection.getBlocks();
            int count = 0;

            try {
                // Сохраняем историю
                Map<Point, Block> oldBlocks = new HashMap<>();
                for (var pos : blocks) {
                    oldBlocks.put(pos, instance.getBlock(pos));
                }
                WorldEdit.saveHistory(player, oldBlocks);

                if (input.startsWith("#perlin")) {
                    count = applyNoise(blocks, instance, input);
                } else if (input.startsWith("#random")) {
                    count = applyRandom(blocks, instance, input);
                } else if (input.startsWith("#weighted")) {
                    count = applyWeighted(blocks, instance, input);
                } else if (input.startsWith("#layer")) {
                    count = applyLayer(blocks, instance, input);
                } else {
                    if (input.contains(",")) {
                        // Несколько блоков через запятую — случайный выбор
                        count = applyRandom(blocks, instance, "#random[" + input + "]");
                    } else {
                        // Один блок
                        Block block = Block.fromKey("minecraft:" + input);
                        if (block == null) {
                            player.sendMessage(Component.text("Блок не найден: " + input, NamedTextColor.RED));
                            return;
                        }
                        for (var pos : blocks) {
                            instance.setBlock(pos, block);
                            count++;
                        }
                    }
                }

                player.sendMessage(Component.text("Установлено " + count + " блоков!", NamedTextColor.GREEN));

            } catch (Exception e) {
                player.sendMessage(Component.text("Ошибка: " + e.getMessage(), NamedTextColor.RED));
                e.printStackTrace();
            }

        }, blockArg);
    }

    private int applyNoise(List<Point> blocks, InstanceContainer instance, String input) {
        // #perlin[10:90][stone,dirt,air]
        String[] parts = input.split("\\[");
        String[] range = parts[1].replace("]", "").split(":");
        double minVal = Double.parseDouble(range[0]) / 100.0;
        double maxVal = Double.parseDouble(range[1]) / 100.0;
        List<Block> blockList = parseBlockList(parts[2].replace("]", ""));

        JNoise noise = JNoise.newBuilder()
                .perlin(PerlinNoiseGenerator.newBuilder()
                        .setSeed(System.currentTimeMillis()).build())
                .scale(0.05).build();

        int count = 0;
        for (Point pos : blocks) {
            double val = (noise.evaluateNoise(pos.x(), pos.y(), pos.z()) + 1.0) / 2.0;
            if (val >= minVal && val <= maxVal) {
                int idx = (int) ((val - minVal) / (maxVal - minVal) * blockList.size());
                idx = Math.min(idx, blockList.size() - 1);
                instance.setBlock(pos, blockList.get(idx));
                count++;
            }
        }
        return count;
    }

    private int applyRandom(List<Point> blocks, InstanceContainer instance, String input) {
        // #random[stone,dirt,air]
        String[] parts = input.split("\\[");
        List<Block> blockList = parseBlockList(parts[1].replace("]", ""));
        Random random = new Random();

        int count = 0;
        for (Point pos : blocks) {
            instance.setBlock(pos, blockList.get(random.nextInt(blockList.size())));
            count++;
        }
        return count;
    }

    private int applyWeighted(List<Point> blocks, InstanceContainer instance, String input) {
        // #weighted[stone:70,dirt:20,air:10]
        String[] parts = input.split("\\[");
        String[] entries = parts[1].replace("]", "").split(",");

        List<Block> weightedList = new ArrayList<>();
        for (String entry : entries) {
            String[] kv = entry.trim().split(":");
            if (kv.length < 2) continue;
            Block block = Block.fromKey("minecraft:" + kv[0].trim());
            int weight = Integer.parseInt(kv[1].trim());
            if (block != null) {
                for (int i = 0; i < weight; i++) weightedList.add(block);
            }
        }

        Random random = new Random();
        int count = 0;
        for (Point pos : blocks) {
            instance.setBlock(pos, weightedList.get(random.nextInt(weightedList.size())));
            count++;
        }
        return count;
    }

    private int applyLayer(List<Point> blocks, InstanceContainer instance, String input) {
        // #layer[stone,dirt,grass_block]
        String[] parts = input.split("\\[");
        List<Block> blockList = parseBlockList(parts[1].replace("]", ""));

        int minY = blocks.stream().mapToInt(Point::blockY).min().orElse(0);
        int maxY = blocks.stream().mapToInt(Point::blockY).max().orElse(0);
        int height = Math.max(1, maxY - minY);

        int count = 0;
        for (Point pos : blocks) {
            double t = (double)(pos.blockY() - minY) / height;
            int idx = Math.min((int)(t * blockList.size()), blockList.size() - 1);
            instance.setBlock(pos, blockList.get(idx));
            count++;
        }
        return count;
    }

    private List<Block> parseBlockList(String input) {
        List<Block> result = new ArrayList<>();
        for (String name : input.split(",")) {
            Block b = Block.fromKey("minecraft:" + name.trim());
            if (b != null) result.add(b);
        }
        return result;
    }
}