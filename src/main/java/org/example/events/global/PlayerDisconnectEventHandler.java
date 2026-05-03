package org.example.events.global;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.instance.InstanceContainer;
import org.example.events.EventHandler;

import static org.example.extras.PlayerUtils.getAllPlayers;

public class PlayerDisconnectEventHandler extends EventHandler {
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(PlayerDisconnectEvent.class, event -> {
            // Получаем имя игрока заранее
            String username = event.getPlayer().getUsername();

            var message = Component.text(username + " отключился от сервера.")
                    .color(NamedTextColor.BLUE);
            getAllPlayers().forEach(plr -> plr.sendMessage(message));
        });
    }
}