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





}
