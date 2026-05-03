package org.example.api.blockbench.player;

import java.util.Map;

/**
 * Загруженные данные анимации.
 *
 * @param name    имя анимации
 * @param length  длина в секундах
 * @param loop    зациклить
 * @param tracks  треки по имени кости
 */
public record Animation(
        String name,
        float  length,
        boolean loop,
        Map<String, BoneTrack> tracks
) {
    public BoneTrack getTrack(String boneName) { return tracks.get(boneName); }
}