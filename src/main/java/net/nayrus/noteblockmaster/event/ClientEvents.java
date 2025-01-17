package net.nayrus.noteblockmaster.event;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.nayrus.noteblockmaster.network.data.ComposeData;
import net.nayrus.noteblockmaster.render.ANBInfoRender;
import net.nayrus.noteblockmaster.render.CoreRender;
import net.nayrus.noteblockmaster.render.RenderUtils;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.sound.SubTickScheduler;
import net.nayrus.noteblockmaster.sound.SustainingSound;
import net.nayrus.noteblockmaster.utils.FinalTuple;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundSourceEvent;

import java.awt.*;

public class ClientEvents {

    @SubscribeEvent
    public static void renderBlockOverlays(RenderLevelStageEvent e){
        RenderUtils.CURRENT_CAM_POS = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        Player player = Minecraft.getInstance().player;
        if(player == null) return;
        Level level = player.level();

        if(e.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            FinalTuple.ItemStackTuple items = FinalTuple.getHeldItems(player);

            if (items.contains(Registry.NOTETUNER.get()))
                ANBInfoRender.renderNoteBlockInfo(e, level, Utils.PROPERTY.NOTE);
            else if (items.contains(Registry.TEMPOTUNER.get()))
                ANBInfoRender.renderNoteBlockInfo(e, level, Utils.PROPERTY.TEMPO);
            if (items.contains(Registry.COMPOSER.get())) {
                ItemStack composer = items.getFirst(Registry.COMPOSER.get());
                ComposeData cData = ComposeData.getComposeData(composer);
                player.displayClientMessage(Component.literal("Repeater delay: " + cData.preDelay()).withColor(Color.RED.darker().getRGB()), true);
            }
        }

        CoreRender.renderCoresInRange(e, level, 20);
    }

    @SubscribeEvent
    public static void playSoundSourceEvent(PlaySoundSourceEvent e){
        if(!(e.getSound() instanceof SustainingSound sound)) return;
        sound.addNoteParticle();
        SubTickScheduler.scheduleStop(sound);
    }

}
