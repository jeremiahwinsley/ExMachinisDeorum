package net.permutated.exmachinis.compat.exnihilo;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.permutated.exmachinis.ConfigHolder;
import thedarkcolour.exdeorum.item.HammerItem;
import thedarkcolour.exdeorum.item.MeshItem;
import thedarkcolour.exdeorum.recipe.ProbabilityRecipe;
import thedarkcolour.exdeorum.recipe.RecipeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Access class for Ex Deorum so that external code isn't referenced from the rest of the mod.
 */
public class ExNihiloAPI {
    private ExNihiloAPI() {
        // nothing to do
    }

    private static Optional<Item> itemFromItemStack(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
            return Optional.of(stack.getItem());
        }
        return Optional.empty();
    }

    private static ItemStack getProbabilityResult(LootContext context, ProbabilityRecipe recipe, int fortune) {
        ItemStack item = recipe.result;
        int amount = recipe.resultAmount.getInt(context);

        for(int i = 0; i < fortune; ++i) {
            if (ThreadLocalRandom.current().nextFloat() < 0.3F) {
                amount += recipe.resultAmount.getInt(context);
            }
        }

        return item.copyWithCount(amount);
    }

    public static boolean canHammer(ItemStack stack) {
        return itemFromItemStack(stack)
            .map(RecipeUtil::getHammerRecipe)
            .isPresent();
    }

    public static List<ItemStack> getHammerResult(ServerLevel level, ItemStack stack) {
        LootContext context = RecipeUtil.emptyLootContext(level);
        return itemFromItemStack(stack)
            .map(RecipeUtil::getHammerRecipe)
            .map(recipe -> getProbabilityResult(context, recipe, 0))
            .map(List::of).orElseGet(ArrayList::new);
    }

    public static boolean isMeshItem(ItemStack stack) {
        return stack.getItem() instanceof MeshItem;
    }

    public static boolean isHammerItem(ItemStack stack) {
        return stack.getItem() instanceof HammerItem;
    }

    public static boolean canSieve(ItemStack stack, ItemStack mesh, boolean waterlogged) {
        if (mesh.getItem() instanceof MeshItem meshItem) {
            return !RecipeUtil.getSieveRecipes(meshItem, stack).isEmpty();
        }
        return false;
    }

    public static List<ItemStack> getSieveResult(ServerLevel level, ItemStack stack, ItemStack mesh, boolean waterlogged) {
        if (mesh.getItem() instanceof MeshItem meshItem) {
            LootContext context = RecipeUtil.emptyLootContext(level);
            var recipes = RecipeUtil.getSieveRecipes(meshItem, stack);

            int fortune = 0;
            if (Boolean.TRUE.equals(ConfigHolder.SERVER.sieveFortuneEnabled.get())) {
                // TODO is this going to be laggy?
                Holder<Enchantment> holder = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FORTUNE);
                fortune = mesh.getEnchantmentLevel(holder);
            }

            List<ItemStack> output = new ArrayList<>();
            for (var sieveRecipe : recipes) {
                output.add(getProbabilityResult(context, sieveRecipe, fortune));
            }
            return output;
        }
        return Collections.emptyList();
    }

}
