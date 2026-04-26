package org.example.events.global;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.ping.Status;
import org.example.events.EventHandler;

import static net.minestom.server.MinecraftServer.getConnectionManager;

public class ServerListPingEventHandler extends EventHandler {
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(ServerListPingEvent.class, event -> {
            int onlinePlayers = getConnectionManager().getOnlinePlayerCount(); //server.getConnectionManager().getOnlinePlayerCount();

            event.setStatus(Status.builder()
                    .description(Component.text("Слава сталину!!!!", NamedTextColor.GOLD))
                    .playerInfo(Status.PlayerInfo.builder()
                            .onlinePlayers(onlinePlayers)
                            .maxPlayers(100)
                            .build())
                    .build());
        });
    }
}