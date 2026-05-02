package org.example.comands.player;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.command.builder.arguments.number.ArgumentNumber;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import org.example.api.zero_command.ZeroCommand;
public class gamemode extends Command implements ZeroCommand {

    public gamemode() {
        super("gamemode", "gm");
        setUsage("/gamemode <mode|0-3>");

        ArgumentEnum<GameMode> modeArg = ArgumentType.Enum("mode", GameMode.class)
                .setFormat(ArgumentEnum.Format.LOWER_CASED);

        ArgumentNumber<Integer> numberArg = ArgumentType.Integer("mode")
                .between(0, 3);
        suggest(numberArg, "0", "1", "2", "3");
        addPlayerSyntax((player, context) -> {
            apply(player, context.get(modeArg));
        }, modeArg);

        addPlayerSyntax((player, context) -> {

            apply(player, GameMode.values()[context.get(numberArg)]);
        }, numberArg);
    }

    private void apply(Player player, GameMode mode) {
        player.setGameMode(mode);
        player.sendMessage("Режим игры изменён на: " + mode.name());
    }

    @Override
    public Command getCommand() {
        return this;
    }
}