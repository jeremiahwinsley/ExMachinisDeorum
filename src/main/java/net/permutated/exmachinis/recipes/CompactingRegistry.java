package net.permutated.exmachinis.recipes;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CompactingRegistry {

    private final Object lock = new Object();
    private List<CompactingRecipe> recipeList = Collections.emptyList();
    private final Map<Item, Optional<CompactingRecipe>> recipeByItemCache = new ConcurrentHashMap<>();

    public Optional<CompactingRecipe> findRecipe(final Item item) {
        return recipeByItemCache.computeIfAbsent(item, it -> recipeList.stream()
            .filter(recipe -> recipe.getIngredient().test(it))
            .findFirst());
    }

    public void setRecipeList(final List<RecipeHolder<CompactingRecipe>> recipes) {
        List<CompactingRecipe> temporary = new ArrayList<>(recipes.size());
        recipes.stream().map(RecipeHolder::value).forEach(temporary::add);

        synchronized (lock) {
            recipeList = temporary;
            recipeByItemCache.clear();
        }
    }

    public List<CompactingRecipe> getRecipeList() {
        synchronized (lock) {
            return recipeList;
        }
    }

    public void clearRecipes() {
        synchronized (lock) {
            recipeList = Collections.emptyList();
            recipeByItemCache.clear();
        }
    }
}
