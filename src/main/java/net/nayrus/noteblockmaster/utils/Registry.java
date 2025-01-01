package net.nayrus.noteblockmaster.utils;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.item.TunerItem;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;

public class Registry
{
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NoteBlockMaster.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NoteBlockMaster.MOD_ID);

    public static final DeferredBlock<Block> ADVANCED_NOTEBLOCK = Registry.BLOCKS.register("advanced_noteblock",
            () -> new AdvancedNoteBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NOTE_BLOCK).noOcclusion()));
    public static final DeferredItem<BlockItem> ADVANCED_NOTEBLOCK_ITEM = Registry.ITEMS.register("advanced_noteblock",
            ()-> new BlockItem(Registry.ADVANCED_NOTEBLOCK.get(), new Item.Properties()));


    public static final DeferredItem<Item> TEMPOTUNER = ITEMS.register("tempotuner",
            () -> new TunerItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> NOTETUNER = ITEMS.register("notetuner",
            () -> new TunerItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
