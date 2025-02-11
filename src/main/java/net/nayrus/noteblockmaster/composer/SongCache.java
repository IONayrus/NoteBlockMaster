package net.nayrus.noteblockmaster.composer;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.setup.Registry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SongCache extends SavedData {

    public static SongCache SERVER_CACHE;
    public static SongCache CLIENT_CACHE;

    private final HashMap<UUID, SongData> cache = new HashMap<>();
    private final boolean isLocal;

    public SongCache(boolean local){
        super();
        this.isLocal = local;
    }

    public SongCache create(boolean local) {
        return new SongCache(local);
    }

    public SongCache load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        SongCache data = this.create(false);

        if (tag.contains("SongCache", Tag.TAG_LIST)) {
            ListTag songList = tag.getList("SongCache", Tag.TAG_COMPOUND);

            for (int i = 0; i < songList.size(); i++) {
                CompoundTag songTag = songList.getCompound(i);
                UUID key = songTag.getUUID("ID");
                SongData songData = SongData.load(songTag.getCompound("Data"));
                data.cache.put(key, songData);
            }
        }

        return data;
    }
    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag songList = new ListTag();

        for (Map.Entry<UUID, SongData> entry : cache.entrySet()) {
            CompoundTag songTag = new CompoundTag();
            songTag.putUUID("ID", entry.getKey());
            songTag.put("Data", entry.getValue().save(new CompoundTag()));
            songList.add(songTag);
        }

        tag.put("SongCache", songList);
        return tag;
    }

    private void cache(UUID id, SongData data){
        this.cache.put(id, data);
    }

    public CompletableFuture<SongData> getSongFromServer(UUID id){
        if(!this.isLocal) return CompletableFuture.completedFuture(getFromCache(id));

        CompletableFuture<SongData> song = new CompletableFuture<>();

        hasServerKey(id).thenAccept(hasKey -> {
            if(!hasKey){
                song.complete(null);
                return;
            }
            ComposerNetwork.pullRequests.put(id, song);
            PacketDistributor.sendToServer(new PullRequest(id));
        });

        return song;
    }

    public static void cacheSong(UUID id, SongData data){
        SongCache instance = FMLEnvironment.dist == Dist.CLIENT ? CLIENT_CACHE : SERVER_CACHE;
        if(!instance.cache.containsKey(id)) instance.cache(id, data);
        else NoteBlockMaster.LOGGER.info("Song ID {} is already cached", id);
        instance.setDirty();
    }

    public static @Nullable SongData getSong(UUID id, ItemStack IDHolder){
        SongCache cache;
        if(FMLEnvironment.dist == Dist.CLIENT){
            cache = SongCache.CLIENT_CACHE;

            SongData data = cache.getFromCache(id);
            if(data == null){
                cache.hasServerKey(id).thenAccept(hasKey -> {
                    if(!hasKey) IDHolder.remove(Registry.SONG_ID);
                    else{
                        cache.getSongFromServer(id).thenAccept(songData -> {
                            if(songData != null){
                                cache.cache(id, songData);
                            }
                        });
                    }
                });
            }
            return data;
        }else{
            cache = SongCache.SERVER_CACHE;
            return cache.getFromCache(id);
        }
    }

    private @Nullable SongData getFromCache(UUID id){
        return this.cache.getOrDefault(id, null);
    }

    public SongCache loadFromWorld(ServerLevel overworld){
        return overworld.getDataStorage().computeIfAbsent(new Factory<>(() -> this.create(false), this::load), "songcache");
    }

    public static void handlePullRequest(final PullRequest data, final IPayloadContext context){
        if(SERVER_CACHE != null){
            ServerPlayer player = (ServerPlayer) context.player();
            SongData songData = SERVER_CACHE.getFromCache(data.songID());
            if(songData == null){
                NoteBlockMaster.LOGGER.warn("{} send a pull request for a non-existing song", player.getName());
                return;
            }
            PacketDistributor.sendToPlayer(player, songData);
        }
    }

    private static final ConcurrentHashMap<UUID, CompletableFuture<Boolean>> pendingKeyChecks = new ConcurrentHashMap<>();
    public CompletableFuture<Boolean> hasServerKey(UUID id){
        if(!this.isLocal) return CompletableFuture.completedFuture(this.cache.containsKey(id));

        CompletableFuture<Boolean> hasServerKey = new CompletableFuture<>();
        pendingKeyChecks.put(id, hasServerKey);
        PacketDistributor.sendToServer(new KeyCheck(id, false));
        return hasServerKey;
    }

    public static void handleKeyCheckOnServer(final KeyCheck data, final IPayloadContext context){
        if(SERVER_CACHE != null) {
            try {
                PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new KeyCheck(data.key(), SERVER_CACHE.hasServerKey(data.key()).get()));
            } catch (Exception e) {
                NoteBlockMaster.LOGGER.error(Arrays.toString(e.getStackTrace()));
            }
        }
    }

    public static void handleKeyCheckOnClient(final KeyCheck data, final IPayloadContext context){
        if(!pendingKeyChecks.containsKey(data.key())){
            NoteBlockMaster.LOGGER.warn("Server tried to confirm existence of unknown song"); return;
        }
        pendingKeyChecks.get(data.key()).complete(data.hasServerKey());
        pendingKeyChecks.remove(data.key());
    }

    public record KeyCheck(UUID key, boolean hasServerKey) implements CustomPacketPayload {

        public static final Type<KeyCheck> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "songcache.keycheck"));

        public static final StreamCodec<FriendlyByteBuf, KeyCheck> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, KeyCheck::key,
                ByteBufCodecs.BOOL, KeyCheck::hasServerKey,
                KeyCheck::new
        );

        @Override
        public Type<KeyCheck> type() {
            return TYPE;
        }
    }

    public record PullRequest(UUID songID) implements CustomPacketPayload{

        public static final Type<PullRequest> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "songcache.pullrequest"));

        public static final StreamCodec<FriendlyByteBuf, PullRequest> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, PullRequest::songID, PullRequest::new
        );

        @Override
        public Type<PullRequest> type() {
            return TYPE;
        }
    }
}
