package net.nayrus.noteblockmaster.network.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.render.ANBInfoRender;

import java.util.Map;

public record SyncBlockInfos(Map<BlockPos, ANBInfoRender.BlockInfo> states) implements CustomPacketPayload {

    public static final Type<SyncBlockInfos> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "sync_block_info"));

    public static final StreamCodec<RegistryFriendlyByteBuf, Map<BlockPos, ANBInfoRender.BlockInfo>> MAP_CODEC =
            ByteBufCodecs.map(java.util.LinkedHashMap::new, BlockPos.STREAM_CODEC, ANBInfoRender.BlockInfo.STREAM_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBlockInfos> STREAM_CODEC = StreamCodec.composite(
            MAP_CODEC,
            SyncBlockInfos::states,
            SyncBlockInfos::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}