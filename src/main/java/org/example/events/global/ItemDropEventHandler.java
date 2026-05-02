package org.example.events.global;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.time.TimeUnit;
import org.example.events.EventHandler;

public class ItemDropEventHandler extends EventHandler {
    @Override
    public void register(EventNode<Event> node, InstanceContainer instance) {
        node.addListener(ItemDropEvent.class, event -> {
            Player player = event.getPlayer();
            ItemStack stack = event.getItemStack();

            // Спавним предмет на позиции игрока
            ItemEntity itemEntity = new ItemEntity(stack);

            Pos spawnPos = new Pos(
                    player.getPosition().x(),
                    player.getPosition().y() + 1.0, // на уровне рук
                    player.getPosition().z()
            );

            itemEntity.setInstance(event.getInstance(), spawnPos);
            itemEntity.setPickupDelay(33, TimeUnit.SERVER_TICK); // задержка перед возможностью подобрать предмет
            // Бросаем в направлении взгляда игрока
            double yawRad = Math.toRadians(player.getPosition().yaw());
            double pitchRad = Math.toRadians(player.getPosition().pitch());

            double dx = -Math.sin(yawRad) * Math.cos(pitchRad);
            double dy = -Math.sin(pitchRad);
            double dz = Math.cos(yawRad) * Math.cos(pitchRad);

            double speed = 6.0;
            itemEntity.setVelocity(new Vec(dx * speed, dy * speed + 2, dz * speed));
        });
    }
}