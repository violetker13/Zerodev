package org.example.comands.player;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import org.example.api.cursorapi.NewBody;
import org.example.api.zero_command.ZeroCommand;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class newbody extends Command implements ZeroCommand {

    public enum BodyActions { ON, OFF }

    private static final Map<UUID, NewBody> bodies = new HashMap<>();

    public newbody() {
        super("newbody");
        setUsage("/newbody <on|off>");
        ArgumentEnum<@NotNull BodyActions> action = ArgumentType.Enum("action", BodyActions.class)
                .setFormat(ArgumentEnum.Format.LOWER_CASED);
        addPlayerSyntax((player, context) -> {
            switch (context.get(action)) {
                case ON -> {
                    if (bodies.containsKey(player.getUuid())) {
                        sendError(player, "newbody уже активен!");
                        return;
                    }

                    bodies.put(player.getUuid(), NewBody.of(player));
                    player.sendMessage("newbody включён!");
                }
                case OFF -> {
                    NewBody body = bodies.remove(player.getUuid());
                    if (body == null) {
                        sendError(player, "newbody не активен!");
                        return;
                    }
                    body.remove();
                    player.sendMessage("newbody выключен!");
                }
            }
        }, action);
    }

    @Override
    public Command getCommand() { return this; }
}