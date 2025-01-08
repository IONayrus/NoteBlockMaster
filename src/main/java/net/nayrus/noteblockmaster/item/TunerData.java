package net.nayrus.noteblockmaster.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TunerData(int value, boolean setmode) {

    public static final Codec<TunerData> TUNER_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("value").forGetter(TunerData::value),
                    Codec.BOOL.fieldOf("setmode").forGetter(TunerData::setmode)
            ).apply(instance, TunerData::new)
    );
    public static final StreamCodec<ByteBuf, TunerData> TUNER_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, TunerData::value,
            ByteBufCodecs.BOOL, TunerData::setmode,
            TunerData::new
    );
    
}
