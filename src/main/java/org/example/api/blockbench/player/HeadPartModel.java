package org.example.api.blockbench.player;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.component.DataComponents;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.ResolvableProfile;

import java.util.List;
import java.util.UUID;
public class HeadPartModel {
    private static final float CUBE_SCALE = 0.5f;
    private final Entity       entity;
    private final Part         part;
    private final BodyPartEntry entry;
    public HeadPartModel(BodyPartEntry entry, Pos origin) {
        this.entry  = entry;
        this.part   = entry.part();
        entity = new Entity(EntityType.ITEM_DISPLAY);
        ItemDisplayMeta meta = (ItemDisplayMeta) entity.getEntityMeta();

        meta.setNotifyAboutChanges(false);
        meta.setTranslation(new Vec(
                entry.offsetX(),
                entry.offsetY(),
                entry.offsetZ()
        ));
        meta.setScale(new Vec(CUBE_SCALE, CUBE_SCALE, CUBE_SCALE));
        if(part == Part.HEAD){
            meta.setScale(new Vec(CUBE_SCALE*2, CUBE_SCALE*2, CUBE_SCALE*2));

        }
        meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        ItemStack head = ItemStack.of(Material.PLAYER_HEAD)
                .with(DataComponents.PROFILE, new ResolvableProfile(
                        new PlayerSkin(entry.textureValue(), "")
                ));
        meta.setItemStack(head);
        meta.setPosRotInterpolationDuration(1);
        meta.setTransformationInterpolationStartDelta(1);
        meta.setDisplayContext(ItemDisplayMeta.DisplayContext.HEAD);
        meta.setNotifyAboutChanges(true);
    }
    public void spawn(Instance instance, Pos origin) {
        entity.setInstance(instance, origin);
    }
    public void remove() {
        entity.remove();
    }
    public Entity      getEntity() { return entity; }
    public Part        getPart()   { return part;   }
    public BodyPartEntry getEntry() { return entry; }
    private static ItemStack buildHead(String value, String signature) {
        GameProfile.Property textureProperty = new GameProfile.Property(
                "textures",
                value,
                signature
        );
        GameProfile profile = new GameProfile(
                UUID.randomUUID(),
                "skin",
                List.of(textureProperty)
        );
        ResolvableProfile resolvable = new ResolvableProfile(profile);
        return ItemStack.of(Material.PLAYER_HEAD)
                .with(DataComponents.PROFILE, resolvable);
    }
}