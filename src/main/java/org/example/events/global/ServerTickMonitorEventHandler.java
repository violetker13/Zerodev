package org.example.events.global;

import com.sun.management.OperatingSystemMXBean;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.instance.InstanceContainer;
import org.example.events.EventHandler;
import org.example.extras.PlayerUtils;
import org.example.extras.Utils;

import java.awt.*;

public class ServerTickMonitorEventHandler extends EventHandler {

    // Счетчик тиков
    private int tickCount = 0;
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(ServerTickMonitorEvent.class, event -> {
            if (tickCount++ % 20 != 0) {
                return;//-Xmx256M -Xms256M
            }
            var monitor = event.getTickMonitor();
            long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
            long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;

            double mspt = monitor.getTickTime();

            double tps = Math.min(20.0, 1000.0 / Math.max(1.0, mspt));

            String footerTemplate = String.format(
                    "&6Твой пинг: &f%%d&6ms\n\n" +
                            "&2TPS: &a%.2f &8| &2MSPT: &d%.2fms\n" +
                            "&2RAM: &e%dMB &7/ &e%dMB",
                    tps, mspt, usedMemory, maxMemory
            );
            var header = Utils.ColorizeText("&6Тестирование\n\n&8ZeroDev");

            PlayerUtils.getAllPlayers().forEach(player -> {
                player.sendPacketsToViewers();
                player.sendPlayerListHeaderAndFooter(
                        header,
                        Utils.ColorizeText(String.format(footerTemplate, player.getLatency()))
                );
            });
        });
    }
}