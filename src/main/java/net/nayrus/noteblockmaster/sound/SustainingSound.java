package net.nayrus.noteblockmaster.sound;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class SustainingSound extends SimpleSoundInstance {

    private final BlockPos immutablePos;
    private final int sustain;
    private final Level level;

    public SustainingSound(SoundEvent soundEvent, SoundSource source, float volume, float pitch, RandomSource random, BlockPos pos, Level level, int sustain) {
        super(soundEvent, source, volume, pitch, random, pos);
        this.immutablePos = pos.immutable();
        this.level = level;
        this.sustain = sustain;
    }

    public void addNoteParticle(){
        level.addParticle(ParticleTypes.NOTE, this.getX(), this.getY() + 0.7, this.getZ(), this.pitch + this.sustain/200.0, 0.0, 0.0);
    }

    public BlockPos getImmutablePos() {
        return immutablePos;
    }

    public int getSustain() {
        return sustain;
    }
}