package org.example.api.blockbench.player;

import java.util.List;

/** Трек ключевых кадров для одной кости. */
public record BoneTrack(List<Keyframe> keyframes) {

    /** Интерполировать вращение в момент времени t */
    public Quat sample(float t) {
        if (keyframes.isEmpty()) return Quat.IDENTITY;
        if (keyframes.size() == 1) return keyframes.get(0).toQuat();

        Keyframe prev = keyframes.get(0);
        for (int i = 1; i < keyframes.size(); i++) {
            Keyframe next = keyframes.get(i);
            if (t <= next.time()) {
                float span = next.time() - prev.time();
                float alpha = span < 1e-5f ? 0f : (t - prev.time()) / span;
                return Quat.slerp(prev.toQuat(), next.toQuat(), alpha);
            }
            prev = next;
        }
        return keyframes.get(keyframes.size() - 1).toQuat();
    }
}