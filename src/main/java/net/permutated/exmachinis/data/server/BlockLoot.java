package net.permutated.exmachinis.data.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.permutated.exmachinis.ModRegistry;

import java.util.ArrayList;
import java.util.List;

public class BlockLoot extends VanillaBlockLoot {
    public BlockLoot(HolderLookup.Provider registries) {
        super(registries);
    }

    @Override
    protected void generate() {
        ModRegistry.BLOCKS.getEntries().forEach(block -> dropSelf(block.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        List<Block> knownBlocks = new ArrayList<>();
        ModRegistry.BLOCKS.getEntries().stream()
            .map(DeferredHolder::get)
            .forEach(knownBlocks::add);
        return knownBlocks;
    }
}
