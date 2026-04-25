package org.example.events.handlers;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.example.events.EventHandler;

public class PlayerBlockBreakEventHandler extends EventHandler {
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(PlayerBlockBreakEvent.class, event -> {
            if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
            Block block = event.getBlock();
            Material material = Material.fromKey(block.key());
            if (material == null) return;
            ItemStack drop = ItemStack.of(material, 1);
            ItemEntity itemEntity = new ItemEntity(drop);
            Pos blockPos = new Pos(
                    event.getBlockPosition().x() + 0.5,
                    event.getBlockPosition().y() + 0.5,
                    event.getBlockPosition().z() + 0.5
            );
            itemEntity.setInstance(event.getPlayer().getInstance(), blockPos);
            itemEntity.setVelocity(new Vec(0, 4, 0));
        });
    }
}