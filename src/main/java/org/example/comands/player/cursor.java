package org.example.comands.player;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import org.example.api.cursorapi.Cursor;
import org.example.api.zero_command.ZeroCommand;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class cursor extends Command implements ZeroCommand {

    public enum CursorAction { ON, OFF }

    private static final Map<UUID, Cursor> cursors = new HashMap<>();

    public cursor() {
        super("cursor");
        setUsage("/cursor <on|off>");

        ArgumentEnum<@NotNull CursorAction> action = ArgumentType.Enum("action", CursorAction.class)
                .setFormat(ArgumentEnum.Format.LOWER_CASED);

        addPlayerSyntax((player, context) -> {
            switch (context.get(action)) {
                case ON -> {
                    if (cursors.containsKey(player.getUuid())) {
                        sendError(player, "Курсор уже активен!");
                        return;
                    }
                    cursors.put(player.getUuid(), Cursor.of(player));
                    player.sendMessage("Курсор включён!");
                }
                case OFF -> {
                    Cursor cursor = cursors.remove(player.getUuid());
                    if (cursor == null) {
                        sendError(player, "Курсор не активен!");
                        return;
                    }
                    cursor.remove();
                    player.sendMessage("Курсор выключен!");
                }
            }
        }, action);
    }

    @Override
    public Command getCommand() { return this; }
}