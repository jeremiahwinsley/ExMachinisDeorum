package net.permutated.exmachinis.data.builders;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.permutated.exmachinis.data.RecipeException;
import net.permutated.exmachinis.recipes.CompactingRecipe;
import net.permutated.exmachinis.util.Constants;
import net.permutated.exmachinis.util.IngredientStack;
import net.permutated.exmachinis.util.ResourceUtil;

import java.util.function.Supplier;

public class CompactingRecipeBuilder {

    private IngredientStack ingredient = new IngredientStack(Ingredient.EMPTY, 0);
    private final ItemStack output;

    protected String getPrefix() {
        return Constants.COMPACTING;
    }

    public CompactingRecipeBuilder(ItemStack output) {
        this.output = output;
    }

    public static CompactingRecipeBuilder builder(Item output, int count) {
        return new CompactingRecipeBuilder(new ItemStack(output, count));
    }

    public static CompactingRecipeBuilder builder(Item output) {
        return new CompactingRecipeBuilder(new ItemStack(output));
    }

    public static CompactingRecipeBuilder builder(Supplier<Item> output) {
        return builder(output.get());
    }

    public CompactingRecipeBuilder setInput(Ingredient input, int count) {
        this.ingredient = new IngredientStack(input, count);
        return this;
    }

    public CompactingRecipeBuilder setInput(ItemLike input, int count) {
        return setInput(Ingredient.of(input), count);
    }

    public CompactingRecipeBuilder setInput(TagKey<Item> input, int count) {
        return setInput(Ingredient.of(input), count);
    }

    public void build(RecipeOutput consumer) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(output.getItem());
        String modId = key.getNamespace();
        String path = key.getPath();

        ResourceLocation id = ResourceUtil.prefix(getPrefix() + "/" + path);

        if (Ingredient.EMPTY.equals(ingredient.ingredient())) {
            throw new RecipeException(id.toString(), "input is required");
        }

        consumer
            .withConditions(new ModLoadedCondition(modId))
            .accept(id, new CompactingRecipe(ingredient, output), null);
    }
}
