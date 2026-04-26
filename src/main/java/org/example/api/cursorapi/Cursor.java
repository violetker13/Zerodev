package org.example.api.cursorapi;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.example.Main;

public class Cursor {
    private final Player player;
    private Entity cameraEntity;  // заморозка камеры
    private Entity dotEntity;     // точка курсора
    private EventNode<PlayerEvent> playerNode;
    private GameMode originalGameMode;
    private boolean active = false;

    // Центральная точка (куда смотрел игрок при активации)
    private float centerYaw;
    private float centerPitch;

    public Cursor(Player player) {
        this.player = player;
        this.originalGameMode = player.getGameMode();
    }

    public Cursor summon() {
        if (active) return this;

        player.setGameMode(GameMode.SPECTATOR);

        // Запоминаем центр — текущий взгляд игрока
        centerYaw = player.getPosition().yaw();
        centerPitch = player.getPosition().pitch();

        Pos spawnPos = player.getPosition().add(0, 1.6, 0);

        // 1. Сущность для заморозки камеры
        cameraEntity = new Entity(EntityType.ITEM_DISPLAY);
        cameraEntity.setNoGravity(true);
        cameraEntity.setInstance(player.getInstance(), spawnPos);
        cameraEntity.addPassenger(player);
        player.spectate(cameraEntity); // камера заморожена

        // 2. Точка курсора TEXT_DISPLAY
        dotEntity = new Entity(EntityType.TEXT_DISPLAY);
        dotEntity.setNoGravity(true);

        TextDisplayMeta meta = (TextDisplayMeta) dotEntity.getEntityMeta();
        meta.setText(Component.text("•"));
        meta.setBillboardRenderConstraints(TextDisplayMeta.BillboardConstraints.CENTER);
        meta.setPosRotInterpolationDuration(1);
        meta.setSeeThrough(true);
        meta.setBackgroundColor(0);
        dotEntity.setInstance(player.getInstance(), calcDotPos(centerYaw, centerPitch));

        // Нода только для этого игрока
        playerNode = EventNode.type(
                "cursor-node-" + player.getUuid(),
                EventFilter.PLAYER,
                (event, p) -> p.equals(player)
        );

        playerNode.addListener(PlayerMoveEvent.class, this::onMove);
        Main.node.addChild(playerNode);

        active = true;
        return this;
    }

    public void remove() {
        if (!active) return;

        player.stopSpectating();
        player.setGameMode(originalGameMode);

        if (cameraEntity != null) {
            cameraEntity.remove();
            cameraEntity = null;
        }

        if (dotEntity != null) {
            dotEntity.remove();
            dotEntity = null;
        }

        if (playerNode != null) {
            Main.node.removeChild(playerNode);
            playerNode = null;
        }

        active = false;
    }

    private void onMove(PlayerMoveEvent event) {
        if (dotEntity == null) return;

        // Получаем отклонение от центра
        float yaw = event.getNewPosition().yaw();
        float pitch = event.getNewPosition().pitch();

        dotEntity.teleport(calcDotPos(yaw, pitch));
    }

    private Pos calcDotPos(float yaw, float pitch) {
        double maxX = 3.4;
        double maxY = 2;



        // Нормализуем deltaYaw чтобы не было wrap-around (-180 до 180)
        double deltaYaw = yaw - centerYaw;
        while (deltaYaw > 180) deltaYaw -= 360;
        while (deltaYaw < -180) deltaYaw += 360;

        double deltaPitch = pitch - centerPitch;

        // Линейное смещение (без синуса — нет реверса)
        double offsetRight = -(deltaYaw / 90.0) * maxX;
        double offsetUp = -(deltaPitch / 90.0) * maxY;

        // Ограничение
        offsetRight = Math.max(-maxX, Math.min(maxX, offsetRight));
        offsetUp = Math.max(-maxY, Math.min(maxY, offsetUp));

        double yawRad = Math.toRadians(centerYaw);

        // Вектор ВПЕРЁД (направление взгляда)
        double fwdX = -Math.sin(yawRad);
        double fwdZ = Math.cos(yawRad);

        // Вектор ВПРАВО (перпендикулярно взгляду в горизонтальной плоскости)
        double rightX = Math.cos(yawRad);
        double rightZ = Math.sin(yawRad);

        double distance = 1.5;
        Pos playerPos = player.getPosition();

        // Центр — точка перед игроком
        double centerX = playerPos.x() + fwdX * distance;
        double centerY = playerPos.y();
        double centerZ = playerPos.z() + fwdZ * distance;

        // Смещаем вправо/влево по вектору RIGHT и вверх/вниз по Y
        return new Pos(
                centerX + rightX * offsetRight,
                centerY + offsetUp,
                centerZ + rightZ * offsetRight
        );
    }

    public boolean isActive() { return active; }
    public Entity getDotEntity() { return dotEntity; }
    public Player getPlayer() { return player; }

    public static Cursor of(Player player) {
        return new Cursor(player).summon();
    }
}