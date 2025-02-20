package net.nayrus.noteblockmaster.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.NoteBlockMaster;

public record LoadSong(String name) implements CustomPacketPayload {

    public static final Type<LoadSong> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "loadsong"));

    public static final StreamCodec<FriendlyByteBuf, LoadSong> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, LoadSong::name,
            LoadSong::new
    );

    @Override
    public Type<LoadSong> type() {
        return TYPE;
    }
}
