package net.permutated.exmachinis.machines.hammer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.compat.exnihilo.ExNihiloAPI;
import net.permutated.exmachinis.machines.base.AbstractMachineBlock;
import net.permutated.exmachinis.machines.base.AbstractMachineTile;
import net.permutated.exmachinis.util.WorkStatus;

import static net.permutated.exmachinis.util.ItemStackUtil.multiplyStackCount;

public class FluxHammerTile extends AbstractMachineTile {
    public FluxHammerTile(BlockPos pos, BlockState state) {
        super(ModRegistry.FLUX_HAMMER_TILE.get(), pos, state);
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return true;
    }


    /**
     * Perform migrations based on a version number saved in the tile.
     * @param serverLevel the server level
     */
    private void migrate(ServerLevel serverLevel) {
        if (this.version == AbstractMachineBlock.VERSION) {
            return;
        }

        if (this.version > AbstractMachineBlock.VERSION) {
            ExMachinis.LOGGER.error("Current tile version higher than block version - things may break!");
            return;
        }

        if (this.version == 0) {
            // v2.0.0 to v2.0.1
            // OUTPUT defaults to NORTH on existing blocks, when it should match FACING
            Direction facing = getBlockState().getValue(FluxHammerBlock.FACING);
            serverLevel.setBlock(getBlockPos(), getBlockState().setValue(AbstractMachineBlock.OUTPUT, facing), Block.UPDATE_CLIENTS);
            this.version = 1;
            this.setChanged();
        }
    }

    @Override
    public void tick() {
        if (level instanceof ServerLevel serverLevel && canTick(getUpgradeTickDelay())) {
            migrate(serverLevel);

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

            int cost = getUpgradeEnergyCost();
            int stored = energyStorage.getEnergyStored();
            int maxProcessed = getUpgradeItemsProcessed();

            if (stored < cost) {
                // not enough energy for an operation
                workStatus = WorkStatus.OUT_OF_ENERGY;
                return;
            } else if (cost > 0) { // don't need to run this if cost is 0
                // figure out how many operations we can do with the remaining energy
                int quotient = (stored / cost);
                if (quotient < maxProcessed) {
                    maxProcessed = quotient;
                }
            }

            // iterate input slots until reaching the end, or running out of operations
            for (int i = 0; i < itemStackHandler.getSlots(); i++) {
                if (maxProcessed == 0) {
                    break;
                }

                ItemStack stack = itemStackHandler.getStackInSlot(i);
                if (!stack.isEmpty() && ExNihiloAPI.canHammer(stack)) {
                    int multiplier;

                    // shrink stack count by remaining operations or current stack size, whichever is smaller
                    var copy = stack.copy();
                    int count = stack.getCount();
                    if (count >= maxProcessed) {
                        multiplier = maxProcessed;
                        copy.shrink(maxProcessed);
                        maxProcessed = 0;
                    } else {
                        multiplier = count;
                        copy = ItemStack.EMPTY;
                        maxProcessed -= count;
                    }

                    if (!processResults(serverLevel, itemHandler, stack, multiplier, true)) {
                        // simulating inserts failed
                        return;
                    }

                    int totalCost = cost * multiplier;
                    boolean result = energyStorage.consumeEnergy(totalCost, true);
                    if (!result) {
                        // simulating energy use failed
                        workStatus = WorkStatus.OUT_OF_ENERGY;
                        return;
                    }

                    itemStackHandler.setStackInSlot(i, copy); // shrink input
                    energyStorage.consumeEnergy(totalCost, false);
                    processResults(serverLevel, itemHandler, stack, multiplier, false);
                }
            }

            // refill an empty slot from inventory above, if it exists
            pullFromAbove();
        }
    }

    private boolean processResults(ServerLevel serverLevel, IItemHandler itemHandler, ItemStack stack, int multiplier, boolean simulate) {
        // process hammer results
        ExNihiloAPI.getHammerResult(serverLevel, stack).stream()
            .map(result -> multiplyStackCount(result, multiplier))
            .map(output -> ItemHandlerHelper.insertItemStacked(itemHandler, output, simulate))
            .forEach(response -> {
                if (!response.isEmpty()) {
                    workStatus = WorkStatus.INVENTORY_FULL;
                }
            });
        return workStatus == WorkStatus.WORKING;
    }

    private void pullFromAbove() {
        // optionally connect to an inventory above to pull from
        IItemHandler inputItemHandler = null;
        if (level != null && getBlockState().getValue(FluxHammerBlock.HOPPER).equals(Boolean.TRUE)) {
            // ensure that block above is a valid inventory, and get an IItemHandler
            BlockPos above = getBlockPos().above();
            inputItemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, above, Direction.DOWN);
        }

        if (inputItemHandler != null) {
            // iterate input slots until reaching the end, or finding a valid stack
            for (int i = 0; i < inputItemHandler.getSlots(); i++) {
                ItemStack stack = inputItemHandler.getStackInSlot(i);
                if (!stack.isEmpty() && ExNihiloAPI.canHammer(stack)) {
                    // extract up to one stack
                    var extractResult = inputItemHandler.extractItem(i, stack.getMaxStackSize(), true);
                    var insertResult = ItemHandlerHelper.insertItemStacked(itemStackHandler, extractResult, true);

                    // only move items if the leftover amount is less than the extracted amount
                    // should always be true unless item.maxStackSize > slot.getMaxStackSize,
                    // or the internal inventory is full
                    if (extractResult.getCount() > insertResult.getCount()) {
                        int inserted = extractResult.getCount() - insertResult.getCount();
                        ItemStack extracted = inputItemHandler.extractItem(i, inserted, false);
                        ItemHandlerHelper.insertItemStacked(itemStackHandler, extracted, false);
                        return;
                    }
                }
            }
        }
    }
}
