package org.example.comands.player;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;
import org.example.api.zero_command.ZeroCommand;

public class gamemode extends Command implements ZeroCommand {
    public gamemode() {
        super("gamemode", "gm");
        setUsage("/gamemode <creative|survival|adventure|spectator>");

        ArgumentEnum<GameMode> mode = ArgumentType.Enum("mode", GameMode.class)
                .setFormat(ArgumentEnum.Format.LOWER_CASED);

        addPlayerSyntax((player, context) -> {
            GameMode selectedMode = context.get(mode);
            player.setGameMode(selectedMode);
            player.sendMessage("Gamemode изменён на: " + selectedMode.name());
        }, mode);
    }

    @Override
    public Command getCommand() { return this; }
}