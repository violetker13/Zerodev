package org.example.comands;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import org.example.extras.PlayerUtils;
import org.jetbrains.annotations.NotNull;
import static org.example.Main.instances;

public class save extends Command {
    public save() {
        super("save");
        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text(": /save"));
        });
        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Только для игроков!"));
                return;
            }
            if(!PlayerUtils.isAdmin(player)) {player.sendMessage("У тебя нет прав на эту команду!");return;}


                instances.forEach((id, uinst) -> {
                uinst.saveChunksToStorage().thenRun(() -> {
                    player.sendMessage(Component.text("Мир " + id + " сохранён"));
                });
            });

        });
    }
}
