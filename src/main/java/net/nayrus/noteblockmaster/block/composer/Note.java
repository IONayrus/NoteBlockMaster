package net.nayrus.noteblockmaster.block.composer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.sound.AdvancedInstrument;
import net.nayrus.noteblockmaster.utils.Utils;

public record Note(byte instrument, byte key, byte volume, byte pitch) {

    public static Note of(libs.felnull.fnnbs.Note nbsNote){
        byte instrument = (byte) Math.clamp(nbsNote.getInstrument(), 0, AdvancedInstrument.values().length - 1);
        byte key = (byte) (Math.clamp(nbsNote.getKey(), 3, 85) - 3);
        byte keyShift = (byte) Math.clamp((nbsNote.getPitch() / 100), AdvancedNoteBlock.MIN_NOTE_VAL - key , AdvancedNoteBlock.MAX_NOTE_VAL - key);
        key += keyShift;
        byte volume = (byte) (Math.clamp(nbsNote.getVelocity(), 0 , 100) / 5);
        byte pitch = (byte) (nbsNote.getPitch() % 100);
        return new Note(instrument, key, volume, pitch);
    }

    public static final Codec<Note> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Codec.INT.fieldOf("data").forGetter(note ->
                        note.instrument() & 0xFF
                |       (note.key() & 0xFF) << 8
                |       (note.volume() & 0xFF) << 16
                |       (note.pitch() & 0xFF) << 24)
        ).apply(instance, data -> new Note((byte) (data & 0xFF), (byte) ((data >> 8) & 0xFF), (byte) ((data >> 16) & 0xFF), (byte)((data >> 24) & 0xFF)))
    );

    @Override
    public String toString() {
        return  "Instrument: " + AdvancedInstrument.values()[this.instrument]+ ", " +
                "Key: " + Utils.NOTE_STRING[this.key]+ ", " +
                "Volume: " + this.volume * 5 + "%"+ ", " +
                "Pitch: " + this.pitch;
    }
}
