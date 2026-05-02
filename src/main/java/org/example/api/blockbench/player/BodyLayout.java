package org.example.api.blockbench.player;

import java.util.List;

/**
 * Хранит описание всех 19 кубов тела Нотча.
 *
 * Система координат (смещение от точки спавна тела, Y=0 — земля):
 *   X — вправо (+) / влево (-)
 *   Y — вверх
 *   Z — вперёд (+) / назад (-)
 *
 * Масштаб одного куба = 0.25 блока (S = 0.25).
 * Пропорции соответствуют стандартной модели игрока Minecraft:
 *   Голова  : Y 1.50 – 1.75
 *   Торс    : Y 0.75 – 1.50  (ширина 0.50: 2 куба ×0.25)
 *   Руки    : Y 0.75 – 1.50  (по бокам от торса, X ±0.375)
 *   Ноги    : Y 0.00 – 0.75  (по полблока каждая, X ±0.125)
 *
 * PNG-файлы скина лежат в resourcepack/notch/<fileName>.png
 *
 * ТЕКСТУРЫ: значения хардкодированы для теста (скин Нотча).
 * Замени NOTCH_VALUE / NOTCH_SIGNATURE на реальные данные из MineSkin.
 */
public final class BodyLayout {

    // ─── ХАРДКОД ТЕКСТУР ДЛЯ ТЕСТА ──────────────────────────────────────────
    // Это реальный скин Нотча — работает без подписи в Minestom offline-режиме.
    // Если нужна подпись — получи через MineSkin API и вставь в NOTCH_SIGNATURE.
    //
    // Для production-кода каждый кубик должен иметь свой value/signature
    // (19 разных MineSkin-профилей из skin_splitter.py).
    // Здесь для теста все кубики используют один скин — ты сам видишь что
    // размещение работает, потом заменишь на реальные.

    private static final String NOTCH_VALUE =
            "eyJ0aW1lc3RhbXAiOjE2MjI1NTA1NDU3NzksInByb2ZpbGVJZCI6ImU2MWI0" +
                    "MmZjOTAzYTQ0MThhMTdhMTNkMDNmZjcxMmYiLCJwcm9maWxlTmFtZSI6Ik5v" +
                    "dGNoIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNL" +
                    "SU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4" +
                    "dHVyZS8yOTIwMDlhNDkyNWI1OGYwMmM3N2RhZGMyM2YwNjlhYTZiZWYzNTMz" +
                    "OTg3YzFlMzM1N2FkM2E0YzA4NzgzIn19fQ==";

    private static final String NOTCH_SIGNATURE = null; // null = offline / без подписи

    // ─── 19 КУБОВ ────────────────────────────────────────────────────────────

    public static final List<BodyPartEntry> LAYOUT = List.of(

            // ── ГОЛОВА (1 куб) ───────────────────────────────────────────────
            //    PNG: head_1.png  |  Y: 1.5–1.75
            new BodyPartEntry(Part.HEAD, 1, "head_1",
                    0.0f, 1.85f, 0.0f,
                    NOTCH_VALUE, NOTCH_SIGNATURE),

            // ── ТОРС (6 кубов, сетка 2 столбца × 3 строки) ──────────────────
            //    PNG: body_1..body_6  |  Y: 0.75–1.50
            //    Порядок: слева-направо, сверху-вниз
            //    body_1 body_2  ← верхний ряд
            //    body_3 body_4  ← средний ряд
            //    body_5 body_6  ← нижний ряд
            new BodyPartEntry(Part.TORSO, 1, "body_1",
                    -0.125f, 1.375f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE),
            new BodyPartEntry(Part.TORSO, 2, "body_2",
                    0.125f, 1.375f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE),
            new BodyPartEntry(Part.TORSO, 3, "body_3",
                    -0.125f, 1.125f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE),
            new BodyPartEntry(Part.TORSO, 4, "body_4",
                    0.125f, 1.125f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE),
            new BodyPartEntry(Part.TORSO, 5, "body_5",
                    -0.125f, 0.875f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE),
            new BodyPartEntry(Part.TORSO, 6, "body_6",
                    0.125f, 0.875f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE),

            // ── ПРАВАЯ РУКА (3 куба, 1×3) ────────────────────────────────────
            //    PNG: right_arm_1..3  |  X: +0.375 (правее торса)
            new BodyPartEntry(Part.RIGHT_ARM, 1, "right_arm_1",
                    0.375f, 1.375f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE),
            new BodyPartEntry(Part.RIGHT_ARM, 2, "right_arm_2",
                    0.375f, 1.125f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE),
            new BodyPartEntry(Part.RIGHT_ARM, 3, "right_arm_3",
                    0.375f, 0.875f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE),

            // ── ЛЕВАЯ РУКА (3 куба, 1×3) ─────────────────────────────────────
            //    PNG: left_arm_1..3  |  X: -0.375 (левее торса)
            new BodyPartEntry(Part.LEFT_ARM, 1, "left_arm_1",
                    -0.375f, 1.375f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE),
            new BodyPartEntry(Part.LEFT_ARM, 2, "left_arm_2",
                    -0.375f, 1.125f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE),
            new BodyPartEntry(Part.LEFT_ARM, 3, "left_arm_3",
                    -0.375f, 0.875f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE),

            // ── ПРАВАЯ НОГА (3 куба, 1×3) ────────────────────────────────────
            //    PNG: right_leg_1..3  |  X: +0.125
            new BodyPartEntry(Part.RIGHT_LEG, 1, "right_leg_1",
                    0.125f, 0.625f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE),
            new BodyPartEntry(Part.RIGHT_LEG, 2, "right_leg_2",
                    0.125f, 0.375f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE),
            new BodyPartEntry(Part.RIGHT_LEG, 3, "right_leg_3",
                    0.125f, 0.125f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE),

            // ── ЛЕВАЯ НОГА (3 куба, 1×3) ─────────────────────────────────────
            //    PNG: left_leg_1..3  |  X: -0.125
            new BodyPartEntry(Part.LEFT_LEG, 1, "left_leg_1",
                    -0.125f, 0.625f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE),
            new BodyPartEntry(Part.LEFT_LEG, 2, "left_leg_2",
                    -0.125f, 0.375f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE),
            new BodyPartEntry(Part.LEFT_LEG, 3, "left_leg_3",
                    -0.125f, 0.125f, 0.0f, NOTCH_VALUE, NOTCH_SIGNATURE)
    );

    private BodyLayout() {}
}