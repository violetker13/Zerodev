package org.example.events.global;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerStartDiggingEvent;
import net.minestom.server.instance.InstanceContainer;
import org.example.comands.worldedit.WorldEdit;
import org.example.events.EventHandler;

import static org.example.comands.worldedit.WandCommand.isHoldingWand;

public class PlayerStartDiggingEventHandler extends EventHandler {
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(PlayerStartDiggingEvent.class, event -> {
            Player player = event.getPlayer();
            if (!isHoldingWand(player)) return;

            event.setCancelled(true);
            var pos = event.getBlockPosition();
            WorldEdit.getSelection(player).setPos1(pos, (InstanceContainer) player.getInstance());
            player.sendMessage(Component.text(
                    "Pos1: " + pos.blockX() + " " + pos.blockY() + " " + pos.blockZ(),
                    NamedTextColor.GREEN
            ));
        });
    }
}