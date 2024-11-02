package net.permutated.exmachinis.data.client;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.util.ResourceUtil;

public class ItemModels extends ItemModelProvider {
    public ItemModels(PackOutput packOutput, ExistingFileHelper fileHelper) {
        super(packOutput, ExMachinis.MODID, fileHelper);
    }

    private ResourceLocation res(String name) {
        return ResourceUtil.prefix("item/".concat(name));
    }

    @Override
    protected void registerModels() {
        ResourceLocation generated = ResourceLocation.withDefaultNamespace("item/generated");

        ModRegistry.ITEMS.getEntries().stream()
            .filter(item -> !(item.get() instanceof BlockItem))
            .forEach(item -> {
                String name = item.getId().getPath();
                withExistingParent(name, generated)
                    .texture("layer0", res(name));
            });

    }


}
