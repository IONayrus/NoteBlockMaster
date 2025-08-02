package net.nayrus.noteblockmaster.network.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.NoteBlockMaster;

public record RemoveBlockInfo(BlockPos position) implements CustomPacketPayload {

    public static final Type<RemoveBlockInfo> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "remove_block_info"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveBlockInfo> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            RemoveBlockInfo::position,
            RemoveBlockInfo::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}