package net.permutated.exmachinis.machines.sieve;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.IContainerFactory;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.machines.base.AbstractMachineBlock;
import net.permutated.exmachinis.machines.base.AbstractMachineMenu;
import net.permutated.exmachinis.machines.base.AbstractMachineTile;

import javax.annotation.Nullable;
import java.util.List;

import static net.permutated.exmachinis.util.TranslationKey.translateTooltip;

public class FluxSieveBlock extends AbstractMachineBlock implements SimpleWaterloggedBlock {

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 15, 14);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public FluxSieveBlock() {
        super();
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated
    public VoxelShape getShape(BlockState blockState, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    @SuppressWarnings("java:S1874") // mojang deprecated
    public FluidState getFluidState(BlockState blockState) {
        return Boolean.TRUE.equals(blockState.getValue(WATERLOGGED)) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos clickedPos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState(clickedPos);
        return this.defaultBlockState()
            .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluxSieveTile(pos, state);
    }

    @Override
    public BlockEntityType<? extends AbstractMachineTile> getTileType() {
        return ModRegistry.FLUX_SIEVE_TILE.get();
    }

    @Override
    public IContainerFactory<AbstractMachineMenu> containerFactory() {
        return FluxSieveMenu::new;
    }


    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltip, tooltipFlag);
        tooltip.add(translateTooltip("sieve1"));
    }
}
