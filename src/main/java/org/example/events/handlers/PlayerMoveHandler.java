package org.example.events.handlers;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerMoveEvent; // конфликт с именем класса!
import net.minestom.server.instance.InstanceContainer;
import org.example.events.EventHandler;

public class PlayerMoveHandler extends EventHandler { // ← переименуй на Handler
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(PlayerMoveEvent.class, event -> {
            Pos newPos = event.getNewPosition();

            // Телепорт если упал
            if (newPos.y() < -10) {
                event.getPlayer().teleport(newPos.add(0, 100, 0));

            }
           // event.getPlayer().sendActionBar(Component.text(newPos.toString()));
        });
    }
}