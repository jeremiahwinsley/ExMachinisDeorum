package net.permutated.exmachinis.data.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.machines.base.AbstractMachineBlock;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static net.permutated.exmachinis.util.ResourceUtil.blockTag;

public class BlockTags extends BlockTagsProvider {
    public BlockTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, ExMachinis.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        Block[] machines = ModRegistry.BLOCKS.getEntries().stream()
            .map(Supplier::get)
            .filter(AbstractMachineBlock.class::isInstance)
            .toArray(Block[]::new);

        tag(blockTag("minecraft:mineable/pickaxe")).add(machines);
        tag(blockTag("create:brittle")).add(machines);
    }
}
