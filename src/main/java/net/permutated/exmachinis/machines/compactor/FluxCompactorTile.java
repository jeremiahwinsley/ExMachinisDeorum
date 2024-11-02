package net.permutated.exmachinis.machines.compactor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.machines.base.AbstractMachineBlock;
import net.permutated.exmachinis.machines.base.AbstractMachineTile;
import net.permutated.exmachinis.recipes.CompactingRecipe;
import net.permutated.exmachinis.util.WorkStatus;

import java.util.Optional;

import static net.permutated.exmachinis.util.ItemStackUtil.multiplyStackCount;

public class FluxCompactorTile extends AbstractMachineTile {
    public FluxCompactorTile(BlockPos pos, BlockState state) {
        super(ModRegistry.FLUX_COMPACTOR_TILE.get(), pos, state);
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
        return true;
    }

    @Override
    public void tick() {
        if (level instanceof ServerLevel serverLevel && canTick(getUpgradeTickDelay())) {

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
                if (!stack.isEmpty()) {
                    Optional<CompactingRecipe> maybe = ModRegistry.COMPACTING_REGISTRY.findRecipe(stack.getItem());
                    if (maybe.isEmpty() || !maybe.get().getIngredient().test(stack)) {
                        continue;
                    }
                    CompactingRecipe recipe = maybe.get();

                    // Compactor recipes allow multiple inputs > one output.
                    // maxProcessing is defined as the max number of outputs.
                    // This will translate into a larger number of inputs.

                    int multiplier;
                    int recipeInput = recipe.getIngredient().count();

                    // shrink stack count by remaining operations or current stack size
                    // (after accounting for input size), whichever is smaller
                    var copy = stack.copy();
                    int count = stack.getCount();
                    int maxInputs = maxProcessed * recipeInput;

                    if (count >= maxInputs) {
                        multiplier = maxProcessed;
                        copy.shrink(maxInputs);
                        maxProcessed = 0;
                    } else {
                        multiplier = count / recipeInput;
                        int inputUsed = multiplier * recipeInput;
                        copy.shrink(inputUsed);
                        maxProcessed -= multiplier;
                    }

                    if (!processResults(itemHandler, recipe, multiplier, true)) {
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

                    // execute actual inserts and extractions
                    itemStackHandler.setStackInSlot(i, copy); // remove input
                    energyStorage.consumeEnergy(totalCost, false);
                    processResults(itemHandler, recipe, multiplier, false);
                }
            }

            // sort the inventory after completing any work available
            sortSlots();
        }
    }

    private boolean processResults(IItemHandler itemHandler, CompactingRecipe recipe, int multiplier, boolean simulate) {
        // process compacting results
        ItemStack output = multiplyStackCount(recipe.getOutput(), multiplier);
        var response = ItemHandlerHelper.insertItemStacked(itemHandler, output, simulate);
        if (!response.isEmpty()) {
            workStatus = WorkStatus.INVENTORY_FULL;
        }
        return workStatus == WorkStatus.WORKING;
    }
}
