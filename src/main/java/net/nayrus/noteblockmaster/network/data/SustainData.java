package net.nayrus.noteblockmaster.network.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.NoteBlockMaster;

public record SustainData(byte duration) implements CustomPacketPayload{

    public static final Type<SustainData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "sustaindata"));

    public static final Codec<SustainData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BYTE.fieldOf("duration").forGetter(SustainData::duration)
            ).apply(instance, SustainData::new)
    );
    public static final StreamCodec<FriendlyByteBuf, SustainData> TUNER_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, SustainData::duration,
            SustainData::new
    );

    @Override
    public Type<SustainData> type() {
        return TYPE;
    }
}
