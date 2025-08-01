package net.nayrus.noteblockmaster.event;


import net.nayrus.noteblockmaster.composer.SongCache;
import net.nayrus.noteblockmaster.composer.SongFileManager;
import net.nayrus.noteblockmaster.sound.SubTickScheduler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;


public class ServerEvents {

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event){
        SubTickScheduler.executor.shutdown();
    }

    private static long ticks = 0L;
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event){
        if(ticks%6000==0){
            SongCache.SERVER_CACHE.saveAndClearCache();
            SongFileManager.validateAndLoadCache();
        }
        ticks++;
    }
}
