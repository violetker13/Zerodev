package org.example.extras;

import net.minestom.server.entity.Player;

import java.util.Collection;

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
