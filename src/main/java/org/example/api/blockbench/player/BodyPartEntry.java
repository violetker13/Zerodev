package org.example.api.blockbench.player;

public record BodyPartEntry(
        Part part,
        int index,
        String fileName,   // например "body_1", "right_arm_2"
        float offsetX,
        float offsetY,
        float offsetZ,
        String textureValue,     // base64 value для ResolvableProfile
        String textureSignature  // base64 signature (может быть null для теста)
) {
    /** Удобный конструктор без signature (для теста без подписи). */
    public BodyPartEntry(Part part, int index, String fileName,
                         float offsetX, float offsetY, float offsetZ,
                         String textureValue) {
        this(part, index, fileName, offsetX, offsetY, offsetZ, textureValue, null);
    }
}

