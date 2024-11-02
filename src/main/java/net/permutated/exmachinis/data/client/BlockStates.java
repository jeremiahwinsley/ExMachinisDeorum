package net.permutated.exmachinis.data.client;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.machines.hammer.FluxHammerBlock;
import net.permutated.exmachinis.util.Constants;

import static net.permutated.exmachinis.util.ResourceUtil.block;

public class BlockStates extends BlockStateProvider {
    public BlockStates(PackOutput packOutput, ExistingFileHelper fileHelper) {
        super(packOutput, ExMachinis.MODID, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        sieveModel();
        hammerModel();
        compactorModel();
        bufferModel();
    }

    protected void hammerModel() {
        var hammerBlock = ModRegistry.FLUX_HAMMER_BLOCK.get();
        ModelFile model = models().getExistingFile(block(Constants.FLUX_HAMMER));
        ModelFile hopperModel = models().getExistingFile(block(Constants.FLUX_HAMMER.concat("_hopper")));
        horizontalBlock(hammerBlock, blockState -> Boolean.TRUE.equals(blockState.getValue(FluxHammerBlock.HOPPER)) ? hopperModel : model);
        simpleBlockItem(hammerBlock, hopperModel);
    }

    protected void sieveModel() {
        var sieveBlock = ModRegistry.FLUX_SIEVE_BLOCK.get();
        ModelFile model = models().getExistingFile(block(Constants.FLUX_SIEVE));
        simpleBlock(sieveBlock, model);
        simpleBlockItem(sieveBlock, model);
    }

    protected void compactorModel() {
        var compactorBlock = ModRegistry.FLUX_COMPACTOR_BLOCK.get();
        ModelFile model = models().cubeAll(Constants.FLUX_COMPACTOR, block(Constants.FLUX_COMPACTOR));
        simpleBlock(compactorBlock, model);
        simpleBlockItem(compactorBlock, model);
    }

    protected void bufferModel() {
        var bufferBlock = ModRegistry.ITEM_BUFFER_BLOCK.get();
        ModelFile model = models().cubeAll(Constants.ITEM_BUFFER, block(Constants.ITEM_BUFFER));
        simpleBlock(bufferBlock, model);
        simpleBlockItem(bufferBlock, model);
    }
}
