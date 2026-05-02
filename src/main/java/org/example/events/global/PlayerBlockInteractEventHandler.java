package org.example.events.global;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.InstanceContainer;
import org.example.comands.worldedit.WorldEdit;
import org.example.events.EventHandler;

import static org.example.comands.worldedit.WandCommand.isHoldingWand;

public class PlayerBlockInteractEventHandler extends EventHandler {
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(PlayerBlockInteractEvent.class, event -> {
            if (!event.getHand().equals(PlayerHand.MAIN)) return;
            Player player = event.getPlayer();
            if (!isHoldingWand(player)) return;
            event.setCancelled(true);
            var pos = event.getBlockPosition();
            WorldEdit.getSelection(player).setPos2(pos, (InstanceContainer) player.getInstance());
            player.sendMessage(Component.text(
                    "Pos2: " + pos.blockX() + " " + pos.blockY() + " " + pos.blockZ(),
                    NamedTextColor.RED
            ));
        });
    }
}