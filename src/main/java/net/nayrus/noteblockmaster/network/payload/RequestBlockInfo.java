package net.nayrus.noteblockmaster.network.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.NoteBlockMaster;

import java.util.List;

public record RequestBlockInfo(List<BlockPos> positions) implements CustomPacketPayload {

    public static final Type<RequestBlockInfo> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "request_block_info"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestBlockInfo> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()),
            RequestBlockInfo::positions,
            RequestBlockInfo::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}