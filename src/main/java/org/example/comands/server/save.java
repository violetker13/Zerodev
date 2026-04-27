package org.example.comands.server;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.instance.InstanceContainer;
import org.example.api.zero_command.ZeroCommand; // Твой интерфейс
import org.example.extras.Utils;
import org.example.world.InstanceManager;

public class save extends Command implements ZeroCommand {
    public save() {
        super("save", "сохранить");
        setUsage("/save");
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

        addAdminSyntax((player, context) -> {
            String idStr = context.get(arg);

            InstanceContainer target = InstanceManager.getInstanceById(idStr);

            if (target == null) {
                player.sendMessage(Component.text("Мир " + idStr + " не найден").color(NamedTextColor.RED));
                return;
            }
            target.saveChunksToStorage().thenRun(() -> {
                player.sendMessage(Utils.ColorizeText("Мир &e" + idStr + " &eуспешно сохранён"));
            });
        },arg);
    }

    @Override
    public Command getCommand() {
        return this;
    }
}