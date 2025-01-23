package net.nayrus.noteblockmaster.sound;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.render.particle.SustainedNoteOptions;

public class CoreSound extends SimpleSoundInstance{

    private final BlockPos immutablePos;
    private final Level level;
    private Channel channel;
    private final int sustain;
    private final int noteVal;

    public CoreSound(SoundEvent soundEvent, SoundSource source, float volume, int noteVal, RandomSource random, BlockPos pos, Level level, int sustain) {
        super(soundEvent, source, volume, AdvancedNoteBlock.getPitchFromNote(noteVal) , random, pos);
        this.immutablePos = pos.immutable();
        this.level = level;
        this.sustain = sustain;
        this.noteVal = noteVal;
    }

    public void addNoteParticle(){
        level.addParticle(new SustainedNoteOptions(sustain), this.getX(), this.getY() + 0.7, this.getZ(), (this.noteVal - 2) / 29.0F, 0.0, 0.0);
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