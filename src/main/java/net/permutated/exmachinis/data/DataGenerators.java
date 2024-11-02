package net.permutated.exmachinis.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.data.client.BlockStates;
import net.permutated.exmachinis.data.client.ItemModels;
import net.permutated.exmachinis.data.client.Languages;
import net.permutated.exmachinis.data.server.Advancements;
import net.permutated.exmachinis.data.server.BlockLoot;
import net.permutated.exmachinis.data.server.BlockTags;
import net.permutated.exmachinis.data.server.CraftingRecipes;
import net.permutated.exmachinis.data.server.ItemTags;

import java.util.Collections;
import java.util.List;

@EventBusSubscriber(modid = ExMachinis.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class DataGenerators {
    private DataGenerators() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        if (event.includeServer()) {
            generator.addProvider(true, new AdvancementProvider(packOutput, event.getLookupProvider(), fileHelper,
                List.of(new Advancements())));
            BlockTags blockTags = new BlockTags(packOutput, event.getLookupProvider(), fileHelper);
            generator.addProvider(true, blockTags);
            generator.addProvider(true, new ItemTags(packOutput, event.getLookupProvider(), blockTags.contentsGetter()));
            generator.addProvider(true, new CraftingRecipes(packOutput, event.getLookupProvider()));
            generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)),
                event.getLookupProvider()
            ));
        }
        if (event.includeClient()) {
            generator.addProvider(true, new BlockStates(packOutput, fileHelper));
            generator.addProvider(true, new ItemModels(packOutput, fileHelper));
            generator.addProvider(true, new Languages.English(packOutput));
        }

    }
}
