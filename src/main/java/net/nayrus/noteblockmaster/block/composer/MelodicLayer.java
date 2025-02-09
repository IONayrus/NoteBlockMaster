package net.nayrus.noteblockmaster.block.composer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record MelodicLayer(int x) {

    public static final Codec<MelodicLayer> CODEC = RecordCodecBuilder.create(instance ->
           instance.group(
                  Codec.INT.fieldOf("x").forGetter(MelodicLayer::x)
           ).apply(instance, MelodicLayer::new));


}
