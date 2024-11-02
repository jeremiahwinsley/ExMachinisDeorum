package net.permutated.exmachinis.recipes;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.util.Constants;
import net.permutated.exmachinis.util.IngredientStack;

public class CompactingRecipe implements Recipe<SingleRecipeInput> {
    private final IngredientStack ingredient;
    private final ItemStack output;

    public CompactingRecipe(IngredientStack input, ItemStack output) {
        Preconditions.checkNotNull(input, "input cannot be null.");
        Preconditions.checkState(input.count() > 0, "input count must be greater than 0");
        Preconditions.checkNotNull(output, "output cannot be null.");

        this.ingredient = input;
        this.output = output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRegistry.COMPACTING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRegistry.COMPACTING_RECIPE_TYPE.get();
    }

    public IngredientStack getIngredient() {
        return ingredient;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public static class Serializer implements RecipeSerializer<CompactingRecipe> {
        private static final MapCodec<CompactingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IngredientStack.CODEC.fieldOf(Constants.JSON.INPUT).forGetter(CompactingRecipe::getIngredient),
            ItemStack.CODEC.fieldOf(Constants.JSON.OUTPUT).forGetter(CompactingRecipe::getOutput)
        ).apply(instance, CompactingRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, CompactingRecipe> STREAM_CODEC = StreamCodec.composite(
            IngredientStack.STREAM_CODEC, CompactingRecipe::getIngredient,
            ItemStack.STREAM_CODEC, CompactingRecipe::getOutput,
            CompactingRecipe::new
        );

        @Override
        public MapCodec<CompactingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CompactingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return this.ingredient.test(recipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(SingleRecipeInput recipeInput, HolderLookup.Provider registries) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.output.copy();
    }
}
