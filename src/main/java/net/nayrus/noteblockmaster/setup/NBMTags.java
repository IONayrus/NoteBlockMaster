package net.nayrus.noteblockmaster.setup;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.nayrus.noteblockmaster.NoteBlockMaster;

public class NBMTags {

    public static class Blocks {
        public static final TagKey<Block> NBM_BLOCKS = BlockTags.create(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "nbmaster_noteblock"));
    }

    public static class Items {
        public static final TagKey<Item> TUNERS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "noteblocktuners"));
        public static final TagKey<Item> CORES = ItemTags.create(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "noteblockcores"));
        public static final TagKey<Item> CORE_DESTROY = ItemTags.create(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "core_destroy"));
    }
}
