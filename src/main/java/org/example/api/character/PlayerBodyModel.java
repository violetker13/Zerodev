package org.example.api.character;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;
import org.example.api.character.config.CharacterConfig;
import org.example.api.character.config.PartConfig;

import java.util.ArrayList;
import java.util.List;

public class PlayerBodyModel {

    private final CharacterConfig  config;
    private final PlayerSkeleton   skeleton;
    private final AnimationPlayer  animPlayer;
    private final List<BodyPart>   parts = new ArrayList<>();

    private float[] rootPos   = {0, 0, 0};
    private boolean spawned   = false;

    private net.minestom.server.timer.Task autoTickTask;

    public PlayerBodyModel(Characters character, String textureValue) {
        this(character.config(), textureValue);
    }

    public PlayerBodyModel(CharacterConfig config, String textureValue) {
        this.config    = config;
        this.skeleton  = new PlayerSkeleton(config);
        this.animPlayer = new AnimationPlayer(skeleton);
        buildParts();
    }

    public void spawn(Instance instance, Pos origin, Player player) {
        if (spawned) return;
        spawned = true;

        for (BodyPart p : parts) {
            p.spawn(instance, origin);
            player.addPassenger(p.getEntity());
        }

        rootPos = toArr(origin);
        refresh();
    }


    public void tick(float deltaSeconds, Pos playerPos) {
        if (!spawned) return;
        rootPos = toArr(playerPos);
        applyOrientation(playerPos.yaw(), playerPos.pitch());
        animPlayer.tick(deltaSeconds);
        refresh();
    }

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

    private void applyOrientation(float yaw, float pitch) {
        skeleton.getBone(config.orientationBone())
                .baseRot = Quat.fromEulerDeg(0, -yaw - 2, 0); // +2 подбери
        skeleton.getBone(config.headBone())
                .baseRot = Quat.fromEulerDeg(pitch, 0, 0);
    }

    private void refresh() {
        skeleton.update(rootPos);
        for (BodyPart p : parts) p.updateTransform(rootPos, config.spawnOffset());
    }

    private void buildParts() {  // textureValue больше не нужен
        config.boneCmds().forEach((boneName, cmd) -> {
            BoneNode bone = skeleton.getBone(boneName);
            parts.add(new BodyPart(bone, cmd));
        });
    }

    public AnimationPlayer getAnimationPlayer() { return animPlayer; }
    public PlayerSkeleton  getSkeleton()        { return skeleton;   }
    public boolean         isSpawned()          { return spawned;    }
    public CharacterConfig getConfig()          { return config;     }

    public List<Entity> getEntities() {
        return parts.stream().map(BodyPart::getEntity).toList();
    }

    private static float[] toArr(Pos p) {
        return new float[]{(float) p.x(), (float) p.y(), (float) p.z()};
    }

    private static boolean arr3eq(float[] a, float[] b) {
        return a[0] == b[0] && a[1] == b[1] && a[2] == b[2];
    }
}