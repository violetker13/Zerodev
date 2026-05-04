package org.example.comands.worldedit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.example.api.zero_command.ZeroCommand;

public class WandCommand extends Command implements ZeroCommand {
    public WandCommand() {
        super("/wand");
        setUsage("/wand");

        addPlayerSyntax((player, context) -> {
            player.getInventory().addItemStack(ItemStack.builder(Material.WOODEN_AXE)
                    .customName(Component.text("WorldEdit Wand", NamedTextColor.GOLD))
                    .build());
            player.sendMessage("Топорик выдан! ЛКМ = pos1, ПКМ = pos2");
        });


    }



    public static boolean isHoldingWand(Player player) {
        return player.getItemInMainHand().material() == Material.WOODEN_AXE;
    }

    @Override
    public Command getCommand() { return this; }
}