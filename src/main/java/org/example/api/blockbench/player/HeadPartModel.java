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

import static org.example.Main.server;

/**
 * Один куб тела — ItemDisplay с PLAYER_HEAD.
 *
 * Создаётся через {@link PlayerBodyModel}, спавнится в мире через {@link #spawn}.
 * Хранит ссылку на {@link BodyPartEntry} — откуда взяты позиция и текстура.
 */
public class HeadPartModel {

    // Масштаб одного куба (0.25 блока = один сегмент тела)
    private static final float CUBE_SCALE = 0.5f;

    private final Entity       entity;
    private final Part         part;
    private final BodyPartEntry entry;

    /**
     * @param entry   описание куба (часть, смещение, текстура)
     * @param origin  точка спавна тела (левый нижний угол / ноги)
     */
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
        // Билборд — NONE: голова не поворачивается за камерой (статичная модель)
        meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        ItemStack head = ItemStack.of(Material.PLAYER_HEAD)
                .with(DataComponents.PROFILE, new ResolvableProfile(
                        new PlayerSkin(entry.textureValue(), "") // пустая строка вместо null
                ));
        meta.setItemStack(head);

        // Режим отображения — HEAD (голова занимает всё пространство куба)
        meta.setDisplayContext(ItemDisplayMeta.DisplayContext.HEAD);

        meta.setNotifyAboutChanges(true);
    }

    // ── Спавн ────────────────────────────────────────────────────────────────

    /**
     * Спавним ItemDisplay в мире на позиции origin (ноги тела).
     * Смещение уже задано через Translation в мета.
     */
    public void spawn(Instance instance, Pos origin) {
        entity.setInstance(instance, origin);
    }

    /** Убираем из мира. */
    public void remove() {
        entity.remove();
    }

    // ── Геттеры ──────────────────────────────────────────────────────────────

    public Entity      getEntity() { return entity; }
    public Part        getPart()   { return part;   }
    public BodyPartEntry getEntry() { return entry; }

    // ── Приватные хелперы ────────────────────────────────────────────────────

    /**
     * Собирает ItemStack PLAYER_HEAD с нужной текстурой через ResolvableProfile.
     *
     * @param value     base64-строка textures (из MineSkin / хардкод)
     * @param signature base64-подпись Mojang (null в offline-режиме)
     */
    private static ItemStack buildHead(String value, String signature) {
        // GameProfile с рандомным UUID (Minestom не привязывает голову к нику)
        GameProfile.Property textureProperty = new GameProfile.Property(
                "textures",
                value,
                signature  // null — допустимо в offline/Minestom
        );

        GameProfile profile = new GameProfile(
                UUID.randomUUID(),
                "skin",           // ник не важен
                List.of(textureProperty)
        );

        ResolvableProfile resolvable = new ResolvableProfile(profile);

        return ItemStack.of(Material.PLAYER_HEAD)
                .with(DataComponents.PROFILE, resolvable);
    }
}