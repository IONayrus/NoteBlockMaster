package net.nayrus.noteblockmaster.util;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.item.Tuner;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;

public class Registry
{
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NoteBlockMaster.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NoteBlockMaster.MOD_ID);

    public static final DeferredBlock<Block> ADVANCED_NOTEBLOCK = BLOCKS.register("advanced_noteblock",
            () -> new AdvancedNoteBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NOTE_BLOCK)));

    public static final DeferredItem<Item> ADVANCED_NOTEBLOCK_ITEM = ITEMS.register("advanced_noteblock",
            ()-> new BlockItem(ADVANCED_NOTEBLOCK.get(), new Item.Properties()));

    public static final DeferredItem<Item> TEMPOTUNER = ITEMS.register("tempotuner",
            () -> new Tuner(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> NOTETUNER = ITEMS.register("notetuner",
            () -> new Tuner(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}