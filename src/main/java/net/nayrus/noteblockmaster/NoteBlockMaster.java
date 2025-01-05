package net.nayrus.noteblockmaster;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.command.BPMInfoCommand;
import net.nayrus.noteblockmaster.command.MainCommand;
import net.nayrus.noteblockmaster.datagen.recipes.TunerRecipeSerializer;
import net.nayrus.noteblockmaster.event.ClientEvents;
import net.nayrus.noteblockmaster.event.ServerEvents;
import net.nayrus.noteblockmaster.network.PacketHandler;
import net.nayrus.noteblockmaster.utils.Registry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.server.command.ConfigCommand;
import org.slf4j.Logger;

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
        modContainer.registerConfig(ModConfig.Type.STARTUP, Config.START_UP);

        modEventBus.addListener(AdvancedNoteBlock::loadPropertiesFromConfig);
        NeoForge.EVENT_BUS.register(this);

        Registry.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);

        modEventBus.addListener(PacketHandler::registerNetwork);

        if(FMLEnvironment.dist == Dist.CLIENT){
            NeoForge.EVENT_BUS.register(ClientEvents.class);
            modEventBus.addListener(this::addCreative);
        }else{
            NeoForge.EVENT_BUS.register(ServerEvents.class);
        }
    }

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

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event){
        new BPMInfoCommand(event.getDispatcher());
        MainCommand.saveConfigCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }
}
