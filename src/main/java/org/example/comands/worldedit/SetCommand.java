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
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class SetCommand extends Command {

    public SetCommand() {
        super("set"); // Убрал /, в Minestom префикс не нужен в названии

        var blockArg = ArgumentType.StringArray("block");

        // SuggestionCallback оставляем без изменений, он у тебя написан хорошо
        blockArg.setSuggestionCallback((sender, context, suggestion) -> {
            String raw = context.getRaw("block");
            if (raw == null) raw = "";
            String current = raw;
            int lastSep = Math.max(raw.lastIndexOf(","), Math.max(raw.lastIndexOf("["), raw.lastIndexOf(":")));
            if (lastSep >= 0) current = raw.substring(lastSep + 1);
            current = current.toLowerCase().trim();

            if (raw.isEmpty() || raw.equals("#")) {
                suggestion.addEntry(new SuggestionEntry("#perlin[10:90][stone,dirt,air]", Component.text("Перлин шум")));
                suggestion.addEntry(new SuggestionEntry("#random[stone,dirt,air]", Component.text("Случайные")));
                suggestion.addEntry(new SuggestionEntry("#weighted[stone:70,dirt:30]", Component.text("Веса %")));
                return;
            }

            final String filter = current;
            for (Block block : Block.values()) {
                String name = block.key().value();
                if (name.startsWith(filter)) {
                    String prefix = raw.substring(0, lastSep + 1);
                    suggestion.addEntry(new SuggestionEntry(prefix + name, Component.text(name)));
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

            List<Point> blocks = selection.getBlocks();
            // Защита от краша: лимит 1 млн блоков
            if (blocks.size() > 1_000_000) {
                player.sendMessage(Component.text("Область слишком велика! Макс: 1,000,000", NamedTextColor.RED));
                return;
            }

            String input = String.join(" ", context.get(blockArg)).trim();
            InstanceContainer instance = selection.getInstance();

            player.sendMessage(Component.text("Выполняю операцию...", NamedTextColor.YELLOW));

            Map<Point, Block> oldBlocks = new HashMap<>();
            for (Point p : blocks) {
                oldBlocks.put(p, instance.getBlock(p));
            }
            WorldEdit.saveHistory(player, oldBlocks);

            CompletableFuture.runAsync(() -> {
                AbsoluteBlockBatch batch = new AbsoluteBlockBatch();
                int count;

                try {
                    if (input.startsWith("#perlin")) {
                        count = applyNoise(blocks, batch, input);
                    } else if (input.startsWith("#random")) {
                        count = applyRandom(blocks, batch, input);
                    } else if (input.startsWith("#weighted")) {
                        count = applyWeighted(blocks, batch, input);
                    } else if (input.startsWith("#layer")) {
                        count = applyLayer(blocks, batch, minY(blocks), maxY(blocks), input);
                    } else {
                        Block block = Block.fromKey("minecraft:" + input.replace("minecraft:", ""));
                        if (block == null) throw new IllegalArgumentException("Блок не найден");
                        for (Point p : blocks) batch.setBlock(p, block);
                        count = blocks.size();
                    }

                    // Применяем изменения в основном потоке Minestom
                    // Применяем изменения в основном потоке Minestom
                    int finalCount = count;
                    batch.apply(instance, (instanceResult) -> { // Добавлен аргумент (instanceResult)
                        player.sendMessage(Component.text("Успешно! Изменено блоков: " + finalCount, NamedTextColor.GREEN));
                    });

                } catch (Exception e) {
                    player.sendMessage(Component.text("Ошибка: " + e.getMessage(), NamedTextColor.RED));
                }
            });

        }, blockArg);
    }

    private int applyNoise(List<Point> blocks, AbsoluteBlockBatch batch, String input) {
        String[] parts = input.split("\\[");
        String[] range = parts[1].replace("]", "").split(":");
        double minVal = Double.parseDouble(range[0]) / 100.0;
        double maxVal = Double.parseDouble(range[1]) / 100.0;
        List<Block> blockList = parseBlockList(parts[2].replace("]", ""));

        JNoise noise = JNoise.newBuilder()
                .perlin(PerlinNoiseGenerator.newBuilder().setSeed(System.currentTimeMillis()).build())
                .scale(0.05).build();

        int count = 0;
        for (Point pos : blocks) {
            double val = (noise.evaluateNoise(pos.x(), pos.y(), pos.z()) + 1.0) / 2.0;
            if (val >= minVal && val <= maxVal) {
                int idx = (int) ((val - minVal) / (maxVal - minVal) * blockList.size());
                idx = Math.min(idx, blockList.size() - 1);
                batch.setBlock(pos, blockList.get(idx));
                count++;
            }
        }
        return count;
    }

    private int applyRandom(List<Point> blocks, AbsoluteBlockBatch batch, String input) {
        String blockData = input.contains("[") ? input.split("\\[")[1].replace("]", "") : input;
        List<Block> blockList = parseBlockList(blockData);
        var rnd = ThreadLocalRandom.current(); // Быстрее чем new Random()

        for (Point pos : blocks) {
            batch.setBlock(pos, blockList.get(rnd.nextInt(blockList.size())));
        }
        return blocks.size();
    }

    private int applyWeighted(List<Point> blocks, AbsoluteBlockBatch batch, String input) {
        String[] parts = input.split("\\[");
        String[] entries = parts[1].replace("]", "").split(",");

        NavigableMap<Double, Block> cumulativeWeights = new TreeMap<>();
        double totalWeight = 0;

        for (String entry : entries) {
            String[] kv = entry.split(":");
            Block b = Block.fromKey("minecraft:" + kv[0].trim());
            double w = Double.parseDouble(kv[1].trim());
            if (b != null) {
                totalWeight += w;
                cumulativeWeights.put(totalWeight, b);
            }
        }

        var rnd = ThreadLocalRandom.current();
        for (Point pos : blocks) {
            double r = rnd.nextDouble() * totalWeight;
            batch.setBlock(pos, cumulativeWeights.ceilingEntry(r).getValue());
        }
        return blocks.size();
    }

    private int applyLayer(List<Point> blocks, AbsoluteBlockBatch batch, int minY, int maxY, String input) {
        List<Block> blockList = parseBlockList(input.split("\\[")[1].replace("]", ""));
        int height = Math.max(1, maxY - minY);

        for (Point pos : blocks) {
            double t = (double) (pos.blockY() - minY) / height;
            int idx = Math.min((int) (t * blockList.size()), blockList.size() - 1);
            batch.setBlock(pos, blockList.get(idx));
        }
        return blocks.size();
    }

    private List<Block> parseBlockList(String input) {
        List<Block> result = new ArrayList<>();
        for (String name : input.split(",")) {
            Block b = Block.fromKey("minecraft:" + name.trim().replace("minecraft:", ""));
            if (b != null) result.add(b);
        }
        return result;
    }

    private int minY(List<Point> blocks) { return blocks.stream().mapToInt(Point::blockY).min().orElse(0); }
    private int maxY(List<Point> blocks) { return blocks.stream().mapToInt(Point::blockY).max().orElse(0); }
}