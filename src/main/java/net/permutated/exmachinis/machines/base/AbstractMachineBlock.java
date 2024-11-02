package net.permutated.exmachinis.machines.base;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.items.UpgradeItem;

import javax.annotation.Nullable;

public abstract class AbstractMachineBlock extends Block implements EntityBlock {
    public static final int VERSION = 1; // @see FluxHammerTile#migrate()
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
    public static final DirectionProperty OUTPUT = DirectionProperty.create("output");

    protected AbstractMachineBlock() {
        super(Properties.of().mapColor(MapColor.METAL).strength(3.0F, 3.0F).noOcclusion()
            .isRedstoneConductor((state, getter, pos) -> false)); // allow chests to be opened underneath
        this.registerDefaultState(this.defaultBlockState()
            .setValue(ENABLED, Boolean.TRUE)
            .setValue(OUTPUT, Direction.DOWN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ENABLED, OUTPUT);
    }

    public abstract IContainerFactory<AbstractMachineMenu> containerFactory();

    @SuppressWarnings("java:S1452") // wildcard required here
    public abstract BlockEntityType<? extends AbstractMachineTile> getTileType();


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == getTileType() ? AbstractMachineTile::tick : null;
    }


    @Override
    public void destroy(LevelAccessor world, BlockPos blockPos, BlockState blockState) {
        if (world.getBlockEntity(blockPos) instanceof AbstractMachineTile machineTile) {
            machineTile.dropItems();
        }

        super.destroy(world, blockPos, blockState);
    }


    @Override
    @SuppressWarnings("java:S1874") // deprecated method from super class
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof AbstractMachineTile machineTile) {
                machineTile.dropItems();
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated method from super class
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!newState.is(state.getBlock())) {
            this.checkPoweredState(level, pos, state);
        }
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated method from super class
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean isMoving) {
        this.checkPoweredState(level, pos, state);
    }

    //TODO avoid chunk loading?
    private void checkPoweredState(Level level, BlockPos pos, BlockState state) {
        boolean flag = !level.hasNeighborSignal(pos);
        if (!Boolean.valueOf(flag).equals((state.getValue(ENABLED)))) {
            level.setBlock(pos, state.setValue(ENABLED, flag), Block.UPDATE_ALL);
        }

    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof AbstractMachineTile machineTile) {
                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable(getDescriptionId());
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), player.registryAccess(), ConnectionType.NEOFORGE);
                        machineTile.updateContainer(buffer);
                        return containerFactory().create(i, playerInventory, buffer);
                    }
                };
                player.openMenu(containerProvider, machineTile::updateContainer);
            } else {
                ExMachinis.LOGGER.error("tile entity not instance of AbstractMachineTile");
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof AbstractMachineTile machineTile) {
                ItemStack stackInHand = player.getItemInHand(hand).copy();
                if (!stackInHand.isEmpty() && stackInHand.getItem() instanceof UpgradeItem handItem) {
                    ItemStack inSlot = machineTile.upgradeStackHandler.getStackInSlot(0).copy();
                    if (player.isCreative()) {
                        if (!ItemStack.matches(inSlot, stackInHand)) {
                            // We are in creative, and the contents of the slot is different than what's in hand.
                            // Replace whatever upgrade is in the machine with the stack in hand.
                            machineTile.upgradeStackHandler.setStackInSlot(0, stackInHand);
                            return ItemInteractionResult.SUCCESS;
                        }
                    } else if (inSlot.isEmpty()) { // no upgrades in machine, move the whole stack
                        ItemStack result = machineTile.upgradeStackHandler.insertItem(0, stackInHand, true);
                        if (result.isEmpty()) { // verify that all items were inserted
                            ItemStack actual = machineTile.upgradeStackHandler.insertItem(0, stackInHand, false);
                            player.setItemInHand(hand, actual);
                            return ItemInteractionResult.SUCCESS;
                        }
                    } else if (inSlot.is(stackInHand.getItem())) { // same upgrade type in machine
                        if (inSlot.getCount() < inSlot.getMaxStackSize()) { // make sure the stack size is not max already
                            ItemStack result = machineTile.upgradeStackHandler.insertItem(0, stackInHand, true);
                            int maxInsert = inSlot.getMaxStackSize() - inSlot.getCount(); // the max that can be inserted in the slot.
                            int expectedToRemain = Math.max(stackInHand.getCount() - maxInsert, 0); // if max is greater than count, clamp to 0
                            if (expectedToRemain == result.getCount()) { // verify that the simulated count matches the expected count
                                ItemStack actual = machineTile.upgradeStackHandler.insertItem(0, stackInHand, false);
                                player.setItemInHand(hand, actual);
                                return ItemInteractionResult.SUCCESS;
                            }
                        }
                    } else if (inSlot.getItem() instanceof UpgradeItem slotItem && handItem.getTier().compareTo(slotItem.getTier()) > 0) {
                        // handItem is a higher tier UpgradeItem, so swap slots
                        machineTile.upgradeStackHandler.setStackInSlot(0, stackInHand);
                        player.setItemInHand(hand, inSlot);
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            } else {
                ExMachinis.LOGGER.error("tile entity not instance of AbstractMachineTile");
                return ItemInteractionResult.FAIL;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
