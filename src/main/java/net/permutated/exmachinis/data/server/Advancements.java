package net.permutated.exmachinis.data.server;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.util.Constants;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.permutated.exmachinis.util.TranslationKey.translateAdvancement;

public class Advancements implements AdvancementProvider.AdvancementGenerator {

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer, ExistingFileHelper existingFileHelper) {
        var root = new Advancement.Builder()
            .display(displayInfo(
                ModRegistry.FLUX_SIEVE_ITEM.get(),
                translateAdvancement("root"),
                translateAdvancement("root.desc"),
                Optional.of(ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png")),
                AdvancementType.TASK,
                false,
                false,
                false
            ))
            .addCriterion("item", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
            .save(consumer, advancementPath("root"));

        BiFunction<Supplier<BlockItem>, String, AdvancementHolder> machineBuilder = (registryObject, machine) -> new Advancement.Builder()
            .parent(root)
            .display(displayItem(registryObject, machine))
            .addCriterion("has_" + machine, hasItem(registryObject.get()))
            .save(consumer, advancementPath(machine));

        var sieve = machineBuilder.apply(ModRegistry.FLUX_SIEVE_ITEM, Constants.FLUX_SIEVE);
        var hammer = machineBuilder.apply(ModRegistry.FLUX_HAMMER_ITEM, Constants.FLUX_HAMMER);
        var compactor = machineBuilder.apply(ModRegistry.FLUX_COMPACTOR_ITEM, Constants.FLUX_COMPACTOR);

        TriFunction<Supplier<Item>, String, AdvancementHolder, AdvancementHolder> upgradeBuilder = (registryObject, upgrade, parent) -> new Advancement.Builder()
            .parent(parent)
            .display(displayItem(registryObject, upgrade))
            .addCriterion("has_" + upgrade, hasItem(registryObject.get()))
            .save(consumer, advancementPath(upgrade));

        var gold = upgradeBuilder.apply(ModRegistry.GOLD_UPGRADE, Constants.GOLD_UPGRADE, root);
        var diamond = upgradeBuilder.apply(ModRegistry.DIAMOND_UPGRADE, Constants.DIAMOND_UPGRADE, gold);
        var netherite = upgradeBuilder.apply(ModRegistry.NETHERITE_UPGRADE, Constants.NETHERITE_UPGRADE, diamond);
    }

    private String advancementPath(String key) {
        return String.format("%s:%s", ExMachinis.MODID, key);
    }

    private Criterion<InventoryChangeTrigger.TriggerInstance> hasItem(ItemLike item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(item).build());
    }

    private DisplayInfo displayItem(Supplier<? extends Item> item, String translationKey) {
        return displayInfo(item.get(),
            translateAdvancement(translationKey),
            translateAdvancement(translationKey + ".desc"),
            Optional.empty(), AdvancementType.TASK, true, true, false);
    }

    @SuppressWarnings("java:S107") // arguments needed for returned class
    private DisplayInfo displayInfo(ItemLike title, Component description, Component icon, Optional<ResourceLocation> background, AdvancementType frame, boolean showToast, boolean announceChat, boolean hidden) {
        return new DisplayInfo(new ItemStack(title.asItem()), description, icon, background, frame, showToast, announceChat, hidden);
    }
}
