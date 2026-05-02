package org.example.api.blockbench.player;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Собирает тело игрока из 19 {@link HeadPartModel}.
 *
 * Использование:
 * <pre>
 *   PlayerBodyModel body = new PlayerBodyModel();
 *   body.spawn(instance, playerPos);   // спавним тело на позиции игрока
 *   // ... позже:
 *   body.remove();                     // убираем всё тело
 * </pre>
 *
 * Позиция спавна — точка у ног (Y=0 в системе координат BodyLayout).
 * Всё тело поднимается на 1.75 блока над этой точкой.
 */
public class PlayerBodyModel {

    private final List<HeadPartModel> parts = new ArrayList<>();
    private boolean spawned = false;

    public PlayerBodyModel() {
        // Создаём 19 HeadPartModel по описанию из BodyLayout
        // origin — заглушка (Pos.ZERO), реальная позиция задаётся в spawn()
        for (BodyPartEntry entry : BodyLayout.LAYOUT) {
            parts.add(new HeadPartModel(entry, Pos.ZERO));
        }
    }

    // ── Жизненный цикл ───────────────────────────────────────────────────────

    /**
     * Спавним все 19 ItemDisplay в мире.
     *
     * @param instance  мир (instance)
     * @param origin    позиция у ног (например, pos игрока)
     */
    public void spawn(Instance instance, Pos origin) {
        if (spawned) return;
        spawned = true;
        for (HeadPartModel part : parts) {
            part.spawn(instance, origin);
        }
    }

    /**
     * Убираем всё тело (все 19 entity).
     */
    public void remove() {
        if (!spawned) return;
        spawned = false;
        for (HeadPartModel part : parts) {
            part.remove();
        }
    }

    /**
     * Телепортируем тело на новую позицию.
     * Minestom двигает entity + их трансформация (Translation) сохраняется.
     */
    public void teleport(Pos newOrigin) {
        for (HeadPartModel part : parts) {
            part.getEntity().teleport(newOrigin);
        }
    }

    // ── Доступ к частям ──────────────────────────────────────────────────────

    /** Все 19 кубов. */
    public List<HeadPartModel> getParts() {
        return Collections.unmodifiableList(parts);
    }

    /** Кубы конкретной части тела (например, только TORSO — 6 штук). */
    public List<HeadPartModel> getParts(Part part) {
        return parts.stream()
                .filter(p -> p.getPart() == part)
                .toList();
    }

    /** Все Entity (удобно для пакетных операций). */
    public List<Entity> getEntities() {
        return parts.stream()
                .map(HeadPartModel::getEntity)
                .toList();
    }

    public boolean isSpawned() {
        return spawned;
    }
}