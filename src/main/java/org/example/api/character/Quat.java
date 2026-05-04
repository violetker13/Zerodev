package org.example.api.character;

import java.util.Arrays;

/**
 * Иммутабельный кватернион для вращений костей.
 * Все публичные API принимают углы в градусах.
 */
public final class Quat {

    public static final Quat IDENTITY = new Quat(0, 0, 0, 1);

    public final float x, y, z, w;

    public Quat(float x, float y, float z, float w) {
        this.x = x; this.y = y; this.z = z; this.w = w;
    }
    /** Из углов Эйлера XYZ в градусах (порядок: pitch, yaw, roll) */
    public static Quat fromEulerDeg(float rx, float ry, float rz) {
        double cx = Math.cos(Math.toRadians(rx * 0.5));
        double sx = Math.sin(Math.toRadians(rx * 0.5));
        double cy = Math.cos(Math.toRadians(ry * 0.5));
        double sy = Math.sin(Math.toRadians(ry * 0.5));
        double cz = Math.cos(Math.toRadians(rz * 0.5));
        double sz = Math.sin(Math.toRadians(rz * 0.5));
        return new Quat(
                (float)(sx*cy*cz + cx*sy*sz),
                (float)(cx*sy*cz - sx*cy*sz),
                (float)(cx*cy*sz + sx*sy*cz),
                (float)(cx*cy*cz - sx*sy*sz)
        );
    }

    /** Композиция: this * other (сначала this, потом other) */
    public Quat mul(Quat o) {
        return new Quat(
                w*o.x + x*o.w + y*o.z - z*o.y,
                w*o.y - x*o.z + y*o.w + z*o.x,
                w*o.z + x*o.y - y*o.x + z*o.w,
                w*o.w - x*o.x - y*o.y - z*o.z
        );
    }

    /** Повернуть вектор этим кватернионом */
    public float[] rotate(float vx, float vy, float vz) {
        float tx = 2 * (y*vz - z*vy);
        float ty = 2 * (z*vx - x*vz);
        float tz = 2 * (x*vy - y*vx);
        return new float[]{
                vx + w*tx + y*tz - z*ty,
                vy + w*ty + z*tx - x*tz,
                vz + w*tz + x*ty - y*tx
        };
    }

    /** SLERP — сферическая линейная интерполяция */
    public static Quat slerp(Quat a, Quat b, float t) {
        float dot = a.x*b.x + a.y*b.y + a.z*b.z + a.w*b.w;
        float bx = b.x, by = b.y, bz = b.z, bw = b.w;
        if (dot < 0) { dot = -dot; bx=-bx; by=-by; bz=-bz; bw=-bw; }
        if (dot > 0.9995f) {
            return normalize(a.x + t*(bx-a.x), a.y + t*(by-a.y), a.z + t*(bz-a.z), a.w + t*(bw-a.w));
        }
        float theta0 = (float) Math.acos(dot);
        float sinT0  = (float) Math.sin(theta0);
        float thetaT = theta0 * t;
        float s0 = (float) Math.cos(thetaT) - dot * (float) Math.sin(thetaT) / sinT0;
        float s1 = (float) Math.sin(thetaT) / sinT0;
        return new Quat(s0*a.x + s1*bx, s0*a.y + s1*by, s0*a.z + s1*bz, s0*a.w + s1*bw);
    }

    private static Quat normalize(float x, float y, float z, float w) {
        float len = (float) Math.sqrt(x*x + y*y + z*z + w*w);
        return new Quat(x/len, y/len, z/len, w/len);
    }

    /** Для Minestom: float[]{x, y, z, w} */
    public float[] toArray() { return new float[]{x, y, z, w}; }
}