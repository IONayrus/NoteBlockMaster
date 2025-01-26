package net.nayrus.noteblockmaster.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.block.TuningCore;
import net.nayrus.noteblockmaster.item.TunerItem;
import net.nayrus.noteblockmaster.network.data.ComposeData;
import net.nayrus.noteblockmaster.network.data.TunerData;
import net.nayrus.noteblockmaster.network.payload.ActionPing;
import net.nayrus.noteblockmaster.network.payload.ConfigCheck;
import net.nayrus.noteblockmaster.network.payload.CoreUpdate;
import net.nayrus.noteblockmaster.network.payload.TickSchedule;
import net.nayrus.noteblockmaster.render.ANBInfoRender;
import net.nayrus.noteblockmaster.setup.config.ClientConfig;
import net.nayrus.noteblockmaster.setup.config.StartupConfig;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.utils.FinalTuple;
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
        reg.playToServer(ComposeData.TYPE, ComposeData.STREAM_CODEC, PacketHandler::handleComposeData);
        reg.playToServer(TickSchedule.TYPE, TickSchedule.STREAM_CODEC, PacketHandler::handleTickSchedule);
        reg.playToServer(CoreUpdate.TYPE, CoreUpdate.STREAM_CODEC, PacketHandler::handleCoreUpdate);
    }

    private static void handleStartUpSync(final ConfigCheck packet, final IPayloadContext context) {
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

    private static void handleActionPing(final ActionPing packet, final IPayloadContext context){
        switch(ActionPing.Action.values()[packet.action()]){
            case SAVE_STARTUP_CONFIG -> {
                if(!StartupConfig.UPDATED) {
                    StartupConfig.updateStartUpAndSave();
                    context.player().sendSystemMessage(Component.literal("Updated local configs. Restart your client to apply.")
                            .withColor(Color.GREEN.darker().getRGB()));
                }
            }
            case ACTIVATE_LOW_RES_RENDER -> {
                if(ClientConfig.LOW_RESOLUTION_RENDER.isFalse()) {
                    ClientConfig.LOW_RESOLUTION_RENDER.set(true);
                    context.player().sendSystemMessage(Component.literal("Activated low resolution render to save fps"));
                    ClientConfig.CLIENT.save();
                }
            }
            case DEACTIVATE_LOW_RES_RENDER -> {
                if(ClientConfig.LOW_RESOLUTION_RENDER.isTrue()){
                    ClientConfig.LOW_RESOLUTION_RENDER.set(false);
                    context.player().sendSystemMessage(Component.literal("Low resolution render deactivated"));
                    ClientConfig.CLIENT.save();
                }
            }
        }
    }

    private static void handleTunerData(final TunerData data, final IPayloadContext context){
        Player player = context.player();
        FinalTuple.ItemStackTuple items = FinalTuple.getHeldItems(player);
        if(!(items.contains(TunerItem.class))) return;
        ItemStack stack = items.getFirst(TunerItem.class);
        stack.set(Registry.TUNER_DATA, data);
    }

    private static void handleComposeData(final ComposeData data, final IPayloadContext context) {
        Player player = context.player();
        FinalTuple.ItemStackTuple items = FinalTuple.getHeldItems(player);
        if(!(items.contains(Registry.COMPOSER.get()))) return;
        ItemStack stack = items.getFirst(Registry.COMPOSER.get());
        stack.set(Registry.COMPOSE_DATA, data);
    }

    public static void handleTickSchedule(final TickSchedule tickSchedule, final IPayloadContext context) {
        ServerLevel level = (ServerLevel) context.player().level();
        BlockPos pos = tickSchedule.pos();
        Utils.scheduleTick(level, pos, level.getBlockState(pos).getBlock(), tickSchedule.delay());
    }

    public static void handleCoreUpdate(final CoreUpdate coreUpdate, final IPayloadContext context) {
        Level level = context.player().level();
        BlockPos pos = coreUpdate.pos();
        level.setBlockAndUpdate(pos, level.getBlockState(pos)
                .setValue(TuningCore.VOLUME, coreUpdate.volume())
                .setValue(TuningCore.SUSTAIN, coreUpdate.sustain()));
    }
}
