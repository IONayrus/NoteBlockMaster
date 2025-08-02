package net.nayrus.noteblockmaster.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.sound.AdvancedInstrument;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public abstract class AdvancedNoteBlockEvent extends BlockEvent {
    private final int eventValue;

    protected AdvancedNoteBlockEvent(Level world, BlockPos pos, BlockState state, int val) {
        super(world, pos, state);
        this.eventValue = val;
    }

    public int getEventValue() {
        return this.eventValue;
    }

    public static class Play extends AdvancedNoteBlockEvent implements ICancellableEvent {
        private AdvancedInstrument instrument;

        public Play(Level world, BlockPos pos, BlockState state, int note, AdvancedInstrument instrument) {
            super(world, pos, state, note);
            this.instrument = instrument;
        }

        public AdvancedInstrument getInstrument() {
            return this.instrument;
        }

        public void setInstrument(AdvancedInstrument instrument) {
            this.instrument = instrument;
        }
    }

    public static class NoteChange extends StateChange implements ICancellableEvent {
        private final int oldNoteID;

        public NoteChange(Level world, BlockPos pos, BlockState state, int oldNote, int newNote) {
            super(world, pos, state, newNote);
            this.oldNoteID = oldNote;
        }

        public int getOldNoteID() {
            return this.oldNoteID;
        }
    }

    public static class SubtickChange extends StateChange implements ICancellableEvent {
        private final int oldTick;

        public SubtickChange(Level world, BlockPos pos, BlockState state, int oldTick, int newTick) {
            super(world, pos, state, newTick);
            this.oldTick= oldTick;
        }

        public int getPreviousTick() {
            return this.oldTick;
        }
    }

    public static class StateChange extends AdvancedNoteBlockEvent{

        protected StateChange(Level world, BlockPos pos, BlockState state, int val) {
            super(world, pos, state, val);
        }
    }

    public static class Removed extends StateChange{

        public Removed(Level world, BlockPos pos, BlockState state) {
            super(world, pos, state, -1);
        }
    }

    public static class Placed extends StateChange{

        public Placed(Level world, BlockPos pos, BlockState state) {
            super(world, pos, state, 0);
        }
    }

    public static int onNoteChange(Level level, BlockPos pos, BlockState state, int old, int _new) {
        NoteChange event = new NoteChange(level, pos, state, old, _new);
        return net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event).isCanceled() ? -1 : event.getEventValue();
    }

    public static int onSubtickChange(Level level, BlockPos pos, BlockState state, int old, int _new) {
        SubtickChange event = new SubtickChange(level, pos, state, old, _new);
        return net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event).isCanceled() ? -1 : event.getEventValue();
    }

}