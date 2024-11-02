package net.permutated.exmachinis.util;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemStackUtil {
    private ItemStackUtil() {
        // nothing to do
    }

    /**
     * Multiply input stack, returning a List of one or more resulting ItemStacks.
     * @param input the input stack to copy.
     * @param times the multiplier
     * @return the list of ItemStacks
     */
    public static List<ItemStack> multiplyStack(ItemStack input, int times) {
        int count = input.getCount();
        int max = input.getMaxStackSize();

        int amount = count * times;
        List<ItemStack> output = new ArrayList<>();
        while (amount > max) {
            output.add(input.copyWithCount(max));
            amount -= max;
        }
        output.add(input.copyWithCount(amount));
        return output;
    }

    /**
     * Multiply input stack, returning a single ItemStack that can be over the maxStackSize.
     * While not explicitly allowed, most ItemHandlers should safely insert oversized stacks.
     * @param input the input stack to copy.
     * @param times the multiplier
     * @return the multiplied ItemStack
     */
    public static ItemStack multiplyStackCount(ItemStack input, int times) {
        int count = input.getCount() * times;
        return input.copyWithCount(count);
    }
}
