package org.example.api.cursorapi;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.example.Main;

import java.awt.*;

public class Cursor {
    private final Player player;
    private Entity cursorEntity;
    private EventNode<PlayerEvent> playerNode;
    private GameMode originalGameMode;
    private boolean active = false;

    public Cursor(Player player) {
        this.player = player;
        this.originalGameMode = player.getGameMode();
    }

    public Cursor summon() {
        if (active) return this;
        player.setGameMode(GameMode.SPECTATOR);
        cursorEntity = new Entity(EntityType.ITEM_DISPLAY);
        cursorEntity.setInstance(player.getInstance(), player.getPosition().add(0,1.5,0));

        cursorEntity.setNoGravity(true);
        cursorEntity.addPassenger(player);
        player.spectate(cursorEntity);

        // Нода привязанная только к этому игроку
        playerNode = EventNode.type("cursor-node-" + player.getUuid(), EventFilter.PLAYER, (event, p) -> p.equals(player));
        playerNode.addListener(PlayerMoveEvent.class, this::onMove);
        Main.node.addChild(playerNode);

        active = true;
        return this;
    }

    public void remove() {
        if (!active) return;
        player.setGameMode(originalGameMode);

        player.stopSpectating();
        if (cursorEntity != null) {
            cursorEntity.remove();
            cursorEntity = null;
        }
        if (playerNode != null) {
            Main.node.removeChild(playerNode);
            playerNode = null;
        }

        active = false;
    }

    private void onMove(PlayerMoveEvent event) {
        if (cursorEntity == null) return;
        player.sendActionBar(Component.text(event.getNewPosition().toString()));

    }

    public boolean isActive() { return active; }
    public Entity getCursorEntity() { return cursorEntity; }
    public Player getPlayer() { return player; }

    public static Cursor of(Player player) {
        return new Cursor(player).summon();
    }
}