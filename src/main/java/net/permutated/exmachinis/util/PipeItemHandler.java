package net.permutated.exmachinis.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Only allows input, to prevent extracting from input slots accidentally.
 */
public class PipeItemHandler implements IItemHandler, IItemHandlerModifiable {

    private final ItemStackHandler handler;

    public PipeItemHandler(ItemStackHandler handler) {
        this.handler = handler;
    }


    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.handler.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return this.handler.getSlots();
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.handler.getStackInSlot(slot);
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return this.handler.insertItem(slot, stack, simulate);
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.handler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.handler.isItemValid(slot, stack);
    }
}
