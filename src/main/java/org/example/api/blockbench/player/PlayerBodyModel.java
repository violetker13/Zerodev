package org.example.api.blockbench.player;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Анимированная модель тела игрока (19 кубиков + скелет + AnimationPlayer).
 *
 * ── Быстрый старт ────────────────────────────────────────────────────────────
 *
 *   // Создать и заспавнить
 *   PlayerBodyModel body = new PlayerBodyModel(TEXTURE_VALUE);
 *   body.spawn(instance, player.getPosition());
 *   body.startAutoTick();           // авто-тик каждые 50 мс
 *
 *   // Загрузить и запустить анимацию
 *   Animation walk = AnimationLoader.load("animations/walk.json");
 *   body.getAnimationPlayer().play(walk);
 *
 *   // Переключить анимацию с плавным переходом 0.2 сек
 *   body.getAnimationPlayer().play(idle, 0.2f);
 *
 *   // Телепортировать тело
 *   body.teleport(newPos);
 *
 *   // Удалить
 *   body.remove();
 *
 * ── Кости ────────────────────────────────────────────────────────────────────
 *   root, torso, head, right_arm, left_arm, right_leg, left_leg
 */
public class PlayerBodyModel {

    // Размеры кубов (масштаб ItemDisplay)
    private static final float CUBE  = 0.5f;
    private static final float HEAD  = 1.0f;

    private final PlayerSkeleton   skeleton;
    private final AnimationPlayer  animPlayer;
    private final List<BodyPart>   parts = new ArrayList<>();

    private float[] rootPos = {0, 0, 0};
    private boolean spawned = false;

    // Планировщик-тик (опционально)
    private net.minestom.server.timer.Task autoTickTask;

    // ── Конструктор ──────────────────────────────────────────────────────────

    public PlayerBodyModel(String textureValue) {
        skeleton  = new PlayerSkeleton();
        animPlayer = new AnimationPlayer(skeleton);
        buildParts(textureValue);
    }

    // ── Жизненный цикл ───────────────────────────────────────────────────────

    public void spawn(Instance instance, Pos origin) {
        if (spawned) return;
        spawned = true;
        rootPos = toArr(origin);
        for (BodyPart p : parts) p.spawn(instance, origin);
        refresh();
    }

    /**
     * Обновить анимацию и трансформы вручную.
     * Вызывать каждый тик (50 мс = 0.05 сек).
     * @param deltaSeconds  секунды с прошлого вызова
     * @param origin        текущая мировая позиция модели
     */
    public void tick(float deltaSeconds, Pos origin) {
        if (!spawned) return;

        float[] newPos = toArr(origin);
        boolean moved  = !arr3eq(newPos, rootPos);
        rootPos        = newPos;

        if (moved) {
            // Телепортируем все entity на новую позицию
            for (BodyPart p : parts) p.getEntity().teleport(origin);
        }

        animPlayer.tick(deltaSeconds);
        refresh();
    }

    /**
     * Запустить авто-тик через планировщик Minestom (каждые 50 мс).
     * После вызова не нужно вызывать tick() вручную.
     * Позиция модели не обновляется автоматически — вызывай teleport() снаружи.
     */
    public void startAutoTick() {
        stopAutoTick();
        autoTickTask = MinecraftServer.getSchedulerManager()
                .buildTask(() -> {
                    if (!spawned) { stopAutoTick(); return; }
                    animPlayer.tick(0.05f);
                    refresh();
                })
                .repeat(TaskSchedule.tick(1))
                .schedule();
    }

    public void stopAutoTick() {
        if (autoTickTask != null) { autoTickTask.cancel(); autoTickTask = null; }
    }

    /** Телепортировать модель на новую позицию без анимации */
    public void teleport(Pos origin) {
        rootPos = toArr(origin);
        for (BodyPart p : parts) p.getEntity().teleport(origin);
        refresh();
    }

    public void remove() {
        if (!spawned) return;
        spawned = false;
        stopAutoTick();
        animPlayer.stop();
        parts.forEach(BodyPart::remove);
    }

    // ── Внутреннее обновление ────────────────────────────────────────────────

    private void refresh() {
        skeleton.update(rootPos);
        for (BodyPart p : parts) p.updateTransform(rootPos);
    }

    // ── Построение частей тела ───────────────────────────────────────────────
    //
    // localOffset — центр куба от пивота кости в bind-позе кости.
    // Вычислено из BodyLayout: world_pos - bone_world_pivot.

    private void buildParts(String tex) {
        BoneNode t  = skeleton.torso;
        BoneNode h  = skeleton.head;
        BoneNode ra = skeleton.rightArm;
        BoneNode la = skeleton.leftArm;
        BoneNode rl = skeleton.rightLeg;
        BoneNode ll = skeleton.leftLeg;

        // ГОЛОВА (1 куб, масштаб HEAD)
        add(h,   0,      0.35f, 0, HEAD, tex);

        // ТОРС (6 кубов, 2×3, масштаб CUBE)
        add(t,  -0.125f, 0.625f, 0, CUBE, tex);   // body_1
        add(t,   0.125f, 0.625f, 0, CUBE, tex);   // body_2
        add(t,  -0.125f, 0.375f, 0, CUBE, tex);   // body_3
        add(t,   0.125f, 0.375f, 0, CUBE, tex);   // body_4
        add(t,  -0.125f, 0.125f, 0, CUBE, tex);   // body_5
        add(t,   0.125f, 0.125f, 0, CUBE, tex);   // body_6

        // ПРАВАЯ РУКА (3 куба)
        add(ra,  0, -0.125f, 0, CUBE, tex);
        add(ra,  0, -0.375f, 0, CUBE, tex);
        add(ra,  0, -0.625f, 0, CUBE, tex);

        // ЛЕВАЯ РУКА (3 куба)
        add(la,  0, -0.125f, 0, CUBE, tex);
        add(la,  0, -0.375f, 0, CUBE, tex);
        add(la,  0, -0.625f, 0, CUBE, tex);

        // ПРАВАЯ НОГА (3 куба)
        add(rl,  0, -0.125f, 0, CUBE, tex);
        add(rl,  0, -0.375f, 0, CUBE, tex);
        add(rl,  0, -0.625f, 0, CUBE, tex);

        // ЛЕВАЯ НОГА (3 куба)
        add(ll,  0, -0.125f, 0, CUBE, tex);
        add(ll,  0, -0.375f, 0, CUBE, tex);
        add(ll,  0, -0.625f, 0, CUBE, tex);
    }

    private void add(BoneNode bone, float ox, float oy, float oz, float scale, String tex) {
        parts.add(new BodyPart(bone, ox, oy, oz, scale, tex));
    }

    // ── Геттеры ──────────────────────────────────────────────────────────────

    public AnimationPlayer  getAnimationPlayer() { return animPlayer; }
    public PlayerSkeleton   getSkeleton()        { return skeleton;   }
    public boolean          isSpawned()          { return spawned;    }

    public List<Entity> getEntities() {
        return parts.stream().map(BodyPart::getEntity).toList();
    }

    // ── Утилиты ──────────────────────────────────────────────────────────────

    private static float[] toArr(Pos p) {
        return new float[]{(float) p.x(), (float) p.y(), (float) p.z()};
    }

    private static boolean arr3eq(float[] a, float[] b) {
        return a[0]==b[0] && a[1]==b[1] && a[2]==b[2];
    }
}