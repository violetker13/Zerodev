package org.example.comands.server;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import org.example.api.zero_command.ZeroCommand;
import org.example.extras.PlayerUtils;
import org.example.extras.Utils;

public class stop extends Command implements ZeroCommand
{
    public stop() {
        super("stop");
        addAdminSyntax((player, context) -> {
            PlayerUtils.getAllPlayers().forEach((players ->{
                players.kick(Utils.ColorizeText("&cСервер выключен"));
            }));
            MinecraftServer.stopCleanly();
        });
    }

    @Override
    public Command getCommand() {
        return this;
    }
}
