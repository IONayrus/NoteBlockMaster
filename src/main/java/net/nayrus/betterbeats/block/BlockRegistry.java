package net.nayrus.betterbeats.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.nayrus.betterbeats.BetterBeats;
import net.nayrus.betterbeats.item.ItemRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockRegistry
{
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BetterBeats.MOD_ID);

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block)
    {
        DeferredBlock<T> regBlock = BLOCKS.register(name, block);
        registerBlockItem(name, regBlock);
        return regBlock;
    }

    public static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block)
    {
        ItemRegistry.ITEMS.register(name, ()-> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
    }

    public static final DeferredBlock<Block>  ADVANCED_NOTEBLOCK = registerBlock("advanced_noteblock",
            () -> new AdvancedNoteblock(BlockBehaviour.Properties.ofFullCopy(Blocks.NOTE_BLOCK)));

}
