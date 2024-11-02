package net.permutated.exmachinis.data.client;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.util.Constants;

import static net.permutated.exmachinis.util.TranslationKey.advancement;
import static net.permutated.exmachinis.util.TranslationKey.desc;
import static net.permutated.exmachinis.util.TranslationKey.gui;
import static net.permutated.exmachinis.util.TranslationKey.jei;
import static net.permutated.exmachinis.util.TranslationKey.tab;
import static net.permutated.exmachinis.util.TranslationKey.tooltip;

public class Languages {
    private Languages() {
        // nothing to do
    }

    public static class English extends LanguageProvider {

        public English(PackOutput packOutput) {
            super(packOutput, ExMachinis.MODID, "en_us");
        }

        @Override
        protected void addTranslations() {
            addBlock(ModRegistry.FLUX_SIEVE_BLOCK, "Flux Sieve");
            addBlock(ModRegistry.FLUX_HAMMER_BLOCK, "Flux Hammer");
            addBlock(ModRegistry.FLUX_COMPACTOR_BLOCK, "Flux Compactor");
            addBlock(ModRegistry.ITEM_BUFFER_BLOCK, "Item Buffer");

            addItem(ModRegistry.GOLD_UPGRADE, "Gold Upgrade");
            addItem(ModRegistry.DIAMOND_UPGRADE, "Diamond Upgrade");
            addItem(ModRegistry.NETHERITE_UPGRADE, "Netherite Upgrade");
            addItem(ModRegistry.COMPARATOR_UPGRADE, "Comparator Upgrade");

            add(gui("noStatus"), "Machine starting up...");
            add(gui("inventoryMissing"), "Missing inventory on output side.");
            add(gui("inventoryFull"), "Inventory is full.");
            add(gui("working"), "Machine is working.");
            add(gui("outOfEnergy"), "Out of energy.");
            add(gui("meshMissing"), "Mesh required for operation.");
            add(gui("redstoneDisabled"), "Disabled by redstone signal.");

            add(gui("workArea"), "Work area (in chunks)");
            add(gui("workAreaBlocks"), "Work area (in blocks)");
            add(gui("toggleWork"), "Working status");
            add(tab(), "Ex Machinis");

            add(advancement("root"), "Ex Machinis");
            add(advancement(desc("root")), "From machines, automation!");

            add(advancement(Constants.FLUX_SIEVE), "Sieving...with flux!");
            add(advancement(desc(Constants.FLUX_SIEVE)), "Tired of right-clicking? Craft a Flux Sieve");

            add(advancement(Constants.FLUX_HAMMER), "Break it down!");
            add(advancement(desc(Constants.FLUX_HAMMER)), "No more pounding sand! Craft a Flux Hammer");

            add(advancement(Constants.FLUX_COMPACTOR), "Put it back together!");
            add(advancement(desc(Constants.FLUX_COMPACTOR)), "Pesky pieces filling up your storage? Craft a Flux Compactor");

            add(advancement(Constants.GOLD_UPGRADE), "Do more at once!");
            add(advancement(desc(Constants.GOLD_UPGRADE)), "Craft a Gold Upgrade");

            add(advancement(Constants.DIAMOND_UPGRADE), "Do more at once, faster!");
            add(advancement(desc(Constants.DIAMOND_UPGRADE)), "Craft a Diamond Upgrade");

            add(advancement(Constants.NETHERITE_UPGRADE), "Is this the end?");
            add(advancement(desc(Constants.NETHERITE_UPGRADE)), "\"No, this is netherite.\" Craft a Netherite Upgrade");

            add(tooltip("sieve1"), "RF-powered automatic sieve");
            add(tooltip("hammer1"), "RF-powered automatic hammer");
            add(tooltip("compactor1"), "RF-powered automatic compactor");

            add(tooltip("activated"), "Activated");
            add(tooltip("progress"), "Progress: %d/%d seconds");

            add(tooltip("fluxBar"), "Redstone Flux:");
            add(tooltip("fluxData"), "%d/%d RF stored");
            add(tooltip("workBar"), "Work Progress:");
            add(tooltip("workData"), "%d/%d ticks");
            add(tooltip("upgradeSlot"), "Upgrade Slot");
            add(tooltip("meshSlot"), "Mesh Slot");
            add(tooltip("comparatorSlot"), "Comparator Slot");

            add(tooltip("goldItemsProcessed"), "2/4/8");
            add(tooltip("diamondItemsProcessed"), "16/32/64");
            add(tooltip("netheriteItemsProcessed"), "64");

            add(tooltip("stackSize"), "Upgrade count: %s");
            add(tooltip("itemsProcessed"), "Items processed: %s");
            add(tooltip("energyPerTick"), "RF per tick: %s RF/t");
            add(tooltip("costPerBlock"), "RF cost per item: %d");
            add(tooltip("processingTime"), "Processing time: %d ticks");

            add(tooltip("player"), "Player: %s");

            add(tooltip("comparatorDirection"), "Direction: %s");
            add(tooltip("comparatorSetDirection1"), "Sneak + right-click on a block to");
            add(tooltip("comparatorSetDirection2"), "set the redstone output direction");
            add(tooltip("comparatorSetDirection3"), "to the clicked face of the block.");

            add(jei(Constants.COMPACTING), "Flux Compactor");

            add(jei(Constants.FLUX_COMPACTOR), """
                The Flux Compactor will take items from the internal inventory,
                and place the outputs into an inventory below.

                Upgrades can be added to increase the number of items processed at once.

                The block can be disabled by a redstone signal.

                The output side can be configured by shift + right-clicking with an Ex Nihilo hammer.
                """);
            add(jei(Constants.FLUX_HAMMER), """
                The Flux Hammer will take items from an inventory above,
                and place the outputs into an inventory in front.

                Upgrades can be added to increase the number of items processed at once.

                The block can be disabled by a redstone signal.

                The output side can be configured by shift + right-clicking with an Ex Nihilo hammer.
                """);
            add(jei(Constants.FLUX_SIEVE), """
                The Flux Sieve will take items from the internal inventory,
                and place the outputs into an inventory below.

                Upgrades can be added to increase the number of items processed at once.

                Meshes can be enchanted in an anvil with Fortune to increase drop chances.

                The block can be disabled by a redstone signal.

                The output side can be configured by shift + right-clicking with an Ex Nihilo hammer.
                """);

            add(jei(Constants.ITEM_BUFFER), """
                The Item Buffer will take items from the internal inventory,
                and move them into an inventory below.

                Upgrades can be added to increase the number of items processed at once.

                A comparator upgrade can be added to produce a redstone signal in a particular direction.
                You could use this to turn off other machines.

                The block can be disabled by a redstone signal.

                The output side can be configured by shift + right-clicking with an Ex Nihilo hammer.
                """);
        }
    }
}
