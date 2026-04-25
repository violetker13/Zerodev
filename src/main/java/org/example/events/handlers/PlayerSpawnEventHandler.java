package org.example.events.handlers;

import net.minestom.server.entity.GameMode;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.ChangeGameStatePacket;
import net.minestom.server.network.packet.server.play.SetSlotPacket;
import org.example.events.EventHandler;

public class PlayerSpawnEventHandler extends EventHandler {
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(PlayerSpawnEvent.class, event -> {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);

        });
    }
}