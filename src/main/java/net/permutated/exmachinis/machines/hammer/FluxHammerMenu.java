package net.permutated.exmachinis.machines.hammer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Block;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.machines.base.AbstractMachineMenu;

import java.util.function.Supplier;

public class FluxHammerMenu extends AbstractMachineMenu {

    public FluxHammerMenu(int windowId, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
        super(ModRegistry.FLUX_HAMMER_MENU.get(), windowId, playerInventory, packetBuffer);

    }

    @Override
    protected Supplier<Block> getBlock() {
        return ModRegistry.FLUX_HAMMER_BLOCK;
    }
}
