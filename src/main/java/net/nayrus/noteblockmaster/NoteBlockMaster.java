package net.nayrus.noteblockmaster;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.datagen.recipes.TunerRecipeSerializer;
import net.nayrus.noteblockmaster.util.Registry;
import net.nayrus.noteblockmaster.util.SubTickScheduler;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(NoteBlockMaster.MOD_ID)
public class NoteBlockMaster
{

    public static final String MOD_ID = "noteblockmaster";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID);
    public static final DeferredHolder<RecipeSerializer<?>, TunerRecipeSerializer> TUNER_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("tunerrecipe", () -> TunerRecipeSerializer.INSTANCE);


    public NoteBlockMaster(IEventBus modEventBus, ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.STARTUP, Config.SPEC);

        modEventBus.addListener(this::beforeModRegistry);

        Registry.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
    }

    private void beforeModRegistry (final NewRegistryEvent event)
    {
        SubTickScheduler.SUBTICK_LENGTH = Config.SUBTICK_LENGTH.get();
        AdvancedNoteBlock.MAX_SUBTICKS = (int) (100 / SubTickScheduler.SUBTICK_LENGTH);
        AdvancedNoteBlock.SUBTICK = IntegerProperty.create("subtick",0,AdvancedNoteBlock.MAX_SUBTICKS-1);
        AdvancedNoteBlock.OCTAVE = IntegerProperty.create("octave",2 - Config.ADDITIONAL_OCTAVES.get(),4 + Config.ADDITIONAL_OCTAVES.get());
        AdvancedNoteBlock.minNoteVal = AdvancedNoteBlock.noteStringAsInt(Config.LOWER_NOTE_LIMIT.get());
        AdvancedNoteBlock.maxNoteVal = AdvancedNoteBlock.noteStringAsInt(Config.HIGHER_NOTE_LIMIT.get());
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES){
            event.accept(Registry.TEMPOTUNER);
            event.accept(Registry.NOTETUNER);
        }
        if(event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS){
            event.accept(Registry.ADVANCED_NOTEBLOCK);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event)
    {
        SubTickScheduler.shutdown();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }

}
