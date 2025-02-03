package net.nayrus.noteblockmaster.network.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.NoteBlockMaster;

public record TunerData(int value, byte flags) implements CustomPacketPayload {

    public static final Type<TunerData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "tunerdata"));

    public static final Codec<TunerData> TUNER_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("value").forGetter(TunerData::value),
                    Codec.BYTE.fieldOf("setmode").forGetter(TunerData::flags)
            ).apply(instance, TunerData::new)
    );
    public static final StreamCodec<FriendlyByteBuf, TunerData> TUNER_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, TunerData::value,
            ByteBufCodecs.BYTE, TunerData::flags,
            TunerData::new
    );

    @Override
    public Type<TunerData> type() {
        return TYPE;
    }

    public static TunerData of(int value, boolean setmode, boolean offhand){
        byte flags = 0;
        if(setmode) flags |= 1;
        if(offhand) flags |= 1 << 1;
        return new TunerData(value, flags);
    }

    public boolean isSetmode(){
        return (this.flags() & 1) != 0;
    }

    public boolean isInOffhand(){
        return (this.flags() & (1 << 1)) != 0;
    }
}
