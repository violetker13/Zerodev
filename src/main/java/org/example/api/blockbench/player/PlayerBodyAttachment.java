package org.example.api.blockbench.player;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.timer.TaskSchedule;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerBodyAttachment {

    private static final ConcurrentHashMap<UUID, PlayerBodyAttachment> active = new ConcurrentHashMap<>();

    private final Player         player;
    private final PlayerBodyModel body;
    private       float          lastYaw = Float.NaN;

    private PlayerBodyAttachment(Player player, PlayerBodyModel body) {
        this.player = player;
        this.body   = body;
    }

    // ── Публичный API ─────────────────────────────────────────────────────────

    public static void attach(Player player, PlayerBodyModel body) {
        detach(player);
        player.setInvisible(true);
        body.spawn(player.getInstance(), player.getPosition());

        PlayerBodyAttachment attachment = new PlayerBodyAttachment(player, body);
        active.put(player.getUuid(), attachment);
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            if (!player.isOnline() || !active.containsKey(player.getUuid())) {
                attachment.cleanup();
                return;
            }
            attachment.tick();
        }).repeat(TaskSchedule.tick(1)).schedule();
    }

    public static void detach(Player player) {
        PlayerBodyAttachment attachment = active.remove(player.getUuid());
        if (attachment != null) {
            attachment.cleanup();
        }
    }

    // ── Внутренняя логика ─────────────────────────────────────────────────────

    private void tick() {
        Pos pos = player.getPosition();
        float yaw = pos.yaw();
        body.teleport(pos);

        // Обновляем поворот только если yaw изменился
        if (yaw != lastYaw) {
            lastYaw = yaw;
            applyRotation(yaw);
        }
    }

    private void applyRotation(float yaw) {
        // Конвертируем yaw (градусы) в кватернион для DisplayMeta
        // Minecraft yaw: 0=юг, 90=запад, 180=север, -90=восток
        double rad = Math.toRadians(-yaw); // минус — разворот в правую систему координат
        float sin = (float) Math.sin(rad / 2);
        float cos = (float) Math.cos(rad / 2);

        // Кватернион поворота вокруг оси Y: (0, sin, 0, cos)
        for (HeadPartModel part : body.getParts()) {
            ItemDisplayMeta meta = (ItemDisplayMeta) part.getEntity().getEntityMeta();
            meta.setLeftRotation(new float[]{0f, sin, 0f, cos});
        }
    }

    private void cleanup() {
        body.remove();
        if (player.isOnline()) {
            player.setInvisible(false);
        }
    }
}