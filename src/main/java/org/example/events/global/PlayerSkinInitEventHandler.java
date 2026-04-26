package org.example.events.global;

import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSkinInitEvent;
import net.minestom.server.instance.InstanceContainer;
import org.example.database.DatabaseManager;
import org.example.events.EventHandler;

public class PlayerSkinInitEventHandler extends EventHandler {
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(PlayerSkinInitEvent.class, event -> {
            var player = event.getPlayer();
            String _skin = DatabaseManager.getSkinNick(player.getUuid());

            if (_skin != null) {
                Thread.startVirtualThread(() -> {
                    PlayerSkin skin = PlayerSkin.fromUsername(_skin);
                    if (skin != null) {
                        player.setSkin(skin);
                    }
                });
            }
        });
    }
}