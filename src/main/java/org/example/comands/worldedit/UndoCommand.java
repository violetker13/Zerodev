package org.example.comands.worldedit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;

import java.util.*;

public class UndoCommand extends Command {
    public UndoCommand() {
        super("/undo");

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) return;

            var history = WorldEdit.getHistory(player);
            if (history.isEmpty()) {
                player.sendMessage(Component.text("Нечего отменять!", NamedTextColor.RED));
                return;
            }

            // Берём последнее действие
            Map<Point, Block> last = history.pop();
            var instance = (net.minestom.server.instance.InstanceContainer) player.getInstance();

            // Восстанавливаем блоки
            for (Map.Entry<Point, Block> entry : last.entrySet()) {
                instance.setBlock(entry.getKey(), entry.getValue());
            }

            player.sendMessage(Component.text(
                    "Отменено " + last.size() + " блоков!",
                    NamedTextColor.GREEN
            ));
        });
    }
}