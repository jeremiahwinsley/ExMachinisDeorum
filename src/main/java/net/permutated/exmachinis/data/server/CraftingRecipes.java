package net.permutated.exmachinis.data.server;

import net.allthemods.alltheores.blocks.BlockList;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.data.builders.CompactingRecipeBuilder;
import thedarkcolour.exdeorum.registry.EItems;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class CraftingRecipes extends RecipeProvider {
    public CraftingRecipes(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
        super(packOutput, provider);
    }

    private ShapedRecipeBuilder shaped(ItemLike provider) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, provider)
            .group(ExMachinis.MODID);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {
        buildCraftingRecipes(consumer);
        buildCompactingRecipes(consumer);
    }

    protected void buildCraftingRecipes(RecipeOutput consumer) {
        shaped(ModRegistry.GOLD_UPGRADE.get())
            .pattern("tct")
            .pattern("gdg")
            .pattern("ggg")
            .define('t', Items.CYAN_TERRACOTTA)
            .define('c', Tags.Items.DYES_GREEN)
            .define('d', Tags.Items.INGOTS_GOLD)
            .define('g', Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
            .save(consumer);

        shaped(ModRegistry.DIAMOND_UPGRADE.get())
            .pattern("nun")
            .pattern("nun")
            .pattern("nun")
            .define('u', ModRegistry.GOLD_UPGRADE.get())
            .define('n', Tags.Items.GEMS_DIAMOND)
            .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
            .save(consumer);

        shaped(ModRegistry.NETHERITE_UPGRADE.get())
            .pattern("nun")
            .pattern("nun")
            .pattern("nun")
            .define('u', ModRegistry.DIAMOND_UPGRADE.get())
            .define('n', Tags.Items.INGOTS_NETHERITE)
            .unlockedBy("has_netherite_ingot", has(Tags.Items.INGOTS_NETHERITE))
            .save(consumer);

        shaped(ModRegistry.COMPARATOR_UPGRADE.get())
            .pattern("tct")
            .pattern("gdg")
            .pattern("ggg")
            .define('t', Items.CYAN_TERRACOTTA)
            .define('c', Tags.Items.DYES_GREEN)
            .define('d', Items.COMPARATOR)
            .define('g', Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_comparator", has(Items.COMPARATOR))
            .save(consumer);

        shaped(ModRegistry.FLUX_SIEVE_ITEM.get())
            .pattern("bbb")
            .pattern("bsb")
            .pattern("ihi")
            .define('b', Items.IRON_BARS)
            .define('s', ModRegistry.SIEVES)
            .define('i', Tags.Items.STORAGE_BLOCKS_IRON)
            .define('h', Items.HOPPER)
            .unlockedBy("has_sieve", has(ModRegistry.SIEVES))
            .save(consumer);

        var hammerItem = EItems.DIAMOND_HAMMER.get();
        shaped(ModRegistry.FLUX_HAMMER_ITEM.get())
            .pattern("ggg")
            .pattern("gdg")
            .pattern("ihi")
            .define('g', Tags.Items.GLASS_PANES)
            .define('d', hammerItem)
            .define('i', Tags.Items.STORAGE_BLOCKS_IRON)
            .define('h', Items.HOPPER)
            .unlockedBy("has_diamond_hammer", has(hammerItem))
            .save(consumer);

        shaped(ModRegistry.FLUX_COMPACTOR_ITEM.get())
            .pattern("ipi")
            .pattern("pcp")
            .pattern("ihi")
            .define('i', Tags.Items.STORAGE_BLOCKS_IRON)
            .define('p', Items.PISTON)
            .define('c', Items.COMPARATOR)
            .define('h', Items.HOPPER)
            .unlockedBy("has_hopper", has(Items.HOPPER))
            .save(consumer);

        shaped(ModRegistry.ITEM_BUFFER_ITEM.get())
            .pattern("iri")
            .pattern("ici")
            .pattern("ihi")
            .define('i', Tags.Items.INGOTS_IRON)
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('c', Tags.Items.CHESTS_WOODEN)
            .define('h', Items.HOPPER)
            .unlockedBy("has_hopper", has(Items.HOPPER))
            .save(consumer);
    }

    protected void buildCompactingRecipes(RecipeOutput consumer) {
        CompactingRecipeBuilder.builder(Objects.requireNonNull(Items.IRON_ORE))
            .setInput(Ingredient.of(EItems.IRON_ORE_CHUNK.get()), 4)
            .build(consumer);

        CompactingRecipeBuilder.builder(Objects.requireNonNull(Items.GOLD_ORE))
            .setInput(Ingredient.of(EItems.GOLD_ORE_CHUNK.get()), 4)
            .build(consumer);

        CompactingRecipeBuilder.builder(Objects.requireNonNull(Items.COPPER_ORE))
            .setInput(Ingredient.of(EItems.COPPER_ORE_CHUNK.get()), 4)
            .build(consumer);

        var oreMap = Map.ofEntries(
            Map.entry(BlockList.LEAD_ORE_ITEM, EItems.LEAD_ORE_CHUNK),
            Map.entry(BlockList.NICKEL_ORE_ITEM, EItems.NICKEL_ORE_CHUNK),
            Map.entry(BlockList.SILVER_ORE_ITEM, EItems.SILVER_ORE_CHUNK),
            Map.entry(BlockList.TIN_ORE_ITEM, EItems.TIN_ORE_CHUNK),
            Map.entry(BlockList.ALUMINUM_ORE_ITEM, EItems.ALUMINUM_ORE_CHUNK),
            Map.entry(BlockList.PLATINUM_ORE_ITEM, EItems.PLATINUM_ORE_CHUNK),
            Map.entry(BlockList.URANIUM_ORE_ITEM, EItems.URANIUM_ORE_CHUNK),
            Map.entry(BlockList.ZINC_ORE_ITEM, EItems.ZINC_ORE_CHUNK),
            Map.entry(BlockList.IRIDIUM_ORE_ITEM, EItems.IRIDIUM_ORE_CHUNK),
            Map.entry(BlockList.OSMIUM_ORE_ITEM, EItems.OSMIUM_ORE_CHUNK)
        );

        oreMap.forEach((ore, chunk) -> CompactingRecipeBuilder.builder(ore)
            .setInput(chunk, 4)
            .build(consumer));

        var pebbleMap = Map.ofEntries(
            Map.entry(Blocks.ANDESITE, EItems.ANDESITE_PEBBLE.get()),
            Map.entry(Blocks.BASALT, EItems.BASALT_PEBBLE.get()),
            Map.entry(Blocks.BLACKSTONE, EItems.BLACKSTONE_PEBBLE.get()),
            Map.entry(Blocks.STONE, EItems.STONE_PEBBLE.get()),
            Map.entry(Blocks.CALCITE, EItems.CALCITE_PEBBLE.get()),
            Map.entry(Blocks.DEEPSLATE, EItems.DEEPSLATE_PEBBLE.get()),
            Map.entry(Blocks.DIORITE, EItems.DIORITE_PEBBLE.get()),
            Map.entry(Blocks.GRANITE, EItems.GRANITE_PEBBLE.get()),
            Map.entry(Blocks.TUFF, EItems.TUFF_PEBBLE.get())
        );

        pebbleMap.forEach((block, pebble) -> CompactingRecipeBuilder.builder(block.asItem())
            .setInput(Ingredient.of(pebble), 4)
            .build(consumer));
    }

}
