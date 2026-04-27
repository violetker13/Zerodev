package org.example.comands.worldedit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.instance.InstanceContainer;
import org.example.api.zero_command.ZeroCommand;

public class PosCommand extends Command implements ZeroCommand {
    public PosCommand() {
        super("/pos");
        setUsage("//pos <1|2>");

        var numArg = ArgumentType.Integer("num");
        numArg.setSuggestionCallback((sender, context, suggestion) -> {
            suggestion.addEntry(new SuggestionEntry("1", Component.text("Установить Pos1")));
            suggestion.addEntry(new SuggestionEntry("2", Component.text("Установить Pos2")));
        });

        addPlayerSyntax((player, context) -> {
            var pos = player.getPosition();
            var instance = (InstanceContainer) player.getInstance();

            switch (context.get(numArg)) {
                case 1 -> {
                    WorldEdit.getSelection(player).setPos1(pos, instance);
                    player.sendMessage(Component.text(
                            "Pos1: " + pos.blockX() + " " + pos.blockY() + " " + pos.blockZ(),
                            NamedTextColor.AQUA
                    ));
                }
                case 2 -> {
                    WorldEdit.getSelection(player).setPos2(pos, instance);
                    player.sendMessage(Component.text(
                            "Pos2: " + pos.blockX() + " " + pos.blockY() + " " + pos.blockZ(),
                            NamedTextColor.AQUA
                    ));
                }
                default -> sendError(player, "Укажи 1 или 2!");
            }
        }, numArg);
    }

    @Override
    public Command getCommand() { return this; }
}