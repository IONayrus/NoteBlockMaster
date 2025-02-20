package net.nayrus.noteblockmaster.event;

import net.minecraft.server.level.ServerPlayer;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.composer.SongCache;
import net.nayrus.noteblockmaster.network.payload.ConfigCheck;
import net.nayrus.noteblockmaster.sound.SubTickScheduler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class ServerEvents {



    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event){
        SubTickScheduler.executor.shutdown();
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new ConfigCheck(
                (byte) AdvancedNoteBlock.MIN_NOTE_VAL,
                (byte) AdvancedNoteBlock.MAX_NOTE_VAL,
                (byte) AdvancedNoteBlock.SUBTICK_LENGTH
        ));
    }

    private static long ticks = 0L;
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event){
        if(ticks%6000==0) SongCache.SERVER_CACHE.saveAndClear();
        ticks++;
    }
}
