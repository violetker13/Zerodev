package org.example.api.blockbench.player;

/** Один ключевой кадр — время + вращение (Эйлер XYZ в градусах). */
public record Keyframe(float time, float rx, float ry, float rz) {
    public Quat toQuat() { return Quat.fromEulerDeg(rx, ry, rz); }
}