package net.permutated.exmachinis.data.client;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;

import java.util.Collection;

public class ItemModels extends ItemModelProvider {
    public ItemModels(PackOutput packOutput, ExistingFileHelper fileHelper) {
        super(packOutput, ExMachinis.MODID, fileHelper);
    }

    private ResourceLocation res(String name) {
        return new ResourceLocation(ExMachinis.MODID, "item/".concat(name));
    }

    @Override
    protected void registerModels() {
        Collection<RegistryObject<Item>> entries = ModRegistry.ITEMS.getEntries();

        ResourceLocation generated = new ResourceLocation("item/generated");

        entries.stream()
            .filter(item -> !(item.get() instanceof BlockItem))
            .forEach(item -> {
                String name = item.getId().getPath();
                withExistingParent(name, generated)
                    .texture("layer0", res(name));
            });

    }


}
