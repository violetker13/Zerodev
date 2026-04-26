package org.example.events.handlers;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.SharedInstance;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.network.player.PlayerSocketConnection;
import org.example.Main;
import org.example.events.EventHandler;
import org.example.extras.PlayerUtils;
import org.example.extras.Utils;

import java.util.UUID;

import static org.example.Main.server;

public class PlayerChatEventHandler extends EventHandler {
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(PlayerChatEvent.class, event -> {
            event.setCancelled(true);

// В чате
            PlayerUtils.getAllPlayers().forEach(player -> player.sendMessage(
                    Utils.ColorizeText("&6Ранг&b " + event.getPlayer().getUsername() + "&7: &f" + event.getRawMessage())
            ));
        });
    }
}