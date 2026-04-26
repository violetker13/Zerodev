package org.example.extras;

import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.example.Main;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.example.Main.ADMINS;
import static org.example.Main.server;

public class PlayerUtils {
    //если игрок админ
    public static boolean isAdmin(Player player) {
        return ADMINS.contains(player.getUsername());

    }
    // MinecraftServer.TICK_MS

    //возвращает сет всех игроков на СЕРВЕРЕ!
    public static Collection<Player> getAllPlayers() {
        return server.getConnectionManager().getOnlinePlayers();


    }

    //загрузчик рп resourcepack/pack.zip, который должен быть в папке рядом с jar файлом, при загрузке игрока на сервер
    public static void LoadResourcePack(Player player) {
        if (Main.packUrl != null) {player.sendResourcePacks(ResourcePackRequest.resourcePackRequest().packs(ResourcePackInfo.resourcePackInfo().id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).uri(URI.create(Main.packUrl)).hash(Main.packHash).build()).required(false).build());
        }
    }



}
