package net.permutated.exmachinis.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.permutated.exmachinis.ConfigHolder;

import java.util.List;

import static net.permutated.exmachinis.util.TranslationKey.translateTooltip;

public class UpgradeItem extends Item {

    public enum Tier {
        GOLD(3),
        DIAMOND(3),
        NETHERITE(1),
        ;

        final int stackSize;
        Tier(int stackSize) {
            this.stackSize = stackSize;
        }

        public int getItemsProcessed(int stackCount) {
            return switch (this) {
                case GOLD -> 1 << stackCount;
                case DIAMOND -> 1 << (3 + stackCount);
                case NETHERITE -> 64;
            };
        }
    }

    public UpgradeItem(Tier tier) {
        super(new Properties().stacksTo(tier.stackSize).setNoRepair());
        this.tier = tier;
    }

    private final Tier tier;

    public Tier getTier() {
        return this.tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltip, tooltipFlag);

        // Items Processed: 2/4/8
        // RF cost per block: 1280
        // Processing time: 160 ticks
        int cost = 0;
        int time = 1;
        switch (getTier()) {
            case GOLD -> {
                cost = ConfigHolder.SERVER.goldEnergyPerBlock.get();
                time = ConfigHolder.SERVER.goldTicksPerOperation.get();
            }
            case DIAMOND -> {
                cost = ConfigHolder.SERVER.diamondEnergyPerBlock.get();
                time = ConfigHolder.SERVER.diamondTicksPerOperation.get();
            }
            case NETHERITE -> {
                cost = ConfigHolder.SERVER.netheriteEnergyPerBlock.get();
                time = ConfigHolder.SERVER.netheriteTicksPerOperation.get();
            }
        }

        MutableComponent stackSize = Component.empty();
        MutableComponent itemsProcessed = Component.empty();
        MutableComponent energyPerTick = Component.empty();

        for (int i = 1;i <= getTier().stackSize;i++) {
            ChatFormatting style = i == stack.getCount() ? ChatFormatting.WHITE : ChatFormatting.GRAY;

            int count = getTier().getItemsProcessed(i);
            int perTick = (count * cost) / time;

            stackSize.append(Component.literal(String.valueOf(i)).withStyle(style));
            itemsProcessed.append(Component.literal(String.valueOf(count)).withStyle(style));
            energyPerTick.append(Component.literal(String.valueOf(perTick)).withStyle(style));

            if (i < getTier().stackSize) {
                stackSize.append(Component.literal("/").withStyle(ChatFormatting.GRAY));
                itemsProcessed.append(Component.literal("/").withStyle(ChatFormatting.GRAY));
                energyPerTick.append(Component.literal("/").withStyle(ChatFormatting.GRAY));
            }
        }

        tooltip.add(translateTooltip("stackSize", stackSize).withStyle(ChatFormatting.GRAY));
        tooltip.add(translateTooltip("itemsProcessed", itemsProcessed).withStyle(ChatFormatting.GRAY));
        tooltip.add(translateTooltip("energyPerTick", energyPerTick).withStyle(ChatFormatting.GRAY));
        tooltip.add(translateTooltip("costPerBlock", cost).withStyle(ChatFormatting.GRAY));
        tooltip.add(translateTooltip("processingTime", time).withStyle(ChatFormatting.GRAY));
    }
}
