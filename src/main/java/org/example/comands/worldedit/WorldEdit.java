package org.example.comands.worldedit;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;

import java.util.*;

public class WorldEdit {
    private static final Map<UUID, Selection> selections = new HashMap<>();
    private static final Map<UUID, Deque<Map<Point, Block>>> histories = new HashMap<>();

    private static final int MAX_HISTORY = 10;

    public static Selection getSelection(Player player) {
        return selections.computeIfAbsent(player.getUuid(), k -> new Selection());
    }

    public static Deque<Map<Point, Block>> getHistory(Player player) {
        return histories.computeIfAbsent(player.getUuid(), k -> new ArrayDeque<>());
    }

    public static void saveHistory(Player player, Map<Point, Block> oldBlocks) {
        var history = getHistory(player);
        if (history.size() >= MAX_HISTORY) {
            history.pollFirst();
        }
        history.push(oldBlocks);
    }
}