package net.nayrus.noteblockmaster.sound;


import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.block.TuningCore;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class SubTickScheduler {

    public static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new SubtickThread());
    public static HashMap<BlockPos, SustainingSound> SUSTAINED_SOUNDS = new HashMap<>();

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
            SustainingSound instance = new SustainingSound(
                    instrument.getSustainedEvent(TuningCore.getSustain(core)), SoundSource.RECORDS, (TuningCore.getVolume(core) / 20.0F), AdvancedNoteBlock.getPitchFromNote(AdvancedNoteBlock.getNoteValue(anb)),
                    RandomSource.create(level.getRandom().nextLong()), pos, level,  TuningCore.getSustain(core) * 1000);

            executor.schedule(()-> playSustainingSound(instance),
                    (long) anb.getValue(AdvancedNoteBlock.SUBTICK) * AdvancedNoteBlock.SUBTICK_LENGTH, TimeUnit.MILLISECONDS);
        }
    }

    public static void playSustainingSound(SustainingSound sound) {
        playbackStop(sound.getImmutablePos());
        Minecraft.getInstance().getSoundManager().play(sound);
    }

    @OnlyIn(Dist.CLIENT)
    public static void playbackStop(BlockPos pos){
        SUSTAINED_SOUNDS.computeIfPresent(pos.immutable(), (p, sound) -> {
            if(!sound.getChannel().stopped()) Minecraft.getInstance().getSoundManager().stop(sound);
            return null;
        });
    }

    static class SubtickThread implements ThreadFactory{

        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("NBM Subtick Thread");
            return thread;
        }
    }
}
