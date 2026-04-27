package org.example.comands.worldedit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;

import java.util.concurrent.CompletableFuture;

public class UndoCommand extends Command {
    public UndoCommand() {
        super("undo");

        setDefaultExecutor((sender, context) -> {
            if (!(sender instanceof Player player)) return;

            var history = WorldEdit.getHistory(player);
            if (history.isEmpty()) {
                player.sendMessage(Component.text("Нечего отменять!", NamedTextColor.RED));
                return;
            }

            // Достаем последние сохраненные блоки
            var lastChange = history.pop();
            var instance = (InstanceContainer) player.getInstance();
            if (instance == null) return;

            player.sendMessage(Component.text("Отмена... (" + lastChange.size() + " блоков)", NamedTextColor.YELLOW));

            // Асинхронно применяем отмену через батч
            CompletableFuture.runAsync(() -> {
                AbsoluteBlockBatch batch = new AbsoluteBlockBatch();
                lastChange.forEach(batch::setBlock);

                batch.apply(instance, (inst) -> {
                    player.sendMessage(Component.text("Успешно отменено!", NamedTextColor.GREEN));
                });
            });
        });
    }
}