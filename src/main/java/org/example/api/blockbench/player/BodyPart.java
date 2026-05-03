package org.example.api.blockbench.player;

import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.player.ResolvableProfile;

/**
 * Один ItemDisplay-куб, привязанный к кости.
 *
 * localOffset — центр куба относительно пивота кости в bind-позе
 *               (в локальном пространстве кости).
 */
public class BodyPart {

    private final Entity     entity;
    private final BoneNode   bone;
    private final float[]    localOffset; // [x, y, z] в пространстве кости

    public BodyPart(BoneNode bone, float ox, float oy, float oz,
                    float scale, String textureValue) {
        this.bone        = bone;
        this.localOffset = new float[]{ox, oy, oz};

        entity = new Entity(EntityType.ITEM_DISPLAY);
        ItemDisplayMeta meta = (ItemDisplayMeta) entity.getEntityMeta();
        meta.setNotifyAboutChanges(false);
        meta.setScale(new Vec(scale, scale, scale));
        meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        meta.setDisplayContext(ItemDisplayMeta.DisplayContext.HEAD);
        meta.setItemStack(buildHead(textureValue));
        meta.setNotifyAboutChanges(true);
    }

    public void spawn(Instance instance, Pos origin) {
        entity.setNoGravity(true);
        entity.setInstance(instance, origin);
    }

    public void remove() { entity.remove(); }

    /**
     * Пересчитывает трансформ entity по текущим мировым данным кости.
     * Entity должна находиться на rootWorldPos.
     */
    public void updateTransform(float[] rootWorldPos) {
        // Поворачиваем локальный оффсет мировым вращением кости
        float[] rotated = bone.worldRot.rotate(localOffset[0], localOffset[1], localOffset[2]);

        // Мировая позиция куба
        float wx = bone.worldPivot[0] + rotated[0];
        float wy = bone.worldPivot[1] + rotated[1];
        float wz = bone.worldPivot[2] + rotated[2];

        // Трансляция относительно entity-origin (rootPos)
        float tx = wx - rootWorldPos[0];
        float ty = wy - rootWorldPos[1];
        float tz = wz - rootWorldPos[2];

        ItemDisplayMeta meta = (ItemDisplayMeta) entity.getEntityMeta();
        meta.setNotifyAboutChanges(false);
        meta.setTranslation(new Vec(tx, ty, tz));
        meta.setLeftRotation(bone.worldRot.toArray());
        meta.setNotifyAboutChanges(true);
    }

    public Entity getEntity() { return entity; }

    // ── helpers ──────────────────────────────────────────────────────────────

    private static ItemStack buildHead(String textureValue) {
        return ItemStack.of(Material.PLAYER_HEAD)
                .with(DataComponents.PROFILE, new ResolvableProfile(new PlayerSkin(textureValue, "")));
    }
}