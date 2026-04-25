package org.example.extras.actions;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import org.example.Main;


public class Test1 {
    public Test1(Player player){

        Entity ent1 = new Entity(EntityType.ITEM_DISPLAY);
        ent1.setNoGravity(true); // сущность не падает
        // Если первый инстанс есть — используем его, иначе используем текущее из Main.instance
        if (Main.getInstanceById(1) != null) {
            ent1.setInstance(Main.getInstanceById(1),player.getPosition());
        } else if (Main.instance != null) {
            ent1.setInstance(Main.instance, player.getPosition());
        }
        ent1.spawn();
        ent1.addPassenger(player);
        player.spectate(ent1);
        player.setNoGravity(true);
        player.setFlying(true);
        player.setAllowFlying(true);

    };
}
