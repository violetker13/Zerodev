package org.example.comands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.instance.InstanceContainer;
import org.example.api.zero_command.ZeroCommand;

import static org.example.world.InstanceManager.getInstanceById;
import static org.example.world.InstanceManager.getInstances;

public class server extends Command implements ZeroCommand {
    public server() {
        super("server");
        setUsage("/server <id>");

        getCommand().setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("Доступные серверы:", NamedTextColor.GOLD));
            getInstances().forEach((id, inst) -> sender.sendMessage(Component.text(
                    "  #" + id + " — " + inst.getPlayers().size() + " игроков",
                    NamedTextColor.YELLOW
            )));
        });

        var arg = ArgumentType.Integer("id");
        arg.setSuggestionCallback((sender, context, suggestion) ->
                getInstances().forEach((id, inst) -> suggestion.addEntry(new SuggestionEntry(
                        String.valueOf(id),
                        Component.text("Инстанс #" + id + " (" + inst.getPlayers().size() + " игроков)")
                )))
        );

        addPlayerSyntax((player, context) -> {
            int id = context.get(arg);
            InstanceContainer target = getInstanceById(id);

            if (target == null) {
                sendError(player, "Инстанс #" + id + " не найден!");
                return;
            }
            if (player.getInstance() != null && player.getInstance().equals(target)) {
                sendError(player, "Вы уже на сервере #" + id + "!");
                return;
            }

            player.sendMessage("Перемещение на сервер #" + id + "...");
            player.setInstance(target, target.getPlayers().isEmpty()
                    ? player.getRespawnPoint()
                    : player.getPosition()
            );
        }, arg);
    }

    @Override
    public Command getCommand() { return this; }
}