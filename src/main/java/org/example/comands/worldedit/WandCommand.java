package org.example.comands.worldedit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.component.DataComponent;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.Event;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.example.Main;

public class WandCommand extends Command {
    public WandCommand() {
        super("/wand");

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) return;

            ItemStack wand = ItemStack.builder(Material.WOODEN_AXE)
                    .customName(Component.text("WorldEdit Wand", NamedTextColor.GOLD))
                    .build();

            player.getInventory().addItemStack(wand);
            player.sendMessage(Component.text("Топорик выдан! ЛКМ = pos1, ПКМ = pos2", NamedTextColor.GREEN));
        });

        // Регистрируем обработчики кликов
        registerWandListeners();
    }

    private void registerWandListeners() {
        // ЛКМ по блоку = pos1
        Main.node.addListener(PlayerBlockBreakEvent.class, event -> {
            Player player = event.getPlayer();
            if (!isHoldingWand(player)) return;

            event.setCancelled(true);
            var pos = event.getBlockPosition();
            WorldEdit.getSelection(player).setPos1(pos, (InstanceContainer) player.getInstance());
            player.sendMessage(Component.text("Pos1: " + pos.blockX() + " " + pos.blockY() + " " + pos.blockZ(), NamedTextColor.GREEN));
        });

        // ПКМ по блоку = pos2
        Main.node.addListener(PlayerBlockInteractEvent.class, event -> {
            if (!event.getHand().equals(PlayerHand.MAIN)) return;

            Player player = event.getPlayer();
            if (!isHoldingWand(player)) return;

            event.setCancelled(true);
            var pos = event.getBlockPosition();
            WorldEdit.getSelection(player).setPos2(pos, (InstanceContainer) player.getInstance());
            player.sendMessage(Component.text(":Pos2" + pos.blockX() + " " + pos.blockY() + " " + pos.blockZ(), NamedTextColor.RED));
        });
    }

    private boolean isHoldingWand(Player player) {
        ItemStack item = player.getItemInMainHand();
        return item.material() == Material.WOODEN_AXE;
    }
}