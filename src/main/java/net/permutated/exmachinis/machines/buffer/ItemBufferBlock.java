package net.permutated.exmachinis.machines.buffer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.IContainerFactory;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.machines.base.AbstractMachineBlock;
import net.permutated.exmachinis.machines.base.AbstractMachineMenu;
import net.permutated.exmachinis.machines.base.AbstractMachineTile;
import org.jetbrains.annotations.Nullable;

public class ItemBufferBlock extends AbstractMachineBlock {
    @Override
    public IContainerFactory<AbstractMachineMenu> containerFactory() {
        return ItemBufferMenu::new;
    }

    @Override
    public BlockEntityType<? extends AbstractMachineTile> getTileType() {
        return ModRegistry.ITEM_BUFFER_TILE.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ItemBufferTile(pos, state);
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated method from super class
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated method from super class
    public int getSignal(BlockState state, BlockGetter getter, BlockPos blockPos, Direction direction) {
        BlockEntity entity = getter.getBlockEntity(blockPos);
        if (entity instanceof ItemBufferTile tile) {
            int redstoneLevel = tile.getRedstoneLevel();
            Direction redstoneDirection = tile.getRedstoneDirection();

            return Mth.clamp(direction.getOpposite() == redstoneDirection ? redstoneLevel : 0, 0, 15);
        }
        return 0;
    }
}
