package net.nayrus.noteblockmaster.network.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.NoteBlockMaster;

public record TickSchedule(BlockPos pos, int delay) implements CustomPacketPayload{

    public static final Type<TickSchedule> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "tickschedule"));

    @Override
    public Type<TickSchedule> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, TickSchedule> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, TickSchedule::pos,
            ByteBufCodecs.INT, TickSchedule::delay,
            TickSchedule::new
    );

}
