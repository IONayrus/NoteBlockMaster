package net.nayrus.noteblockmaster.render.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.nayrus.noteblockmaster.setup.Registry;

public record SustainedNoteOptions(int duration) implements ParticleOptions {

    public static final MapCodec<SustainedNoteOptions> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("duration").forGetter(SustainedNoteOptions::duration)
            ).apply(instance, SustainedNoteOptions::new));

    public static final StreamCodec<FriendlyByteBuf, SustainedNoteOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SustainedNoteOptions::duration,
            SustainedNoteOptions::new
    );

    @Override
    public ParticleType<SustainedNoteOptions> getType() {
        return Registry.SUSTAINED_NOTE.get();
    }
}
