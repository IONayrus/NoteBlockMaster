package net.nayrus.noteblockmaster;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.block.TuningCore;
import net.nayrus.noteblockmaster.command.BPMInfoCommand;
import net.nayrus.noteblockmaster.command.MainCommand;
import net.nayrus.noteblockmaster.datagen.recipes.TunerRecipeSerializer;
import net.nayrus.noteblockmaster.event.ClientEvents;
import net.nayrus.noteblockmaster.event.CommonEvents;
import net.nayrus.noteblockmaster.event.ServerEvents;
import net.nayrus.noteblockmaster.network.PacketHandler;
import net.nayrus.noteblockmaster.setup.config.ClientConfig;
import net.nayrus.noteblockmaster.setup.config.StartupConfig;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.sound.SoundRegistry;
import net.nayrus.noteblockmaster.utils.KeyBindings;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.server.command.ConfigCommand;
import org.slf4j.Logger;

@Mod(NoteBlockMaster.MOD_ID)
public class NoteBlockMaster
{
    public static final String MOD_ID = "noteblockmaster";
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID);
    public static final DeferredHolder<RecipeSerializer<?>, TunerRecipeSerializer> TUNER_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("tunerrecipe", () -> TunerRecipeSerializer.INSTANCE);

    public NoteBlockMaster(IEventBus modEventBus, ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.STARTUP, StartupConfig.START_UP);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT);
        SoundRegistry.SOUND_EVENTS.register(modEventBus);
        TuningCore.loadSustainProperty();

        modEventBus.addListener(AdvancedNoteBlock::loadPropertiesFromConfig);
        NeoForge.EVENT_BUS.register(this);

        Registry.register(modEventBus);
        modEventBus.addListener(Registry::registerClientExtensions);
        modEventBus.addListener(Registry::registerParticles);
        RECIPE_SERIALIZERS.register(modEventBus);

        modEventBus.addListener(PacketHandler::registerNetwork);
        modEventBus.addListener(KeyBindings::registerBindings);

        if(FMLEnvironment.dist == Dist.CLIENT){
            NeoForge.EVENT_BUS.register(ClientEvents.class);
        }else{
            NeoForge.EVENT_BUS.register(ServerEvents.class);
        }
        NeoForge.EVENT_BUS.register(CommonEvents.class);
    }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event){
        new BPMInfoCommand(event.getDispatcher());
        MainCommand.mainCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }
}
