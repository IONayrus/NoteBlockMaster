package net.nayrus.noteblockmaster.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.NoteBlockMaster;

public record ConfigCheck(byte minNote, byte maxNote, byte subtickLength) implements CustomPacketPayload {

    public static final Type<ConfigCheck> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "configcheck"));

    @Override
    public Type<ConfigCheck> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, ConfigCheck> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, ConfigCheck::minNote,
            ByteBufCodecs.BYTE, ConfigCheck::maxNote,
            ByteBufCodecs.BYTE, ConfigCheck::subtickLength,
            ConfigCheck::new
    );
}
