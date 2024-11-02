package net.permutated.exmachinis.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.permutated.exmachinis.ExMachinis;

public class ResourceUtil {
    private ResourceUtil() {
        // nothing to do
    }

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(ExMachinis.MODID, path);
    }

    public static String id(String path) {
        return prefix(path).toString();
    }

    public static ResourceLocation common(String path) {
        return ResourceLocation.fromNamespaceAndPath("c", path);
    }

    public static TagKey<Item> tag(String path) {
        return ItemTags.create(ResourceLocation.parse(path));
    }

    public static TagKey<Block> blockTag(String path) {
        return BlockTags.create(ResourceLocation.parse(path));
    }

    public static ResourceLocation block(String path) {
        return prefix(String.format("block/%s", path));
    }

    public static ResourceLocation gui(String path) {
        return prefix(String.format("textures/gui/%s.png", path));
    }

    public static ResourceLocation jei(String path) {
        return prefix(String.format("textures/jei/%s.png", path));
    }
}
