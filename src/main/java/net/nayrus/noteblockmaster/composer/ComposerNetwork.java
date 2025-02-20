package net.nayrus.noteblockmaster.composer;

import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ComposerNetwork {

    public static void register(final PayloadRegistrar reg){
        reg.playToClient(ComposerBlockEntity.ClientItemUpdate.TYPE, ComposerBlockEntity.ClientItemUpdate.STREAM_CODEC, ComposerBlockEntity::handleClientItemUpdate);

        reg.executesOn(HandlerThread.NETWORK);

        reg.playToClient(SongCache.PushRequest.TYPE, SongCache.PushRequest.STREAM_CODEC, SongCache::handlePushRequest);
        reg.playToClient(SongCache.DropRequest.TYPE, SongCache.DropRequest.STREAM_CODEC, SongCache::handleDropRequest);

        reg.playBidirectional(SongData.TYPE, SongData.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ComposerNetwork::handleSongPull, ComposerNetwork::handleSongPush));
        reg.playBidirectional(SongCache.KeyCheck.TYPE, SongCache.KeyCheck.STREAM_CODEC,
                new DirectionalPayloadHandler<>(SongCache::handleKeyCheckOnClient, SongCache::handleKeyCheckOnServer));

        reg.playToServer(SongCache.PullRequest.TYPE, SongCache.PullRequest.STREAM_CODEC, SongCache::handlePullRequest);
    }

    private static void handleSongPush(final SongData data, final IPayloadContext context){
        SongCache.cacheSong(data.getID(), data, SongCache.SERVER_CACHE);
    }

    public static final ConcurrentHashMap<UUID, CompletableFuture<SongData>> pullRequests = new ConcurrentHashMap<>();
    private static void handleSongPull(final SongData data, final IPayloadContext context){
        UUID id = data.getID();
        pullRequests.computeIfPresent(id,
                (uuid, future) -> {
                    future.complete(data);
                    return null;
                });
        SongCache.cacheSong(id, data);
    }

}
