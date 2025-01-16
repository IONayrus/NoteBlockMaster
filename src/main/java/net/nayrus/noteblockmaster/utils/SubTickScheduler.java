package net.nayrus.noteblockmaster.utils;


import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.block.TuningCore;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class SubTickScheduler {

    public static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new SubTickThread());

    public static void delayedNoteBlockEvent(BlockState state, Level level, BlockPos pos, AdvancedInstrument instrument, float volume){
        executor.schedule(() -> {
            int i = AdvancedNoteBlock.getNoteValue(state);
            level.addParticle(
                    ParticleTypes.NOTE, (double) pos.getX() + 0.5, (double) pos.getY() + 1.2, (double) pos.getZ() + 0.5, (i - 2f) / 29, 0.0, 0.0
            );
            level.playSound(
                    null,
                    pos,
                    instrument.getSoundEvent().value(),
                    SoundSource.RECORDS,
                    volume,
                    AdvancedNoteBlock.getPitchFromNote(i)
            );
        }, (long) state.getValue(AdvancedNoteBlock.SUBTICK) * AdvancedNoteBlock.SUBTICK_LENGTH, TimeUnit.MILLISECONDS);
    }

    public static void delayedSustainedNoteBlockEvent(BlockState anb, BlockState core, Level level, BlockPos pos, AdvancedInstrument instrument){
        if(level.isClientSide()) {
            SoundEvent sound = instrument.getSoundEvent().value();
            SimpleSoundInstance simplesoundinstance = new SimpleSoundInstance(
                    sound, SoundSource.RECORDS, 3.0F * TuningCore.getVolume(core), AdvancedNoteBlock.getPitchFromNote(AdvancedNoteBlock.getNoteValue(anb)),
                    RandomSource.create(level.getRandom().nextLong()), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5
            );
        }
    }

    static class SubTickThread implements ThreadFactory{

        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("Subtickscheduler");
            return thread;
        }
    }
}
