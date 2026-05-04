package org.example.comands.player;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.example.api.character.Animation;
import org.example.api.character.AnimationLoader;
import org.example.api.character.Characters;
import org.example.api.character.PlayerBodyModel;
import org.example.api.zero_command.ZeroCommand;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class newbody extends Command implements ZeroCommand {

    public enum BodyActions { ON, OFF }

    // Замени на свою текстуру
    private static final String TEXTURE =
            "eyJ0aW1lc3RhbXAiOjE2MjI1NTA1NDU3NzksInByb2ZpbGVJZCI6ImU2MWI0" +
                    "MmZjOTAzYTQ0MThhMTdhMTNkMDNmZjcxMmYiLCJwcm9maWxlTmFtZSI6Ik5v" +
                    "dGNoIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNL" +
                    "SU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4" +
                    "dHVyZS8yOTIwMDlhNDkyNWI1OGYwMmM3N2RhZGMyM2YwNjlhYTZiZWYzNTMz" +
                    "OTg3YzFlMzM1N2FkM2E0YzA4NzgzIn19fQ==";
    private static final Map<UUID, PlayerBodyModel>                      bodies     = new HashMap<>();
    private static final Map<UUID, Task>                                 tickTasks  = new HashMap<>();
    private static final Map<UUID, EventListener<PlayerMoveEvent>>       moveList   = new HashMap<>();
    private static final Map<UUID, EventListener<PlayerDisconnectEvent>> discList   = new HashMap<>();
    private static final Animation WALK_ANIM = loadAnim("animations/walk.json");
    private static final Animation IDLE_ANIM = loadAnim("animations/idle.json");

    private static Animation loadAnim(String path) {
        try { return AnimationLoader.load(path); }
        catch (IOException e) {
            System.err.println("[newbody] Не удалось загрузить " + path);
            return null;
        }
    }

    public newbody() {
        super("newbody");
        setUsage("/newbody <on|off>");

        ArgumentEnum<@NotNull BodyActions> action =
                ArgumentType.Enum("action", BodyActions.class)
                        .setFormat(ArgumentEnum.Format.LOWER_CASED);

        addPlayerSyntax((player, context) -> {
            switch (context.get(action)) {
                case ON  -> activate(player);
                case OFF -> deactivate(player);
            }
        }, action);
    }
    private void activate(@NotNull Player player) {
        UUID uuid = player.getUuid();
        if (bodies.containsKey(uuid)) { sendError(player, "newbody уже активен!"); return; }// 1. Делаем игрока невидимы
        PlayerBodyModel body = new PlayerBodyModel(Characters.SAITAMA, TEXTURE);
        body.spawn(player.getInstance(), player.getPosition(),player);
        bodies.put(uuid, body);
        player.getPlayerMeta().setInvisible(true);

        // Запускаем idle если есть
        if (IDLE_ANIM != null) body.getAnimationPlayer().play(IDLE_ANIM);

        // 3. Тик каждые 50 мс — обновляет ориентацию + анимацию
        Task task = MinecraftServer.getSchedulerManager()
                .buildTask(() -> {
                    if (!player.isOnline()) { deactivate(player); return; }
                    PlayerBodyModel b = bodies.get(uuid);
                    if (b != null) b.tick(0.05f, player.getPosition());
                })
                .repeat(TaskSchedule.tick(1))
                .schedule();
        tickTasks.put(uuid, task);

        // 4. PlayerMoveEvent — для смены анимаций walk/idle
        EventListener<PlayerMoveEvent> ml = EventListener.builder(PlayerMoveEvent.class)
                .handler(event -> {
                    PlayerBodyModel b = bodies.get(uuid);
                    if (b == null) return;

                    boolean isWalking = isMovingHorizontally(event);
                    Animation target  = isWalking ? WALK_ANIM : IDLE_ANIM;

                    if (target != null && b.getAnimationPlayer().getCurrent() != target) {
                        b.getAnimationPlayer().play(target, 0.15f);
                    }
                })
                .build();

        // 5. Авто-удаление при дисконнекте
        EventListener<PlayerDisconnectEvent> dl = EventListener.builder(PlayerDisconnectEvent.class)
                .handler(e -> deactivate(player))
                .build();

        player.eventNode().addListener(ml);
        player.eventNode().addListener(dl);
        moveList.put(uuid, ml);
        discList.put(uuid, dl);

        player.sendMessage("§aNewBody включён!");
    }

    // ── Деактивация ──────────────────────────────────────────────────────────

    private void deactivate(Player player) {
        UUID uuid = player.getUuid();
        player.getPlayerMeta().setInvisible(false);

        PlayerBodyModel body = bodies.remove(uuid);
        if (body == null) { sendError(player, "Newbody не активен!"); return; }

        // Удаляем тело
        body.remove();

        // Снимаем невидимость

        // Останавливаем тик
        Task t = tickTasks.remove(uuid);
        if (t != null) t.cancel();

        // Убираем слушатели
        EventListener<PlayerMoveEvent> ml = moveList.remove(uuid);
        EventListener<PlayerDisconnectEvent> dl = discList.remove(uuid);
        if (ml != null) player.eventNode().removeListener(ml);
        if (dl != null) player.eventNode().removeListener(dl);

        player.sendMessage("§cNewBody выключен!");
    }

    // ── Утилиты ──────────────────────────────────────────────────────────────

    private boolean isMovingHorizontally(PlayerMoveEvent e) {
        double dx = e.getNewPosition().x() - e.getPlayer().getPosition().x();
        double dz = e.getNewPosition().z() - e.getPlayer().getPosition().z();
        return (dx*dx + dz*dz) > 1e-6;
    }

    @Override public Command getCommand() { return this; }
}