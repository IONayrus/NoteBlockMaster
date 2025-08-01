package net.nayrus.noteblockmaster.event;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.sound.AdvancedInstrument;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public abstract class AdvancedNoteBlockEvent extends BlockEvent {
    private int noteId;

    protected AdvancedNoteBlockEvent(Level world, BlockPos pos, BlockState state, int note) {
        super(world, pos, state);
        this.noteId = note;
    }

    public int getAdvancedNoteId() {
        return this.noteId;
    }

    public void setNote(net.neoforged.neoforge.event.level.NoteBlockEvent.Note note, net.neoforged.neoforge.event.level.NoteBlockEvent.Octave octave) {
        Preconditions.checkArgument(octave != net.neoforged.neoforge.event.level.NoteBlockEvent.Octave.HIGH || note == net.neoforged.neoforge.event.level.NoteBlockEvent.Note.F_SHARP, "Octave.HIGH is only valid for Note.F_SHARP!");
        this.noteId = note.ordinal() + octave.ordinal() * 12;
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

    public static class Change extends AdvancedNoteBlockEvent implements ICancellableEvent {
        private final int oldNoteID;

        public Change(Level world, BlockPos pos, BlockState state, int oldNote, int newNote) {
            super(world, pos, state, newNote);
            this.oldNoteID = oldNote;
        }

        public int getOldNoteID() {
            return this.oldNoteID;
        }
    }

    public static int onNoteChange(Level level, BlockPos pos, BlockState state, int old, int _new) {
        Change event = new Change(level, pos, state, old, _new);
        return net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event).isCanceled() ? -1 : event.getAdvancedNoteId();
    }

}