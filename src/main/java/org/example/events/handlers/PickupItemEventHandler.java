package org.example.events.handlers;

import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import org.example.events.EventHandler;

public class PickupItemEventHandler extends EventHandler {
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(PickupItemEvent.class, event -> {
            if(event.getLivingEntity() instanceof Player) {
                Player player = (Player) event.getLivingEntity();
                ItemEntity itemEntity = event.getItemEntity();
                ItemStack itemStack = itemEntity.getItemStack();

                PlayerInventory playerInventory = player.getInventory();
                playerInventory.addItemStack(itemStack);
            }
        });
    }
}