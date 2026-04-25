package org.example.comands;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerInputEvent;
import net.minestom.server.network.player.ClientSettings;
import org.jetbrains.annotations.NotNull;

public class gamemode extends Command {


    public gamemode() {
        super("gamemode");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("Использование: /gamemode <creative|survival|adventure|spectator>"));
        });

        ArgumentEnum<@NotNull GameMode> mode = ArgumentType.Enum("mode", GameMode.class)
                .setFormat(ArgumentEnum.Format.LOWER_CASED);
        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Только для игроков!"));
                return;
            }
            GameMode selectedMode = context.get(mode);
            player.setGameMode(selectedMode);
            player.sendMessage(Component.text("Gamemode изменён на: " + selectedMode.name()));

        }, mode);
    }
}
