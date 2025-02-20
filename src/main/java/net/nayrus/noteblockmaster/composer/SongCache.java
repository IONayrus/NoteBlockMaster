package net.nayrus.noteblockmaster.composer;

import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.setup.Registry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SongCache extends SavedData {

    public static SongCache SERVER_CACHE;
    public static SongCache CLIENT_CACHE;

    private final ConcurrentHashMap<UUID, SongData> cache = new ConcurrentHashMap<>();
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
                SongFileManager.registeredSongs.add(key);
                SongData songData = SongData.load(songTag.getCompound("Data"));
                data.cache.put(key, songData);
            }
        }

        return data;
    }
    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        return saveCacheOnTag(tag);
    }

    public CompoundTag saveCacheOnTag(CompoundTag tag){
        ListTag songList = new ListTag();

        for (Map.Entry<UUID, SongData> entry : this.cache.entrySet()) {
            CompoundTag songTag = new CompoundTag();
            songTag.putUUID("ID", entry.getKey());
            songTag.put("Data", entry.getValue().save(new CompoundTag()));
            songList.add(songTag);
        }

        tag.put("SongCache", songList);
        return tag;
    }

    public void saveCachedSongs() throws IOException {
        for(SongData song : this.cache.values()){
            SongFileManager.safeCachedSong(song);
        }
    }

    public boolean saveIfPresent(UUID id){
        if(!this.cache.containsKey(id)) return false;
        try{
            SongFileManager.safeCachedSong(this.cache.get(id));
        }catch (IOException e){
            NoteBlockMaster.LOGGER.error("Error saving song {} - {}", id, e.getLocalizedMessage());
        }
        return true;
    }

    public boolean loadSongFromFile(UUID id){
        SongData songData = SongFileManager.loadCachedSong(id);
        if(songData == null){
            SongFileManager.registeredSongs.remove(id);
            NoteBlockMaster.LOGGER.debug("Tried to load not-existing cached song file of {}", id);
            return false;
        }
        if(!SongFileManager.registeredSongs.contains(id)) SongFileManager.registeredSongs.add(id);
        this.cache.put(id, songData);
        return true;
    }

    public void flushCache() throws IOException {
        if(this.isLocal) CLIENT_CACHE.saveCachedSongs();
        else SERVER_CACHE.saveCachedSongs();
    }

    public void saveAndClearCache(){
        NoteBlockMaster.LOGGER.debug("Saving & clearing song cache");
        try{
            this.flushCache();
            this.cache.clear();
            this.setDirty();
        } catch (IOException e) {
            NoteBlockMaster.LOGGER.error("Error druring cache safe - {}", e.getLocalizedMessage());
        }
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
        cacheSong(id, data, instance);
    }

    public static void cacheSong(UUID id, SongData data, SongCache instance){
        if(!instance.cache.containsKey(id)){
            instance.cache(id, data);
            if(!instance.isLocal && !SongFileManager.registeredSongs.contains(id)) SongFileManager.registeredSongs.add(id);
        }
        else NoteBlockMaster.LOGGER.info("Song ID {} is already cached", id);
        instance.setDirty();
    }

    @OnlyIn(Dist.CLIENT) private static final HashMap<UUID, Long> pendingSongRequest = new HashMap<>();
    public static @Nullable SongData getSong(UUID id, ItemStack IDHolder){
        SongCache cache;
        if(FMLEnvironment.dist == Dist.CLIENT){
            cache = SongCache.CLIENT_CACHE;

            SongData data = cache.getFromCache(id);
            if(data == null){
                pendingSongRequest.computeIfPresent(id, (uuid, time) -> { if(time + 1000 > Util.getMillis()) return time; return null; });
                if(!pendingSongRequest.containsKey(id)){
                    cache.hasServerKey(id).thenAccept(hasKey -> {
                        if(!hasKey){
                            IDHolder.remove(Registry.SONG_ID);
                            pendingSongRequest.remove(id);
                        }
                        else{
                            cache.getSongFromServer(id).thenAccept(songData -> {
                                if(songData != null){
                                    cache.cache(id, songData);
                                }
                                pendingSongRequest.remove(id);
                            });
                        }
                    });
                    pendingSongRequest.put(id, Util.getMillis());
                }
            }
            return data;
        }else{
            cache = SongCache.SERVER_CACHE;
            return cache.getFromCache(id);
        }
    }

    public void dropSong(UUID id) {
        this.cache.remove(id);
        if(!this.isLocal){
            try {
                SongFileManager.deleteCachedSong(id);
                SongFileManager.registeredSongs.removeIf(currentID -> currentID.compareTo(id) == 0);
            } catch (IOException e) {
                NoteBlockMaster.LOGGER.error("Could not delete cached song {} - {}", id, e.getLocalizedMessage());
            }
        }
        this.setDirty();
    }

    public List<String> getCachedSongInfo(){
        List<String> songInfos = new ArrayList<>();
        for(Map.Entry<UUID, SongData> entry : this.cache.entrySet()){
            songInfos.add(entry.getKey().toString() + " // " + entry.getValue().title()  + " by " + entry.getValue().author());
        }
        return songInfos;
    }

    public List<String> getRegisteredSongIDs(){
        List<String> asString = new ArrayList<>();
        SongFileManager.registeredSongs.forEach(uuid -> asString.add(uuid.toString()));
        return asString;
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

    public static void handlePushRequest(final PushRequest data, final IPayloadContext context){
        if(CLIENT_CACHE != null){
            Player player = context.player();
            SongData songData = CLIENT_CACHE.getFromCache(data.songID());
            if(songData == null){
                NoteBlockMaster.LOGGER.warn("The Server send a push request for a non-existing song");
                return;
            }
            player.displayClientMessage(Component.literal("Pushing song " + songData.title() + " by " + songData.author() + " to server"), false);
            PacketDistributor.sendToServer(songData);
        }
    }

    public static void handleDropRequest(final DropRequest data, final IPayloadContext context){
        if(CLIENT_CACHE != null){
            Player player = context.player();
            SongData songData = CLIENT_CACHE.getFromCache(data.songID());
            if(songData == null){
                NoteBlockMaster.LOGGER.warn("Could not delete song with unknown UUID {}", data.songID());
                return;
            }
            player.displayClientMessage(Component.literal("Deleted song " + songData.title() + " by " + songData.author()), false);
            CLIENT_CACHE.dropSong(data.songID());
        }
    }

    private static final ConcurrentHashMap<UUID, CompletableFuture<Boolean>> pendingKeyChecks = new ConcurrentHashMap<>();
    public CompletableFuture<Boolean> hasServerKey(UUID id){
        if(!this.isLocal) {
            if(this.cache.containsKey(id)) return CompletableFuture.completedFuture(true);
            if(!SongFileManager.registeredSongs.contains(id)) return CompletableFuture.completedFuture(false);

            return CompletableFuture.completedFuture(this.loadSongFromFile(id));
        }

        if(!pendingKeyChecks.containsKey(id)) {
            CompletableFuture<Boolean> hasServerKey = new CompletableFuture<>();
            pendingKeyChecks.put(id, hasServerKey);
            PacketDistributor.sendToServer(new KeyCheck(id, false));
            return hasServerKey;
        }
        else return pendingKeyChecks.get(id);
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

    public record PushRequest(UUID songID) implements CustomPacketPayload{

        public static final Type<PushRequest> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "songcache.pushrequest"));

        public static final StreamCodec<FriendlyByteBuf, PushRequest> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, PushRequest::songID, PushRequest::new
        );

        @Override
        public Type<PushRequest> type() {
            return TYPE;
        }
    }

    public record DropRequest(UUID songID) implements CustomPacketPayload{

        public static final Type<DropRequest> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "songcache.droprequest"));

        public static final StreamCodec<FriendlyByteBuf, DropRequest> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, DropRequest::songID, DropRequest::new
        );

        @Override
        public Type<DropRequest> type() {
            return TYPE;
        }
    }

}
