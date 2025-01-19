package net.nayrus.noteblockmaster.sound;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.nayrus.noteblockmaster.block.TuningCore;

public class SustainingSound extends SimpleSoundInstance{

    private final BlockPos immutablePos;
    private final Level level;
    private Channel channel;
    private final int sustain;

    public SustainingSound(SoundEvent soundEvent, SoundSource source, float volume, float pitch, RandomSource random, BlockPos pos, Level level, int sustain) {
        super(soundEvent, source, volume, pitch, random, pos);
        this.immutablePos = pos.immutable();
        this.level = level;
        this.sustain = sustain;
    }

    public void addNoteParticle(){
        level.addParticle(ParticleTypes.NOTE, this.getX(), this.getY() + 0.7, this.getZ(), this.pitch + this.sustain/ (double)TuningCore.SUSTAIN_MAXVAL, 0.0, 0.0);
    }

    public BlockPos getImmutablePos() {
        return immutablePos;
    }

    @Override
    public Attenuation getAttenuation() {
        return Attenuation.NONE;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return this.channel;
    }
}