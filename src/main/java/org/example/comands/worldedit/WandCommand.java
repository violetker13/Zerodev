package org.example.comands.worldedit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.example.Main;
import org.example.api.zero_command.ZeroCommand;

public class WandCommand extends Command implements ZeroCommand {
    public WandCommand() {
        super("/wand");
        setUsage("//wand");

        addPlayerSyntax((player, context) -> {
            player.getInventory().addItemStack(ItemStack.builder(Material.WOODEN_AXE)
                    .customName(Component.text("WorldEdit Wand", NamedTextColor.GOLD))
                    .build());
            player.sendMessage("Топорик выдан! ЛКМ = pos1, ПКМ = pos2");
        });

        registerListeners();
    }

    private void registerListeners() {
        Main.node.addListener(PlayerBlockBreakEvent.class, event -> {
            Player player = event.getPlayer();
            if (!isHoldingWand(player)) return;
            event.setCancelled(true);
            var pos = event.getBlockPosition();
            WorldEdit.getSelection(player).setPos1(pos, (InstanceContainer) player.getInstance());
            player.sendMessage(Component.text(
                    "Pos1: " + pos.blockX() + " " + pos.blockY() + " " + pos.blockZ(),
                    NamedTextColor.GREEN
            ));
        });

        Main.node.addListener(PlayerBlockInteractEvent.class, event -> {
            if (!event.getHand().equals(PlayerHand.MAIN)) return;
            Player player = event.getPlayer();
            if (!isHoldingWand(player)) return;
            event.setCancelled(true);
            var pos = event.getBlockPosition();
            WorldEdit.getSelection(player).setPos2(pos, (InstanceContainer) player.getInstance());
            player.sendMessage(Component.text(
                    "Pos2: " + pos.blockX() + " " + pos.blockY() + " " + pos.blockZ(),
                    NamedTextColor.RED
            ));
        });
    }

    private boolean isHoldingWand(Player player) {
        return player.getItemInMainHand().material() == Material.WOODEN_AXE;
    }

    @Override
    public Command getCommand() { return this; }
}