package net.permutated.exmachinis.data.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.permutated.exmachinis.ModRegistry;
import thedarkcolour.exdeorum.compat.CompatUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ItemTags extends ItemTagsProvider {
    TagKey<Item> tag;

    public ItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        List<ItemLike> sieves = CompatUtil.getAvailableSieves(true, false);

        var sieveTag = tag(ModRegistry.SIEVES);
        for (ItemLike sieve : sieves) {
            sieveTag.add(sieve.asItem());
        }
    }
}
