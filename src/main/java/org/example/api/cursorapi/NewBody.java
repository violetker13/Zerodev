package org.example.api.cursorapi;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;

public class NewBody {
    private final Player player;
    private Entity cameraEntity;
    private GameMode originalGameMode;
    private boolean active = false;
    private EventListener<PlayerMoveEvent> moveListener;
    private EventListener<PlayerDisconnectEvent> disconnectListener;
    private Pos lastPlayerPos;

    public NewBody(Player player) {
        this.player = player;
        this.originalGameMode = player.getGameMode();
    }

    public NewBody summon() {
        if (active) return this;

        originalGameMode = player.getGameMode();
        player.setGameMode(GameMode.SPECTATOR);
        player.addEffect(new Potion(PotionEffect.INVISIBILITY, 2, -1));
        lastPlayerPos = player.getPosition();
        Pos spawnPos = player.getPosition().add(0, 1.6, 0);
        cameraEntity = new Entity(EntityType.TEXT_DISPLAY);
        TextDisplayMeta meta = (TextDisplayMeta) cameraEntity.getEntityMeta();
        meta.setText(Component.text("•"));
        meta.setBillboardRenderConstraints(TextDisplayMeta.BillboardConstraints.CENTER);
        meta.setPosRotInterpolationDuration(0);
        meta.setSeeThrough(true);
        meta.setBackgroundColor(0);

        cameraEntity.setNoGravity(true);
        cameraEntity.setInstance(player.getInstance(), spawnPos);
        cameraEntity.addPassenger(player);
        player.spectate(cameraEntity);

        moveListener = EventListener.builder(PlayerMoveEvent.class)
                .handler(this::onMove)
                .build();

        disconnectListener = EventListener.builder(PlayerDisconnectEvent.class)
                .handler(event -> remove())
                .build();

        player.eventNode().addListener(moveListener);
        player.eventNode().addListener(disconnectListener);

        active = true;
        return this;
    }

    public void remove() {
        if (!active) return;

        if (moveListener != null) player.eventNode().removeListener(moveListener);
        if (disconnectListener != null) player.eventNode().removeListener(disconnectListener);

        if (player.isOnline()) {
            player.stopSpectating();
            player.setGameMode(originalGameMode);
            if (cameraEntity != null) {
                player.teleport(cameraEntity.getPosition().withYaw(player.getPosition().yaw()).withPitch(player.getPosition().pitch()));
            }
        }

        if (cameraEntity != null) {
            cameraEntity.remove();
            cameraEntity = null;
        }

        active = false;
    }

    private void onMove(PlayerMoveEvent event) {
        if (cameraEntity == null) return;

        Pos newPlayerPos = event.getNewPosition();
        Pos oldPlayerPos = lastPlayerPos;

        // Берём только горизонтальную дельту — Y от спектатора игнорируем
        double dx = newPlayerPos.x() - oldPlayerPos.x();
        double dz = newPlayerPos.z() - oldPlayerPos.z();

        lastPlayerPos = newPlayerPos;

        Pos currentBodyPos = cameraEntity.getPosition();
        Pos newBodyPos = new Pos(
                currentBodyPos.x() + dx,
                currentBodyPos.y(),          // Y тела не меняем
                currentBodyPos.z() + dz,
                newPlayerPos.yaw(),           // поворот синхронизируем
                newPlayerPos.pitch()
        );

        cameraEntity.teleport(newBodyPos);
    }

    public boolean isActive() { return active; }
    public Player getPlayer() { return player; }
    public Entity getCameraEntity() { return cameraEntity; }

    public static NewBody of(Player player) {
        return new NewBody(player).summon();
    }
}