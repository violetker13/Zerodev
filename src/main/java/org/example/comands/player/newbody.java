package org.example.comands.player;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import org.example.api.blockbench.player.Animation;
import org.example.api.blockbench.player.AnimationLoader;
import org.example.api.blockbench.player.PlayerBodyModel;
import org.example.api.zero_command.ZeroCommand;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class newbody extends Command implements ZeroCommand {

    public enum BodyActions { ON, OFF }

    // Текстура скина (замени на свою)
    private static final String TEXTURE =
            "eyJ0aW1lc3RhbXAiOjE2MjI1NTA1NDU3NzksInByb2ZpbGVJZCI6ImU2MWI0" +
                    "MmZjOTAzYTQ0MThhMTdhMTNkMDNmZjcxMmYiLCJwcm9maWxlTmFtZSI6Ik5v" +
                    "dGNoIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNL" +
                    "SU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4" +
                    "dHVyZS8yOTIwMDlhNDkyNWI1OGYwMmM3N2RhZGMyM2YwNjlhYTZiZWYzNTMz" +
                    "OTg3YzFlMzM1N2FkM2E0YzA4NzgzIn19fQ==";

    // uuid → модель тела
    private static final Map<UUID, PlayerBodyModel> bodies = new HashMap<>();
    // uuid → слушатели событий (для корректного удаления)
    private static final Map<UUID, EventListener<PlayerMoveEvent>>       moveListeners = new HashMap<>();
    private static final Map<UUID, EventListener<PlayerDisconnectEvent>> discListeners = new HashMap<>();

    // Кэш анимаций (загружаем один раз)
    private static Animation walkAnim;
    private static Animation idleAnim;

    static {
        try {
            walkAnim = AnimationLoader.load("animations/walk.json");
        } catch (IOException e) {
            System.err.println("[newbody] Не удалось загрузить walk.json: " + e.getMessage());
        }
        try {
            idleAnim = AnimationLoader.load("animations/idle.json");
        } catch (IOException e) {
            System.err.println("[newbody] Не удалось загрузить idle.json (необязательно)");
        }
    }

    public newbody() {
        super("newbody");
        setUsage("/newbody <on|off>");

        ArgumentEnum<@NotNull BodyActions> action = ArgumentType.Enum("action", BodyActions.class)
                .setFormat(ArgumentEnum.Format.LOWER_CASED);

        addPlayerSyntax((player, context) -> {
            switch (context.get(action)) {
                case ON  -> activate(player);
                case OFF -> deactivate(player);
            }
        }, action);
    }

    // ── Активация ────────────────────────────────────────────────────────────

    private void activate(Player player) {
        if (bodies.containsKey(player.getUuid())) {
            sendError(player, "newbody уже активен!");
            return;
        }

        // Создаём модель
        PlayerBodyModel body = new PlayerBodyModel(TEXTURE);
        body.spawn(player.getInstance(), player.getPosition());
        body.startAutoTick();

        // Запускаем idle-анимацию если есть
        if (idleAnim != null) {
            body.getAnimationPlayer().play(idleAnim);
        }

        bodies.put(player.getUuid(), body);

        // ── Движение: двигаем тело + меняем анимацию ────────────────────────
        EventListener<PlayerMoveEvent> moveListener = EventListener
                .builder(PlayerMoveEvent.class)
                .handler(event -> {
                    PlayerBodyModel b = bodies.get(player.getUuid());
                    if (b == null) return;

                    b.teleport(event.getNewPosition());

                    // Смена анимации: идём/стоим
                    boolean moving = isMoving(event);
                    Animation target = moving ? walkAnim : idleAnim;
                    if (target != null) {
                        Animation current = b.getAnimationPlayer().getCurrent();
                        if (current != target) {
                            b.getAnimationPlayer().play(target, 0.15f); // плавный переход 0.15 сек
                        }
                    }
                })
                .build();

        // ── Отключение: убираем тело автоматически ──────────────────────────
        EventListener<PlayerDisconnectEvent> discListener = EventListener
                .builder(PlayerDisconnectEvent.class)
                .handler(event -> deactivate(player))
                .build();

        player.eventNode().addListener(moveListener);
        player.eventNode().addListener(discListener);

        moveListeners.put(player.getUuid(), moveListener);
        discListeners.put(player.getUuid(), discListener);

        player.sendMessage("§aNewBody включён!");
    }

    // ── Деактивация ──────────────────────────────────────────────────────────

    private void deactivate(Player player) {
        UUID uuid = player.getUuid();

        PlayerBodyModel body = bodies.remove(uuid);
        if (body == null) {
            sendError(player, "newbody не активен!");
            return;
        }
        body.remove();

        // Убираем слушатели
        EventListener<PlayerMoveEvent> ml = moveListeners.remove(uuid);
        EventListener<PlayerDisconnectEvent> dl = discListeners.remove(uuid);
        if (ml != null) player.eventNode().removeListener(ml);
        if (dl != null) player.eventNode().removeListener(dl);

        player.sendMessage("§cNewBody выключен!");
    }

    // ── Утилиты ──────────────────────────────────────────────────────────────

    /** Игрок двигается горизонтально? */
    private boolean isMoving(PlayerMoveEvent event) {
        var np = event.getNewPosition();
        var op = event.getPlayer().getPosition();
        double dx = np.x() - op.x();
        double dz = np.z() - op.z();
        return dx*dx + dz*dz > 1e-6;
    }

    @Override
    public Command getCommand() { return this; }
}