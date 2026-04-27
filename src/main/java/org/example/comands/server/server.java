package org.example.comands.server;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.instance.InstanceContainer;
import org.example.api.zero_command.ZeroCommand;
import org.example.world.InstanceManager;
import org.example.world.Worlds;

import static org.example.world.InstanceManager.getInstanceById;

public class server extends Command implements ZeroCommand {   // лучше назвать ServerCommand

    public server() {
        super("server");
        setUsage("/server <id>");

        var arg = ArgumentType.String("id");

        arg.setSuggestionCallback((sender, context, suggestion) -> {
            InstanceManager.getInstances().forEach((world, inst) -> {
                String id = world.name();
                suggestion.addEntry(new SuggestionEntry(
                        id,
                        Component.text("Инстанс " + id + " (" + inst.getPlayers().size() + " игроков)")
                                .color(NamedTextColor.GRAY)
                ));
            });
        });

        addPlayerSyntax((player, context) -> {
            String idStr = context.get(arg);

            // Ищем по строке
            InstanceContainer target = null;
            for (Worlds w : Worlds.values()) {
                if (w.name().equalsIgnoreCase(idStr)) {
                    target = InstanceManager.getInstanceById(w);
                    break;
                }
            }

            if (target == null) {
                player.sendMessage(Component.text("Инстанс " + idStr + " не найден!").color(NamedTextColor.RED));
                return;
            }

            if (player.getInstance() != null && player.getInstance().equals(target)) {
                player.sendMessage(Component.text("Вы уже на этом инстансе!").color(NamedTextColor.RED));
                return;
            }

            player.sendMessage("Перемещение на " + idStr + "...");
            player.setInstance(target, target.getPlayers().isEmpty()
                    ? player.getRespawnPoint()
                    : player.getPosition()
            );
        }, arg);
    }

    @Override
    public Command getCommand() {
        return this;
    }
}