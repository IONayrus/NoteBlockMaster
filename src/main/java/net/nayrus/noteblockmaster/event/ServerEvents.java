package net.nayrus.noteblockmaster.event;

import net.nayrus.noteblockmaster.sound.SubTickScheduler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

public class ServerEvents {

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event){
        SubTickScheduler.executor.shutdown();
    }

}
