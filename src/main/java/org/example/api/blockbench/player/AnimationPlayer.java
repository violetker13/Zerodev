package org.example.api.blockbench.player;

/**
 * Воспроизводит Animation на PlayerSkeleton.
 *
 * Вызывай tick() каждый серверный тик (50 мс → 0.05f секунды).
 */
public class AnimationPlayer {

    private final PlayerSkeleton skeleton;

    private Animation current;
    private float     time;
    private boolean   playing;

    // Плавное смешивание (blend): применяем постепенно за blendDuration секунд
    private float blendTime;
    private float blendDuration;
    private Animation blendFrom;
    private float blendFromTime;

    public AnimationPlayer(PlayerSkeleton skeleton) {
        this.skeleton = skeleton;
    }

    /** Начать воспроизведение с плавным переходом */
    public void play(Animation animation, float blendDurationSeconds) {
        if (playing && current != null) {
            blendFrom     = current;
            blendFromTime = time;
            blendTime     = 0;
            blendDuration = blendDurationSeconds;
        }
        current = animation;
        time    = 0;
        playing = true;
    }

    /** Начать воспроизведение без перехода */
    public void play(Animation animation) {
        play(animation, 0f);
    }

    public void stop() {
        playing   = false;
        current   = null;
        blendFrom = null;
        skeleton.resetAll();
    }

    /**
     * Продвинуть анимацию на deltaSeconds и применить к скелету.
     * @param deltaSeconds  обычно 0.05f (один серверный тик = 50 мс)
     */
    public void tick(float deltaSeconds) {
        if (!playing || current == null) return;

        time += deltaSeconds;
        if (current.loop()) {
            if (time > current.length()) time %= current.length();
        } else if (time >= current.length()) {
            time    = current.length();
            playing = false;
        }

        // Смешивание
        if (blendFrom != null) {
            blendTime += deltaSeconds;
            float alpha = blendDuration > 0f ? Math.min(blendTime / blendDuration, 1f) : 1f;
            applyBlended(alpha);
            if (alpha >= 1f) blendFrom = null;
        } else {
            apply(current, time);
        }
    }

    // ── применение ───────────────────────────────────────────────────────────

    private void apply(Animation anim, float t) {
        for (var entry : anim.tracks().entrySet()) {
            BoneNode bone = skeleton.getBone(entry.getKey());
            if (bone != null) bone.localRot = entry.getValue().sample(t);
        }
    }

    private void applyBlended(float alpha) {
        // Применяем текущую анимацию
        for (var entry : current.tracks().entrySet()) {
            BoneNode bone = skeleton.getBone(entry.getKey());
            if (bone == null) continue;

            Quat curr = entry.getValue().sample(time);

            // Если blendFrom имеет трек для этой кости — смешиваем
            BoneTrack fromTrack = blendFrom.getTrack(entry.getKey());
            if (fromTrack != null) {
                Quat prev = fromTrack.sample(blendFromTime);
                bone.localRot = Quat.slerp(prev, curr, alpha);
            } else {
                bone.localRot = Quat.slerp(Quat.IDENTITY, curr, alpha);
            }
        }
    }

    // ── геттеры ──────────────────────────────────────────────────────────────

    public boolean   isPlaying()  { return playing; }
    public float     getTime()    { return time; }
    public Animation getCurrent() { return current; }
}