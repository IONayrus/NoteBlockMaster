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

        public static final TagKey<Block> NBM_BLOCKS = createTag("nbmaster_noteblock");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> TUNERS = createTag("noteblocktuners");
        public static final TagKey<Item> CORES = createTag("noteblockcores");
        public static final TagKey<Item> CORE_DESTROY = createTag("core_destroy");


        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, name));
        }
    }
}
