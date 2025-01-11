package net.nayrus.noteblockmaster.setup;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.item.ComposersNote;
import net.nayrus.noteblockmaster.network.data.ComposeData;
import net.nayrus.noteblockmaster.network.data.TunerData;
import net.nayrus.noteblockmaster.item.TunerItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.nayrus.noteblockmaster.NoteBlockMaster.MOD_ID;

import java.util.function.Supplier;

public class Registry
{
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, MOD_ID);

    public static final DeferredBlock<Block> ADVANCED_NOTEBLOCK = Registry.BLOCKS.register("advanced_noteblock",
            () -> new AdvancedNoteBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NOTE_BLOCK)));

    public static final DeferredItem<Item> TEMPOTUNER = ITEMS.register("tempotuner", TunerItem::new);
    public static final DeferredItem<Item> NOTETUNER = ITEMS.register("notetuner", TunerItem::new);
    public static final DeferredItem<Item> COMPOSER = ITEMS.register("composer", ComposersNote::new);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TunerData>> TUNER_DATA = DATA_COMPONENT_TYPES.registerComponentType("tuner_data",
            builder -> builder.persistent(TunerData.TUNER_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ComposeData>> COMPOSE_DATA = DATA_COMPONENT_TYPES.registerComponentType("compose_data",
            builder -> builder.persistent(ComposeData.TUNER_CODEC));

    public static final Supplier<SoundEvent> SMITHING = SOUND_EVENTS.register("noteblock_smithing", () ->
        SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID,"noteblock_smithing"))
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        CREATIVE_MODE_TABS.register(eventBus);
        DATA_COMPONENT_TYPES.register(eventBus);
        SOUND_EVENTS.register(eventBus);
    }

    static{
        Registry.ITEMS.register("advanced_noteblock",
                ()-> new BlockItem(Registry.ADVANCED_NOTEBLOCK.get(), new Item.Properties()));

        CREATIVE_MODE_TABS.register("noteblockmaster", ()-> CreativeModeTab.builder()
                .title(Component.literal("Note Block Master"))
                .icon(() -> new ItemStack(ADVANCED_NOTEBLOCK.asItem()))
                .displayItems((pars, output) -> ITEMS.getEntries()
                        .forEach(item -> output.accept(item.get())))
                .build());
    }
}
