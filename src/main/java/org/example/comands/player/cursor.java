package org.example.comands.player;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import org.example.api.cursorapi.Cursor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class cursor extends Command {

    public enum CursorAction {
        ON, OFF
    }

    // Хранилище курсоров по игроку
    private static final Map<UUID, Cursor> cursors = new HashMap<>();

    public cursor() {
        super("cursor");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("Использование: /cursor <on|off>"));
        });

        ArgumentEnum<CursorAction> action = ArgumentType.Enum("action", CursorAction.class)
                .setFormat(ArgumentEnum.Format.LOWER_CASED);

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Только для игроков!"));
                return;
            }
            CursorAction selectedAction = context.get(action);
            switch (selectedAction) {
                case ON -> {
                    if (cursors.containsKey(player.getUuid())) {
                        player.sendMessage(Component.text("Курсор уже активен!"));
                        return;
                    }
                    Cursor cursor = Cursor.of(player);
                    cursors.put(player.getUuid(), cursor);
                    player.sendMessage(Component.text("Курсор включён!"));
                }
                case OFF -> {
                    Cursor cursor = cursors.remove(player.getUuid());
                    if (cursor == null) {
                        player.sendMessage(Component.text("Курсор не активен!"));
                        return;
                    }
                    cursor.remove();
                    player.sendMessage(Component.text("Курсор выключен!"));
                }
            }
        }, action);
    }
}