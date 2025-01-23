package net.nayrus.noteblockmaster.render.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SustainedNoteType extends ParticleType<SustainedNoteOptions> {

    public SustainedNoteType(boolean overrideLimitter) {
        super(overrideLimitter);
    }

    @Override
    public MapCodec<SustainedNoteOptions> codec() {
        return SustainedNoteOptions.CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, SustainedNoteOptions> streamCodec() {
        return SustainedNoteOptions.STREAM_CODEC;
    }
}
