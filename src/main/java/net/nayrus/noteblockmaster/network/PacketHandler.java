package net.nayrus.noteblockmaster.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.nayrus.noteblockmaster.setup.Config;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.item.TunerItem;
import net.nayrus.noteblockmaster.network.payload.ActionPing;
import net.nayrus.noteblockmaster.network.payload.ConfigCheck;
import net.nayrus.noteblockmaster.network.data.TunerData;
import net.nayrus.noteblockmaster.render.ANBInfoRender;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.awt.*;

public class PacketHandler {
    @SubscribeEvent
    public static void registerNetwork(final RegisterPayloadHandlersEvent event){
        final PayloadRegistrar reg = event.registrar(NoteBlockMaster.MOD_ID);

        reg.playToClient(ConfigCheck.TYPE, ConfigCheck.STREAM_CODEC, PacketHandler::handleStartUpSync);
        reg.playToClient(ActionPing.TYPE, ActionPing.STREAM_CODEC, PacketHandler::handleActionPing);

        reg.playToServer(TunerData.TYPE, TunerData.TUNER_STREAM_CODEC, PacketHandler::handleTunerData);
    }

    public static void handleStartUpSync(final ConfigCheck packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if(AdvancedNoteBlock.MIN_NOTE_VAL != packet.minNote() || AdvancedNoteBlock.MAX_NOTE_VAL != packet.maxNote()){
                ANBInfoRender.NOTE_OFF_SYNC = true;
                AdvancedNoteBlock.MIN_NOTE_VAL = packet.minNote();
                AdvancedNoteBlock.MAX_NOTE_VAL = packet.maxNote();
                AdvancedNoteBlock.TOTAL_NOTES = AdvancedNoteBlock.MAX_NOTE_VAL - AdvancedNoteBlock.MIN_NOTE_VAL;
            }
            if(AdvancedNoteBlock.SUBTICK_LENGTH != packet.subtickLength()){
                ANBInfoRender.SUBTICK_OFF_SYNC = true;
                AdvancedNoteBlock.SUBTICK_LENGTH = packet.subtickLength();
                AdvancedNoteBlock.SUBTICKS = (byte) (100.0F / AdvancedNoteBlock.SUBTICK_LENGTH);
            }
            if(ANBInfoRender.SUBTICK_OFF_SYNC || ANBInfoRender.NOTE_OFF_SYNC) Utils.sendDesyncWarning(context.player());
        });
    }

    public static void handleActionPing(final ActionPing packet, final IPayloadContext context){
        switch(ActionPing.Action.values()[packet.action()]){
            case SAVE_STARTUP_CONFIG -> {
                if(!Config.UPDATED) {
                    Config.updateStartUpAndSave();
                    context.player().sendSystemMessage(Component.literal("Updated local configs. Restart your client to apply.")
                            .withColor(Color.GREEN.darker().getRGB()));
                }
            }
            case GOLD_BREAK -> {
                if(Minecraft.getInstance().level instanceof ClientLevel level)
                    level.addDestroyBlockEffect(((BlockHitResult)context.player().pick(8, 0, false)).getBlockPos(), Blocks.GOLD_BLOCK.defaultBlockState());
            }
            case SWING_OFFHAND -> context.player().swing(InteractionHand.OFF_HAND, true);
        }
    }

    public static void handleTunerData(final TunerData data, final IPayloadContext context){
        Player player = context.player();
        if(!(player.getMainHandItem().getItem() instanceof TunerItem)) return;
        ItemStack stack = player.getMainHandItem();
        stack.set(Registry.TUNER_DATA, data);
    }
}
