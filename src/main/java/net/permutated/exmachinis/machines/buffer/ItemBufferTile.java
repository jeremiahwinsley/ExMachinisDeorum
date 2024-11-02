package net.permutated.exmachinis.machines.buffer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.items.ComparatorUpgradeItem;
import net.permutated.exmachinis.machines.base.AbstractMachineBlock;
import net.permutated.exmachinis.machines.base.AbstractMachineTile;
import net.permutated.exmachinis.util.WorkStatus;
import org.jetbrains.annotations.Nullable;

public class ItemBufferTile extends AbstractMachineTile {
    public ItemBufferTile(BlockPos pos, BlockState state) {
        super(ModRegistry.ITEM_BUFFER_TILE.get(), pos, state);
        if (itemStackHandler instanceof MachineItemStackHandler m) {
            m.setListener(this::inventoryChangeListener);
        }
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return true;
    }

    @Override
    protected boolean enableComparatorSlot() {
        return true;
    }

    public static void registerItemCapability(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModRegistry.ITEM_BUFFER_TILE.get(), (machine, side) -> machine.itemStackHandler);
    }

    @Override
    public void tick() {
        if (level instanceof ServerLevel && canTick(getUpgradeTickDelay())) {
            Boolean enabled = getBlockState().getValue(AbstractMachineBlock.ENABLED);
            if (Boolean.FALSE.equals(enabled)) {
                workStatus = WorkStatus.REDSTONE_DISABLED;
                return;
            }

            // ensure that the output is a valid inventory, and get an IItemHandler
            Direction output = getBlockState().getValue(AbstractMachineBlock.OUTPUT);
            BlockPos outPos = getBlockPos().relative(output);

            IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, outPos, output.getOpposite());
            if (itemHandler == null || itemHandler.getSlots() == 0) {
                workStatus = WorkStatus.MISSING_INVENTORY;
                return;
            } else {
                workStatus = WorkStatus.WORKING;
            }

            int maxProcessed = getUpgradeItemsProcessed();

            // iterate input slots until reaching the end, or running out of operations
            for (int i = 0; i < itemStackHandler.getSlots(); i++) {
                if (maxProcessed == 0) {
                    break;
                }

                ItemStack stack = itemStackHandler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    int extractCount;

                    int count = stack.getCount();
                    if (count >= maxProcessed) {
                        extractCount = maxProcessed;
                        maxProcessed = 0;
                    } else {
                        extractCount = count;
                        maxProcessed -= count;
                    }

                    var extractResult = itemStackHandler.extractItem(i, extractCount, true);
                    var insertResult = ItemHandlerHelper.insertItemStacked(itemHandler, extractResult, true);

                    // only move items if the leftover amount is less than the extracted amount
                    if (extractResult.getCount() > insertResult.getCount()) {
                        int inserted = extractResult.getCount() - insertResult.getCount();
                        ItemStack extracted = itemStackHandler.extractItem(i, inserted, false);
                        ItemHandlerHelper.insertItemStacked(itemHandler, extracted, false);
                    }
                }
            }

            // sort the inventory after completing any work available
            sortSlots();
        }
        checkForRedstoneSignal();
    }

    private boolean inventoryChanged = false;
    private void inventoryChangeListener() {
        this.inventoryChanged = true;
    }

    private int redstoneLevel = 0;
    private Direction redstoneDirection = null;
    private void checkForRedstoneSignal() {
        if (level instanceof ServerLevel serverLevel) {
            if (!inventoryChanged) return;
            inventoryChanged = false;

            int calulated = 0;
            ItemStack stack = upgradeStackHandler.getStackInSlot(1);
            if (!stack.isEmpty() && stack.getItem() instanceof ComparatorUpgradeItem) {
                redstoneDirection = ComparatorUpgradeItem.getDirection(stack);

                if (redstoneDirection != null) {
                    calulated = ItemHandlerHelper.calcRedstoneFromInventory(itemStackHandler);
                }
            }

            if (redstoneLevel != calulated) {
                redstoneLevel = calulated;
                serverLevel.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
            }
        }
    }

    public int getRedstoneLevel() {
        return redstoneLevel;
    }

    @Nullable
    public Direction getRedstoneDirection() {
        return redstoneDirection;
    }
}
