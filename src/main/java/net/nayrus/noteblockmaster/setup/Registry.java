package net.nayrus.noteblockmaster.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.block.TuningCore;
import net.nayrus.noteblockmaster.composer.*;
import net.nayrus.noteblockmaster.item.ComposersNote;
import net.nayrus.noteblockmaster.item.SpinningCore;
import net.nayrus.noteblockmaster.item.TunerItem;
import net.nayrus.noteblockmaster.network.data.ComposeData;
import net.nayrus.noteblockmaster.network.data.SongID;
import net.nayrus.noteblockmaster.network.data.TunerData;
import net.nayrus.noteblockmaster.render.item.CoreBaseRender;
import net.nayrus.noteblockmaster.render.item.SpinningCoreRender;
import net.nayrus.noteblockmaster.render.particle.SustainedNoteParticle;
import net.nayrus.noteblockmaster.render.particle.SustainedNoteType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static net.nayrus.noteblockmaster.NoteBlockMaster.MOD_ID;

public class Registry
{
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MOD_ID);
    //public static final DeferredRegister<AttachmentType<?>> DATA_ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MOD_ID);

    //Blocks
    public static final DeferredBlock<Block> ADVANCED_NOTEBLOCK = BLOCKS.register("advanced_noteblock", AdvancedNoteBlock::new);
    public static final DeferredBlock<Block> TUNINGCORE = BLOCKS.register("tuningcore", TuningCore::new);
    public static final DeferredBlock<Block> COMPOSER = BLOCKS.register("composer", ComposerBlock::new);
    public static final Map<DeferredBlock<Block>, DeferredItem<Item>> BLOCK_ITEMS;

    public static final Supplier<BlockEntityType<ComposerBlockEntity>> COMPOSER_BE = BLOCK_ENTITIES.register("composer_be",
            () -> new BlockEntityType<>(ComposerBlockEntity::new, COMPOSER.get()));

    //Items
    public static final DeferredItem<Item> TEMPOTUNER = ITEMS.register("tempotuner", TunerItem::new);
    public static final DeferredItem<Item> NOTETUNER = ITEMS.register("notetuner", TunerItem::new);
    public static final DeferredItem<Item> COMPOSITION = ITEMS.register("composition", ComposersNote::new);
    public static final DeferredItem<Item> CORE = ITEMS.register("core", () -> new Item(new Item.Properties()
            .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "core")))));
    public static final DeferredItem<Item> SUSTAIN = ITEMS.register("sustain", SpinningCore::new);
    public static final DeferredItem<Item> VOLUME = ITEMS.register("volume", SpinningCore::new);

    //Data
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TunerData>> TUNER_DATA = DATA_COMPONENT_TYPES.registerComponentType("tuner_data",
            builder -> builder.persistent(TunerData.TUNER_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ComposeData>> COMPOSE_DATA = DATA_COMPONENT_TYPES.registerComponentType("compose_data",
            builder -> builder.persistent(ComposeData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SongID>> SONG_ID = DATA_COMPONENT_TYPES.registerComponentType("song_id",
            builder -> builder.persistent(SongID.CODEC));

    //Particle
    public static final DeferredHolder<ParticleType<?>, SustainedNoteType> SUSTAINED_NOTE = PARTICLE_TYPES.register("sustained_note", () -> new SustainedNoteType(false));
    //Menu
    public static final Supplier<MenuType<ComposerContainer>> COMPOSER_MENU = MENU_TYPES.register("composer_menu", () -> new MenuType<>(ComposerContainer::new, FeatureFlags.DEFAULT_FLAGS));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        ITEMS.register(eventBus);
        CREATIVE_MODE_TABS.register(eventBus);
        DATA_COMPONENT_TYPES.register(eventBus);
        //DATA_ATTACHMENT_TYPES.register(eventBus);
        PARTICLE_TYPES.register(eventBus);
        MENU_TYPES.register(eventBus);
    }

    static{
        Map<DeferredBlock<Block>,DeferredItem<Item>> block_items = new HashMap<>();
        block_items.put(ADVANCED_NOTEBLOCK, createBlockItem(ADVANCED_NOTEBLOCK));
        //block_items.put(COMPOSER, createBlockItem(COMPOSER));
        BLOCK_ITEMS = block_items;

        CREATIVE_MODE_TABS.register("noteblockmaster", ()-> CreativeModeTab.builder()
                .title(Component.literal("Note Block Master"))
                .icon(() -> new ItemStack(ADVANCED_NOTEBLOCK.asItem()))
                .displayItems((pars, output) -> ITEMS.getEntries()
                        .forEach(item -> output.accept(item.get())))
                .build());
    }

    public static DeferredItem<Item> createBlockItem(DeferredBlock<Block> block){
        return Registry.ITEMS.register(block.getId().getPath(),
                ()-> new BlockItem(block.get(), new Item.Properties()
                        .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, block.getId().getPath())))));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new SpinningCoreRender.Extension(), VOLUME, SUSTAIN);
        event.registerItem(new CoreBaseRender.Extension(), CORE);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(COMPOSER_BE.get(), context -> new ComposerRenderer());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event){
        event.register(COMPOSER_MENU.get(), ComposerScreen::new);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        if(!(Minecraft.getInstance().particleEngine instanceof ISpriteAccessor vanillaSprites)) return;

        event.registerSpecial(SUSTAINED_NOTE.get(), new SustainedNoteParticle.Provider(vanillaSprites.nbm$getRegisteredSprite(ParticleTypes.NOTE)));
    }
}
