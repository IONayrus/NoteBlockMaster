package net.nayrus.noteblockmaster.network.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.NoteBlockMaster;

import java.util.UUID;

public record SongID(UUID songID) implements CustomPacketPayload {

    public static final Type<SongID> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "songid"));

    public static final Codec<SongID> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    UUIDUtil.CODEC.fieldOf("songid").forGetter(SongID::songID)
            ).apply(instance, SongID::new));

    public static final StreamCodec<FriendlyByteBuf, SongID> STREAM_CODEC = StreamCodec.composite(UUIDUtil.STREAM_CODEC, SongID::songID, SongID::new);

    @Override
    public Type<SongID> type() {
        return TYPE;
    }
}
