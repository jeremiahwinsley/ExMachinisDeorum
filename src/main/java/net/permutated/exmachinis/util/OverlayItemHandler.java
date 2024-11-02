package net.permutated.exmachinis.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Allows prepending an additional inventory to a base inventory.
 * Intended for adding GUI-only slots while allowing access to the base inventory with automation.
 * Allows shift-clicking to fill overlay slots before the main inventory.
 */
public class OverlayItemHandler implements IItemHandler, IItemHandlerModifiable {

    private final int slots;
    private final ItemStackHandler base;
    private final ItemStackHandler overlay;

    public OverlayItemHandler(ItemStackHandler base, ItemStackHandler overlay) {
        this.base = base;
        this.overlay = overlay;

        this.slots = base.getSlots() + overlay.getSlots();
    }

    public IItemHandler getBase() {
        return base;
    }

    @Override
    public int getSlots() {
        return slots;
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < overlay.getSlots()) {
            return overlay.getStackInSlot(slot);
        } else {
            return base.getStackInSlot(slot - overlay.getSlots());
        }
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot < overlay.getSlots()) {
            return overlay.insertItem(slot, stack, simulate);
        } else {
            return base.insertItem(slot - overlay.getSlots(), stack, simulate);
        }
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot < overlay.getSlots()) {
            return overlay.extractItem(slot, amount, simulate);
        } else {
            return base.extractItem(slot - overlay.getSlots(), amount, simulate);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        if (slot < overlay.getSlots()) {
            return overlay.getSlotLimit(slot);
        } else {
            return base.getSlotLimit(slot - overlay.getSlots());
        }
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (slot < overlay.getSlots()) {
            return overlay.isItemValid(slot, stack);
        } else {
            return base.isItemValid(slot - overlay.getSlots(), stack);
        }
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (slot < overlay.getSlots()) {
            overlay.setStackInSlot(slot, stack);
        } else {
            base.setStackInSlot(slot - overlay.getSlots(), stack);
        }
    }
}
