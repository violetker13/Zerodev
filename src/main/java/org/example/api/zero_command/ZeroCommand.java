package org.example.api.zero_command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import org.example.extras.PlayerUtils;

import java.util.Collection;
import java.util.function.BiConsumer;

public interface ZeroCommand {

    /**
     * Этот метод должен возвращать саму команду (обычно просто return this;)
     */
    Command getCommand();

    default void addPlayerSyntax(BiConsumer<Player, CommandContext> executor, Argument<?>... args) {
        getCommand().addSyntax((sender, context) -> {
            if (sender instanceof Player player) {
                executor.accept(player, context);
            } else {
                sender.sendMessage(Component.text("Эта команда доступна только игрокам!", NamedTextColor.RED));
            }
        }, args);
    }

    default void addConsoleOnlySyntax(BiConsumer<CommandSender, CommandContext> executor, Argument<?>... args) {
        getCommand().addSyntax((sender, context) -> {
            if (sender instanceof Player) {
                sender.sendMessage(Component.text("Эта команда доступна только из консоли!", NamedTextColor.RED));
                return;
            }
            executor.accept(sender, context);
        }, args);
    }


    default void addAdminSyntax(BiConsumer<Player, CommandContext> executor, Argument<?>... args) {
        getCommand().addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Только для игроков!", NamedTextColor.RED));
                return;
            }

            if (!PlayerUtils.isAdmin(player)) {
                player.sendMessage(Component.text("У тебя нет прав на выполнение этой команды!", NamedTextColor.RED));
                return;
            }

            executor.accept(player, context);
        }, args);
    }
    default void suggest(Argument<?> arg, String... options) {
        arg.setSuggestionCallback((sender, context, suggestion) -> {
            for (String option : options) {
                suggestion.addEntry(new SuggestionEntry(option));
            }
        });
    }

    default void setUsage(String usage) {
        getCommand().setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("Использование: " + usage, NamedTextColor.YELLOW));
        });
    }
    default void suggest(Argument<?> arg, Collection<String> options) {
        arg.setSuggestionCallback((sender, context, suggestion) -> {
            for (String option : options) {
                suggestion.addEntry(new SuggestionEntry(option));
            }
        });
    }

    default void suggestDynamic(Argument<?> arg, java.util.function.Supplier<Collection<String>> optionsSupplier) {
        arg.setSuggestionCallback((sender, context, suggestion) -> {
            for (String option : optionsSupplier.get()) {
                suggestion.addEntry(new SuggestionEntry(option));
            }
        });
    }
    default void sendError(Player player, String message) {
        player.sendMessage(Component.text(message, NamedTextColor.RED));
    }
}