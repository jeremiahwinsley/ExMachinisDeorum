package net.permutated.exmachinis.machines.compactor;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.IContainerFactory;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.machines.base.AbstractMachineBlock;
import net.permutated.exmachinis.machines.base.AbstractMachineMenu;
import net.permutated.exmachinis.machines.base.AbstractMachineTile;

import javax.annotation.Nullable;
import java.util.List;

import static net.permutated.exmachinis.util.TranslationKey.translateTooltip;

public class FluxCompactorBlock extends AbstractMachineBlock {
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluxCompactorTile(pos, state);
    }

    @Override
    public BlockEntityType<? extends AbstractMachineTile> getTileType() {
        return ModRegistry.FLUX_COMPACTOR_TILE.get();
    }

    @Override
    public IContainerFactory<AbstractMachineMenu> containerFactory() {
        return FluxCompactorMenu::new;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltip, tooltipFlag);
        tooltip.add(translateTooltip("compactor1"));
    }
}
