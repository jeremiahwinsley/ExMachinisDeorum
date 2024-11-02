package net.permutated.exmachinis.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record IngredientStack(Ingredient ingredient, int count) {
    public static final Codec<IngredientStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Ingredient.CODEC_NONEMPTY.fieldOf(Constants.JSON.INGREDIENT).forGetter(IngredientStack::ingredient),
        ExtraCodecs.intRange(1, 99).fieldOf(Constants.JSON.COUNT).orElse(1).forGetter(IngredientStack::count)
    ).apply(instance, IngredientStack::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientStack> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, IngredientStack::ingredient,
        ByteBufCodecs.INT, IngredientStack::count,
        IngredientStack::new
    );

    public boolean test(ItemStack itemStack) {
        return itemStack.getCount() >= count && ingredient.test(itemStack);
    }

    public boolean test(Item item) {
        return ingredient.test(item.getDefaultInstance());
    }
}
