package org.example.events.global;

import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.instance.InstanceContainer;
import org.example.Main;
import org.example.events.EventHandler;
import org.example.extras.Utils;

import java.net.URI;
import java.nio.file.Path;
import java.util.UUID;

import static org.example.Main.packUrl;

public class AsyncPlayerConfigurationHandler extends EventHandler {
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(AsyncPlayerConfigurationEvent.class, event -> {

            final Player player = event.getPlayer();
            event.getPlayer().setGameMode(GameMode.SURVIVAL);

            event.setSpawningInstance(instance);

            player.setRespawnPoint(new Pos(5, 45, 5, 0, 0));

            if (Main.packUrl != null) {
                player.sendResourcePacks(ResourcePackRequest.resourcePackRequest()
                        .packs(ResourcePackInfo.resourcePackInfo()
                                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                                .uri(URI.create(Main.packUrl))
                                .hash(Main.packHash) // добавь хеш
                                .build())
                        .required(true)
                        .build()
                );
            }


            player.setPermissionLevel(4);


        });
    }
}