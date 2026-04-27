package org.example.comands.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import org.example.api.zero_command.ZeroCommand;
import org.example.extras.PlayerUtils;
import org.example.extras.Utils;
import org.example.world.InstanceManager;
import org.jetbrains.annotations.NotNull;

public class kick extends Command implements ZeroCommand {
    public kick() {
        super("kick");
        setUsage("/kick <player>");
        var goal = ArgumentType.String("goal");
        var russianText = ArgumentType.StringArray("text");

        goal.setSuggestionCallback((sender, context, suggestion) -> {
            PlayerUtils.getAllPlayers().forEach((player) -> {
                var id = player.getUsername();
                suggestion.addEntry(new SuggestionEntry(
                        id,
                        Component.text("player " + id + " (" + player + " игроков)")
                                .color(NamedTextColor.GRAY)
                ));
            });
        });
        addAdminSyntax(((player, context) ->{
            String vitimName = context.get(goal);
            String text = String.join(" ", context.get(russianText));

            Player target = MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(vitimName);
            if(PlayerUtils.getAllPlayers().contains(target)) {

                target.kick(Utils.ColorizeText(text));
            } else {
                player.sendMessage(Utils.ColorizeText("&6Игрока" + vitimName +"не существует"));
            }
        }),goal,russianText);
    }

    @Override
    public Command getCommand() {
        return this;
    }
}
