package net.nayrus.noteblockmaster.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.NoteBlockMaster;

public record StartUpSync(byte minNote, byte maxNote, byte subtickLength) implements CustomPacketPayload {

    public static final Type<StartUpSync> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "startupsync"));

    @Override
    public Type<StartUpSync> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, StartUpSync> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, StartUpSync::minNote,
            ByteBufCodecs.BYTE, StartUpSync::maxNote,
            ByteBufCodecs.BYTE, StartUpSync::subtickLength,
            StartUpSync::new
    );
}
