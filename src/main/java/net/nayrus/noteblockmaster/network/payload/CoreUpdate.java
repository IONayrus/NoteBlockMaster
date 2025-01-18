package net.nayrus.noteblockmaster.network.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.NoteBlockMaster;

public record CoreUpdate(BlockPos pos, int sustain, int volume) implements CustomPacketPayload {

    public static final Type<CoreUpdate> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "coreupdate"));

    @Override
    public Type<CoreUpdate> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, CoreUpdate> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, CoreUpdate::pos,
            ByteBufCodecs.INT, CoreUpdate::sustain,
            ByteBufCodecs.INT, CoreUpdate::volume,
            CoreUpdate::new
    );
}
