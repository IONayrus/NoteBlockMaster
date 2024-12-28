package net.nayrus.betterbeats.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.nayrus.betterbeats.BetterBeats;

public class BetterTags {
    public static class Blocks {

        public static final TagKey<Block> BETTER_BLOCKS = createTag("betterbeats_block");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(BetterBeats.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> BEATWAKERS = createTag("beatwakers");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(BetterBeats.MOD_ID, name));
        }
    }
}
