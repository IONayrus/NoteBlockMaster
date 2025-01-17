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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.block.TuningCore;
import net.nayrus.noteblockmaster.item.AnimatedCore;
import net.nayrus.noteblockmaster.item.ComposersNote;
import net.nayrus.noteblockmaster.item.TunerItem;
import net.nayrus.noteblockmaster.network.data.ComposeData;
import net.nayrus.noteblockmaster.network.data.TunerData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static net.nayrus.noteblockmaster.NoteBlockMaster.MOD_ID;

public class Registry
{
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, MOD_ID);


    public static final DeferredBlock<Block> ADVANCED_NOTEBLOCK = Registry.BLOCKS.register("advanced_noteblock",
            () -> new AdvancedNoteBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NOTE_BLOCK)));
    public static final DeferredBlock<Block> TUNINGCORE = Registry.BLOCKS.register("tuningcore", TuningCore::new);

    public static final DeferredItem<Item> TEMPOTUNER = ITEMS.register("tempotuner", TunerItem::new);
    public static final DeferredItem<Item> NOTETUNER = ITEMS.register("notetuner", TunerItem::new);
    public static final DeferredItem<Item> COMPOSER = ITEMS.register("composer", ComposersNote::new);
    public static final DeferredItem<Item> CORE = ITEMS.register("core", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SUSTAIN = ITEMS.register("sustain", AnimatedCore::new);
    public static final DeferredItem<Item> VOLUME = ITEMS.register("volume", AnimatedCore::new);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TunerData>> TUNER_DATA = DATA_COMPONENT_TYPES.registerComponentType("tuner_data",
            builder -> builder.persistent(TunerData.TUNER_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ComposeData>> COMPOSE_DATA = DATA_COMPONENT_TYPES.registerComponentType("compose_data",
            builder -> builder.persistent(ComposeData.CODEC));

    public static final Supplier<SoundEvent> SMITHING = SOUND_EVENTS.register("noteblock_smithing", () ->
        SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID,"noteblock_smithing")));
    public static final Supplier<SoundEvent> SUSTAINED_PLING_SOUND = SOUND_EVENTS.register("sustained_pling", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_pling")));
    public static final Supplier<SoundEvent> SUSTAINED_BANJO_SOUND = SOUND_EVENTS.register("sustained_banjo", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_banjo")));
    public static final Supplier<SoundEvent> SUSTAINED_BIT_SOUND = SOUND_EVENTS.register("sustained_bit", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_bit")));
    public static final Supplier<SoundEvent> SUSTAINED_DIDGERIDOO_SOUND = SOUND_EVENTS.register("sustained_didgeridoo", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_didgeridoo")));
    public static final Supplier<SoundEvent> SUSTAINED_COW_BELL_SOUND = SOUND_EVENTS.register("sustained_cowbell", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_cowbell")));
    public static final Supplier<SoundEvent> SUSTAINED_IRON_XYLOPHONE_SOUND = SOUND_EVENTS.register("sustained_ironxylophone", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_ironxylophone")));
    public static final Supplier<SoundEvent> SUSTAINED_XYLOPHONE_SOUND = SOUND_EVENTS.register("sustained_xylophone", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_xylophone")));
    public static final Supplier<SoundEvent> SUSTAINED_CHIME_SOUND = SOUND_EVENTS.register("sustained_chime", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_chime")));
    public static final Supplier<SoundEvent> SUSTAINED_GUITAR_SOUND = SOUND_EVENTS.register("sustained_guitar", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_guitar")));
    public static final Supplier<SoundEvent> SUSTAINED_BELL_SOUND = SOUND_EVENTS.register("sustained_bell", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_bell")));
    public static final Supplier<SoundEvent> SUSTAINED_FLUTE_SOUND = SOUND_EVENTS.register("sustained_flute", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_flute")));
    public static final Supplier<SoundEvent> SUSTAINED_BASS_SOUND = SOUND_EVENTS.register("sustained_bass", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_bass")));
    public static final Supplier<SoundEvent> SUSTAINED_HAT_SOUND = SOUND_EVENTS.register("sustained_hat", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_hat")));
    public static final Supplier<SoundEvent> SUSTAINED_SNARE_SOUND = SOUND_EVENTS.register("sustained_snare", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_snare")));
    public static final Supplier<SoundEvent> SUSTAINED_BASEDRUM_SOUND = SOUND_EVENTS.register("sustained_basedrum", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_basedrum")));
    public static final Supplier<SoundEvent> SUSTAINED_HARP_SOUND = SOUND_EVENTS.register("sustained_harp", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_harp")));


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
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
