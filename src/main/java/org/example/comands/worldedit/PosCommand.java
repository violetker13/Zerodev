package org.example.comands.worldedit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;

public class PosCommand extends Command {
    public PosCommand() {
        super("/pos");  // /pos или /we

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("Использование: /pos <1|2>", NamedTextColor.RED));
        });

        var numArg = ArgumentType.Integer("num");
        numArg.setSuggestionCallback((sender, context, suggestion) -> {
            suggestion.addEntry(new SuggestionEntry("1", Component.text("Установить Pos1")));
            suggestion.addEntry(new SuggestionEntry("2", Component.text("Установить Pos2")));
        });

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) return;

            int num = context.get(numArg);
            var pos = player.getPosition();
            var instance = (InstanceContainer) player.getInstance();

            switch (num) {
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
                default -> sender.sendMessage(Component.text("Укажи 1 или 2!", NamedTextColor.RED));
            }
        }, numArg);
    }
}