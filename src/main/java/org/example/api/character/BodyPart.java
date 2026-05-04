package org.example.api.character;

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
import net.minestom.server.item.component.CustomModelData;
import net.minestom.server.network.player.ResolvableProfile;

import java.util.List;

/**
 * Один ItemDisplay-куб, привязанный к кости.
 *
 * localOffset — центр куба относительно пивота кости в bind-позе
 *               (в локальном пространстве кости).
 */
public class BodyPart {

    private final Entity   entity;
    private final BoneNode bone;

    public BodyPart(BoneNode bone, int customModelData) {
        this.bone = bone;

        entity = new Entity(EntityType.ITEM_DISPLAY);
        ItemDisplayMeta meta = (ItemDisplayMeta) entity.getEntityMeta();
        meta.setNotifyAboutChanges(false);
        meta.setScale(new Vec(1, 1, 1));
        meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        meta.setDisplayContext(ItemDisplayMeta.DisplayContext.HEAD);
        meta.setItemStack(
                ItemStack.builder(Material.PAPER)
                        .set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(
                                List.of((float) customModelData), // floats
                                List.of(),                         // flags
                                List.of(String.valueOf(customModelData)),                         // strings
                                List.of()                          // colors
                        ))
                        .build()
        );
        meta.setTransformationInterpolationDuration(1);
        meta.setPosRotInterpolationDuration(1);
        meta.setNotifyAboutChanges(true);
    }

    public void spawn(Instance instance, Pos origin) {
        entity.setNoGravity(true);
        entity.setInstance(instance, origin);
    }

    // BodyPart.java
    public void updateTransform(float[] rootWorldPos, Vec offset) {
        ItemDisplayMeta meta = (ItemDisplayMeta) entity.getEntityMeta();
        meta.setNotifyAboutChanges(false);

        float tx = bone.worldPivot[0] - rootWorldPos[0] + (float) offset.x();
        float ty = bone.worldPivot[1] - rootWorldPos[1] + (float) offset.y();
        float tz = bone.worldPivot[2] - rootWorldPos[2] + (float) offset.z();

        meta.setTranslation(new Vec(tx, ty, tz));
        meta.setLeftRotation(bone.worldRot.toArray());

        meta.setNotifyAboutChanges(true);
    }

    public void remove() { entity.remove(); }
    public Entity getEntity() { return entity; }
}