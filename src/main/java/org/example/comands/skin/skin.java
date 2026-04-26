package org.example.comands.skin;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import org.example.api.zero_command.ZeroCommand;
import org.example.database.DatabaseManager;
import org.example.extras.PlayerUtils;


public class skin extends Command implements ZeroCommand {

    public skin() {
        super("skin", "скин","zero-skin");
        setUsage("/skin <set/info> <url>");
        var setLiteral = ArgumentType.Literal("set");
        var urlArg = ArgumentType.String("url");

        addAdminSyntax((player, context) -> {
            String targetNick = context.get(urlArg);
            Thread.startVirtualThread(() -> {
                PlayerSkin skin = PlayerSkin.fromUsername(targetNick);

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
