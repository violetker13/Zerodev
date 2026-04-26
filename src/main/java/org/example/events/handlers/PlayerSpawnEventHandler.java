package org.example.events.handlers;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.timer.TaskSchedule;
import org.example.events.EventHandler;

public class PlayerSpawnEventHandler extends EventHandler {
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(PlayerSpawnEvent.class, event -> {
            Player player = event.getPlayer();
            player.sendActionBar(Component.text("Добро пожаловать на сервер!"));
        });
    }
}