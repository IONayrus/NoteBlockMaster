package net.nayrus.noteblockmaster.event;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.nayrus.noteblockmaster.item.TunerItem;
import net.nayrus.noteblockmaster.network.data.ComposeData;
import net.nayrus.noteblockmaster.render.ANBInfoRender;
import net.nayrus.noteblockmaster.render.CoreRender;
import net.nayrus.noteblockmaster.render.utils.RenderUtils;
import net.nayrus.noteblockmaster.screen.ComposerScreen;
import net.nayrus.noteblockmaster.setup.NBMTags;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.sound.CoreSound;
import net.nayrus.noteblockmaster.sound.SubTickScheduler;
import net.nayrus.noteblockmaster.utils.FinalTuple;
import net.nayrus.noteblockmaster.utils.KeyBindings;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundSourceEvent;

import java.awt.*;

public class ClientEvents {

    public static long ticks = 0;
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (KeyBindings.OPEN_OFFHAND_GUI.get().consumeClick()) {
            if(!(Minecraft.getInstance().player instanceof Player player)) return;
            ItemStack off = player.getOffhandItem();
            if(off.is(NBMTags.Items.TUNERS)) TunerItem.openTunerGUI(off, player.getMainHandItem(), true);
            if(off.is(Registry.COMPOSER)) Minecraft.getInstance().setScreen(new ComposerScreen(off));
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

            if (items.contains(Registry.COMPOSER.get())) {
                ItemStack composer = items.getFirst(Registry.COMPOSER.get());
                ComposeData cData = ComposeData.getComposeData(composer);
                player.displayClientMessage(Component.literal("Repeater delay: " + cData.preDelay()).withColor(Color.RED.darker().getRGB()), true);
            }
        }
        CoreRender.renderCoresInRange(e, level);
    }

    @SubscribeEvent
    public static void playSoundSourceEvent(PlaySoundSourceEvent e){
        if(!(e.getSound() instanceof CoreSound sound)) return;
        SubTickScheduler.SUSTAINED_SOUNDS.put(sound.getImmutablePos(), sound);
        sound.addNoteParticle();
        sound.setChannel(e.getChannel());
    }

}
