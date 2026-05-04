package org.example.events.global;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.instance.InstanceContainer;
import org.example.events.EventHandler;
import org.example.extras.Utils;
import org.example.world.InstanceManager;

public class PlayerChatEventHandler extends EventHandler {
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(PlayerChatEvent.class, event -> {
            event.setCancelled(true);
            var mes = event.getRawMessage();
            if (mes.startsWith("!")) {
                // Глобальный чат — отправляем всем инстансам
                if (mes.length() > 1) { // есть текст после !
                    InstanceManager.getInstances().forEach((world, instanceContainer) -> {
                        instanceContainer.sendMessage(Utils.ColorizeText(
                                "&6[!] &4" + event.getPlayer().getUsername() + "&7: &f" + mes.substring(1).trim()
                        ));
                    });
                }
            } else {
                // Локальный чат — только текущий инстанс
                event.getInstance().sendMessage(Utils.ColorizeText(
                        "&2[" + InstanceManager.getIdByInstance(event.getInstance()) + "] &4"
                                + event.getPlayer().getUsername() + "&7: &f" + mes.trim()
                ));
            }
        });
    }
}