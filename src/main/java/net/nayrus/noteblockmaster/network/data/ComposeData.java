package net.nayrus.noteblockmaster.network.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ComposeData(int beat, int subtick, int nextRepeater, float bpm) {

    public static final Codec<ComposeData> TUNER_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("beat").forGetter(ComposeData::beat),
                    Codec.INT.fieldOf("subtick").forGetter(ComposeData::subtick),
                    Codec.INT.fieldOf("repeater").forGetter(ComposeData::nextRepeater),
                    Codec.FLOAT.fieldOf("bpm").forGetter(ComposeData::bpm)
            ).apply(instance, ComposeData::new)

    );

}
