package org.example.api.character.config;

/**
 * Описание одной кости скелета.
 *
 * @param name   уникальное имя кости
 * @param parent имя родителя, null — корневая кость
 * @param px     локальный пивот X относительно родителя
 * @param py     локальный пивот Y
 * @param pz     локальный пивот Z
 */

public record BoneConfig(
        String name,
        String parent,
        float px, float py, float pz
) {
    /** Корневая кость без родителя. */
    public static BoneConfig root(String name, float px, float py, float pz) {
        return new BoneConfig(name, null, px, py, pz);
    }

    public static BoneConfig child(String name, String parent, float px, float py, float pz) {
        return new BoneConfig(name, parent, px, py, pz);
    }
}