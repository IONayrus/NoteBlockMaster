package net.nayrus.noteblockmaster.util;


import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SubTickScheduler {

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static void delayedNoteBlockEvent(BlockState state, Level level, BlockPos pos){
        executor.schedule(()-> {
            NoteBlockInstrument noteblockinstrument = state.getValue(NoteBlock.INSTRUMENT);
            float f;
            if (noteblockinstrument.isTunable()) {
                int i = AdvancedNoteBlock.getNoteValue(state);
                f = AdvancedNoteBlock.getPitchFromNote(i);
                level.addParticle(
                        ParticleTypes.NOTE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)i / 24.0, 0.0, 0.0
                );
            } else {
                f = 1.0F;
            }

            level.playSound(
                    null,
                    (double)pos.getX() + 0.5,
                    (double)pos.getY() + 0.5,
                    (double)pos.getZ() + 0.5,
                    noteblockinstrument.getSoundEvent(),
                    SoundSource.RECORDS,
                    3.0F,
                    f
            );
        },state.getValue(AdvancedNoteBlock.SUBTICK) * AdvancedNoteBlock.SUBTICK_LENGTH, TimeUnit.MILLISECONDS);
    }

    public static void shutdown(){executor.shutdown();}
}
