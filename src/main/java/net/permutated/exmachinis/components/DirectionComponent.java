package net.permutated.exmachinis.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.permutated.exmachinis.util.Constants;

public record DirectionComponent(Direction direction) {
    public static Codec<DirectionComponent> BASIC_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Direction.CODEC.fieldOf(Constants.NBT.DIRECTION).forGetter(DirectionComponent::direction)
    ).apply(instance, DirectionComponent::new));
}
