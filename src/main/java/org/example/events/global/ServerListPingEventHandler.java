package org.example.events.global;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.object.ObjectContents;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.ping.Status;
import org.example.events.EventHandler;
import org.example.extras.Utils;

import static java.lang.Math.clamp;
import static net.minestom.server.MinecraftServer.getConnectionManager;

public class ServerListPingEventHandler extends EventHandler {
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(ServerListPingEvent.class, event -> {
            int onlinePlayers = getConnectionManager().getOnlinePlayerCount(); //server.getConnectionManager().getOnlinePlayerCount();



            event.setStatus(Status.builder()
                    .versionInfo(new Status.VersionInfo("version", MinecraftServer.PROTOCOL_VERSION))
                    .playerInfo(MinecraftServer.getConnectionManager().getOnlinePlayerCount(),clamp(MinecraftServer.getConnectionManager().getOnlinePlayerCount()+1,10,1000))
                            .description(Utils.ColorizeText("&7 - Лучше построить своё с нуля, чем перестраивать чужое"))

                    .build());

        });
    }
}