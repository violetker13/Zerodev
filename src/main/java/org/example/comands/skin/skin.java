package org.example.comands.skin;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.avatar.PlayerMeta;
import net.minestom.server.network.NetworkBuffer;
import org.example.api.zero_command.ZeroCommand;
import org.example.database.DatabaseManager;
import org.example.extras.PlayerUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;


public class skin extends Command implements ZeroCommand {

    public skin() {
        super("skin", "скин","zero-skin");
        setUsage("/skin <set> <player>");
        var setLiteral = ArgumentType.Literal("set");
        var urlArg = ArgumentType.String("player");

        addPlayerSyntax((player, context) -> {
            String targetNick = context.get(urlArg);
            Thread.startVirtualThread(() -> {
                PlayerSkin skin = PlayerSkin.fromUsername(targetNick);
                SocketAddress remote = player.getPlayerConnection().getRemoteAddress();

                if (skin != null) {
                    DatabaseManager.saveSkin(player.getUuid(), targetNick);
                    player.setSkin(skin);
                    player.sendMessage("Скин игрока " + targetNick + " успешно загружен в базу и применен!");
                } else {
                    player.sendMessage("Ошибка: не удалось найти скин для ника " + targetNick);
                }
            });
        }, setLiteral, urlArg);
    }

    @Override
    public Command getCommand() {
        return this;
    }
}
