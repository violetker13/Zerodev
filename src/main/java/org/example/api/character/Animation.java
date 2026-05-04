package org.example.api.character;

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
        Map<String, AnimationTrack> tracks
) {
    public AnimationTrack getTrack(String boneName) { return tracks.get(boneName); }
}