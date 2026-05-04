package org.example.api.character.config;

/**
 * Описание одного куба тела (ItemDisplay).
 *
 * @param bone    имя кости, к которой прикреплён куб
 * @param ox      смещение центра куба от пивота кости (X)
 * @param oy      смещение Y
 * @param oz      смещение Z
 * @param scale   масштаб куба (0.5 = обычный, 1.0 = голова)
 */
public record PartConfig(
        String bone,
        float ox, float oy, float oz,
        float scale
) {
    public static PartConfig of(String bone, float ox, float oy, float oz, float scale) {
        return new PartConfig(bone, ox, oy, oz, scale);
    }
}