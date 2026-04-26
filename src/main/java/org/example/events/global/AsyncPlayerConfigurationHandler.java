package org.example.events.global;

import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.instance.InstanceContainer;
import org.example.Main;
import org.example.database.DatabaseManager;
import org.example.events.EventHandler;
import org.example.extras.Utils;

import java.net.URI;
import java.nio.file.Path;
import java.util.UUID;

import static org.example.Main.packUrl;
import static org.example.extras.PlayerUtils.LoadResourcePack;
import static org.example.extras.PlayerUtils.getAllPlayers;

public class AsyncPlayerConfigurationHandler extends EventHandler {
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            if(!event.isFirstConfig()) return;
            String username = event.getPlayer().getUsername();
            var message = Component.text(username + " подключился к серверу.")
                    .color(NamedTextColor.YELLOW);
            getAllPlayers().forEach(plr -> plr.sendMessage(message));

            final Player player = event.getPlayer();
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
            event.setSpawningInstance(instance);
            player.setRespawnPoint(new Pos(5, 45, 5, 0, 0));
            LoadResourcePack(player);

            player.setPermissionLevel(4);


        });
    }
}