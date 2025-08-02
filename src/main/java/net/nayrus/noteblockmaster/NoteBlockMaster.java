package net.nayrus.noteblockmaster;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.composer.SongCache;
import net.nayrus.noteblockmaster.composer.SongFileManager;
import net.nayrus.noteblockmaster.datagen.DataGenerators;
import net.nayrus.noteblockmaster.datagen.recipes.TunerRecipe;
import net.nayrus.noteblockmaster.event.ClientEvents;
import net.nayrus.noteblockmaster.event.CommonEvents;
import net.nayrus.noteblockmaster.event.ServerEvents;
import net.nayrus.noteblockmaster.network.PacketHandler;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.setup.config.ClientConfig;
import net.nayrus.noteblockmaster.setup.config.StartupConfig;
import net.nayrus.noteblockmaster.sound.SoundRegistry;
import net.nayrus.noteblockmaster.utils.KeyBindings;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mod(NoteBlockMaster.MOD_ID)
public class NoteBlockMaster
{
    public static final String MOD_ID = "noteblockmaster";
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID);
    public static final DeferredHolder<RecipeSerializer<?>, TunerRecipe.Serializer> TUNER_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("tunerrecipe", TunerRecipe.Serializer::new);

    public NoteBlockMaster(IEventBus modEventBus, ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.STARTUP, StartupConfig.START_UP);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT);
        modEventBus.addListener(PacketHandler::registerNetwork);
        modEventBus.addListener(this::onFMLCommonSetup);

        if(FMLEnvironment.dist == Dist.CLIENT){
            modEventBus.addListener(DataGenerators::gatherData);

            modEventBus.addListener(Registry::registerEntityRenderers);
            modEventBus.addListener(Registry::registerParticles);
            modEventBus.addListener(Registry::registerClientExtensions);
            modEventBus.addListener(Registry::registerMenuScreens);

            NeoForge.EVENT_BUS.register(ClientEvents.class);
            modEventBus.addListener(this::onFMLClientSetup);
            modEventBus.addListener(KeyBindings::registerBindings);
            SongCache.CLIENT_CACHE = new SongCache(true);
        }else{
            NeoForge.EVENT_BUS.register(ServerEvents.class);
        }
        NeoForge.EVENT_BUS.register(CommonEvents.class);
        SoundRegistry.SOUND_EVENTS.register(modEventBus);

        modEventBus.addListener(AdvancedNoteBlock::loadPropertiesFromConfig);
        Registry.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
    }

    public void onFMLClientSetup(FMLClientSetupEvent event){
        Path songDir = Paths.get(System.getProperty("user.dir"), "mods\\noteblockmaster\\songs");

        try {
            if (!Files.exists(songDir)) Files.createDirectories(songDir);
        }catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }

        SongFileManager.SONG_DIR = songDir;
    }

    public void onFMLCommonSetup(FMLCommonSetupEvent event){
        Path cacheDir = Paths.get(System.getProperty("user.dir"), "mods\\noteblockmaster\\song_cache");
        try {
            if (!Files.exists(cacheDir)) Files.createDirectories(cacheDir);
        }catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        SongFileManager.CACHE_DIR = cacheDir;
    }
}
