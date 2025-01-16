package net.nayrus.noteblockmaster.network.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.nayrus.noteblockmaster.NoteBlockMaster;

public record TickSchedule(BlockPos pos, Holder<Block> block, int delay) implements CustomPacketPayload{

    public static final Type<TickSchedule> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "tickschedule"));

    @Override
    public Type<TickSchedule> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, TickSchedule> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(BlockPos.CODEC), TickSchedule::pos,
            ByteBufCodecs.holderRegistry(Registries.BLOCK), TickSchedule::block,
            ByteBufCodecs.INT, TickSchedule::delay,
            TickSchedule::new
    );

}
