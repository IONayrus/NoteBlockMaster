package net.nayrus.noteblockmaster.event;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.nayrus.noteblockmaster.composer.ComposerRenderer;
import net.nayrus.noteblockmaster.composer.SongCache;
import net.nayrus.noteblockmaster.composer.SongData;
import net.nayrus.noteblockmaster.item.TunerItem;
import net.nayrus.noteblockmaster.network.data.ComposeData;
import net.nayrus.noteblockmaster.network.data.SongID;
import net.nayrus.noteblockmaster.render.ANBInfoRender;
import net.nayrus.noteblockmaster.render.CoreRender;
import net.nayrus.noteblockmaster.render.utils.RenderUtils;
import net.nayrus.noteblockmaster.screen.CompositionScreen;
import net.nayrus.noteblockmaster.setup.NBMTags;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.sound.CoreSound;
import net.nayrus.noteblockmaster.sound.SubTickScheduler;
import net.nayrus.noteblockmaster.utils.FinalTuple;
import net.nayrus.noteblockmaster.utils.KeyBindings;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundSourceEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.awt.*;
import java.util.UUID;

public class ClientEvents {

    public static long ticks = 0;
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (KeyBindings.OPEN_OFFHAND_GUI.get().consumeClick()) {
            if(!(Minecraft.getInstance().player instanceof Player player)) return;
            ItemStack off = player.getOffhandItem();
            if(off.is(NBMTags.Items.TUNERS)) TunerItem.openTunerGUI(off, player.getMainHandItem(), true);
            if(off.is(Registry.COMPOSITION)) Minecraft.getInstance().setScreen(new CompositionScreen(off));
        }
        if(ticks%200==0) CoreRender.clearMaps();
        ticks++;
    }


    @SubscribeEvent
    public static void renderBlockOverlays(RenderLevelStageEvent e){
        if(!(RenderUtils.eventOnRelevantStage(e))) return;
        if(!(Minecraft.getInstance().player instanceof Player player)) return;

        RenderUtils.CURRENT_CAM_POS = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Level level = player.level();

        if(e.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            FinalTuple.ItemStackTuple items = FinalTuple.getHeldItems(player);

            if (items.getA().is(NBMTags.Items.TUNERS))
                ANBInfoRender.renderNoteBlockInfo(e, level, items.getA().is(Registry.NOTETUNER) ? Utils.PROPERTY.NOTE : Utils.PROPERTY.TEMPO);
            else if (items.getB().is(NBMTags.Items.TUNERS))
                ANBInfoRender.renderNoteBlockInfo(e, level, items.getB().is(Registry.NOTETUNER) ? Utils.PROPERTY.NOTE : Utils.PROPERTY.TEMPO);
        }
        CoreRender.renderCoresInRange(e, level);
    }

    @SubscribeEvent
    public static void renderGUIOverlays(RenderGuiEvent.Post event){
        if(!(Minecraft.getInstance().player instanceof Player player)) return;
        FinalTuple.ItemStackTuple items = FinalTuple.getHeldItems(player);
        if (!items.contains(Registry.COMPOSITION.get())) return;

        ItemStack composer_note = items.getFirst(Registry.COMPOSITION.get());
        ComposeData cData = ComposeData.getComposeData(composer_note);
        player.displayClientMessage(Component.literal("Next repeater delay: " + cData.postDelay()).withColor(Color.RED.darker().getRGB()), true);
        if(composer_note.get(Registry.SONG_ID) instanceof SongID(UUID songID)){
            SongData data = SongCache.getSong(songID, composer_note);
            if(data!= null){
                ComposerRenderer.renderScreenOverlay(event.getGuiGraphics(), data.getNotesAt(cData.beat()), cData);
            }
        }
    }

    @SubscribeEvent
    public static void playSoundSourceEvent(PlaySoundSourceEvent e){
        if(!(e.getSound() instanceof CoreSound sound)) return;
        sound.addNoteParticle();
        sound.setChannel(e.getChannel());
        SubTickScheduler.SUSTAINED_SOUNDS.put(sound.getImmutablePos(), sound);
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent e){
        RenderUtils.clearCache();
    }

}
