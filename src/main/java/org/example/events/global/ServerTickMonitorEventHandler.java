package org.example.events.global;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.instance.InstanceContainer;
import org.example.events.EventHandler;
import org.example.extras.PlayerUtils;
import org.example.extras.Utils;

import static org.example.world.InstanceManager.getIdByInstance;
public class ServerTickMonitorEventHandler extends EventHandler {
    private int tickCount = 0;

    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        // Запускаем проверку каждые 10 секунд в отдельном потоке


        node.addListener(ServerTickMonitorEvent.class, event -> {
            if (tickCount++ % 20 != 0) return;

            var monitor = event.getTickMonitor();
            long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
            long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
            double mspt = monitor.getTickTime();
            double tps = Math.min(20.0, 1000.0 / Math.max(1.0, mspt));
            String footerTemplate = "&6Твой пинг: &f%dms\n\n" +
                    "&2TPS: &a%.2f &8| &2MSPT: &d%.2fms\n" +
                    "&2RAM: &e%dMB &7/ &e%dMB";
            var headerBase = Utils.ColorizeText(
                    "&6Тестирование\n\n&8ZeroDev"
            );
            PlayerUtils.getAllPlayers().forEach(player -> {
                player.sendPlayerListHeaderAndFooter(
                        headerBase.append(Utils.ColorizeText("\n&7Сервер: &f" + getIdByInstance(player.getInstance()))),
                        Utils.ColorizeText(String.format(footerTemplate, player.getLatency(), tps, mspt, usedMemory, maxMemory))
                );
            });
        });
    }

}