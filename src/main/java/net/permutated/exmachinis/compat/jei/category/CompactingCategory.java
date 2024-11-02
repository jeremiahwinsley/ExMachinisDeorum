package net.permutated.exmachinis.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.recipes.CompactingRecipe;
import net.permutated.exmachinis.util.Constants;
import net.permutated.exmachinis.util.TranslationKey;

import java.util.Arrays;
import java.util.List;

public class CompactingCategory implements IRecipeCategory<CompactingRecipe> {

    private final IDrawable icon;
    private final MutableComponent title;
    private final IGuiHelper guiHelper;

    public static final RecipeType<CompactingRecipe> RECIPE_TYPE = RecipeType.create(ExMachinis.MODID, Constants.COMPACTING, CompactingRecipe.class);

    public CompactingCategory(final IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModRegistry.FLUX_COMPACTOR_BLOCK.get()));
        title = Component.translatable(TranslationKey.jei(Constants.COMPACTING));
        this.guiHelper = guiHelper;
    }

    @Override
    public int getWidth() {
        return 82;
    }

    @Override
    public int getHeight() {
        return 26;
    }

    @Override
    public void draw(CompactingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiHelper.getSlotDrawable().draw(guiGraphics, 0, 4);
        guiHelper.getRecipeArrow().draw(guiGraphics, 25, 5);
        guiHelper.getOutputSlot().draw(guiGraphics, 56, 0);
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public RecipeType<CompactingRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CompactingRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> inputs = Arrays.stream(recipe.getIngredient().ingredient().getItems())
            .map(stack -> stack.copyWithCount(recipe.getIngredient().count()))
            .toList();

        builder.addSlot(RecipeIngredientRole.INPUT, 1, 5)
            .addIngredients(VanillaTypes.ITEM_STACK, inputs);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 5)
            .addItemStack(recipe.getOutput());
    }
}
