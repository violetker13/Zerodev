package org.example.comands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import org.example.Main;

public class server extends Command {
    public server() {
        super("server");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("Доступные серверы:", NamedTextColor.GOLD));
            Main.instances.forEach((id, inst) -> {
                int players = inst.getPlayers().size();
                sender.sendMessage(Component.text(
                        "  #" + id + " — " + players + " игроков",
                        NamedTextColor.YELLOW
                ));
            });
            sender.sendMessage(Component.text("Использование: /server <id>", NamedTextColor.GRAY));
        });

        var arg = ArgumentType.Integer("id");

        arg.setSuggestionCallback((sender, context, suggestion) -> {
            Main.instances.forEach((id, inst) -> {
                int players = inst.getPlayers().size();
                suggestion.addEntry(new SuggestionEntry(
                        String.valueOf(id),
                        Component.text("Инстанс #" + id + " (" + players + " игроков)")
                ));
            });
        });

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) {
                return;
            }

            int id = context.get(arg);
            InstanceContainer target = Main.getInstanceById(id);

            if (target == null) {
                player.sendMessage(Component.text(
                        "Инстанс #" + id + " не найден!", NamedTextColor.RED
                ));
                return;
            }

            if (player.getInstance() != null && player.getInstance().equals(target)) {
                player.sendMessage(Component.text(
                        "Вы уже на сервере #" + id + "!", NamedTextColor.YELLOW
                ));
                return;
            }

            player.sendMessage(Component.text(
                    "Перемещение на сервер #" + id + "...", NamedTextColor.GREEN
            ));
            player.setInstance(target, target.getPlayers().isEmpty()
                    ? player.getRespawnPoint()
                    : player.getPosition()
            );

        }, arg);
    }
}