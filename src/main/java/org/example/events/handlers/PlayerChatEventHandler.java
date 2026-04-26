package org.example.events.handlers;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.SharedInstance;
import org.example.events.EventHandler;

import java.util.UUID;

public class PlayerChatEventHandler extends EventHandler {
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(PlayerChatEvent.class, event -> {
            //event.setCancelled(true);
            //new Test1(event.getPlayer());
            SharedInstance sharedInstance = new SharedInstance(UUID.randomUUID(),instance);
        });
    }
}