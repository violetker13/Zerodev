package org.example.comands;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import org.example.api.zero_command.ZeroCommand; // Твой интерфейс
import org.example.world.InstanceManager;

public class save extends Command implements ZeroCommand {
    public save() {
        super("save", "сохранить");

        setUsage("/save");
        addAdminSyntax((player, context) -> {
            var allInstances = InstanceManager.getInstances();
            if (allInstances.isEmpty()) {
                player.sendMessage(Component.text("Нет активных миров для сохранения."));
                return;
            }
            allInstances.forEach((id, uinst) -> {
                uinst.saveChunksToStorage().thenRun(() -> {
                    player.sendMessage(Component.text("Мир №" + id + " успешно сохранён на диск!"));
                });
            });

        });
    }

    @Override
    public Command getCommand() {
        return this;
    }
}