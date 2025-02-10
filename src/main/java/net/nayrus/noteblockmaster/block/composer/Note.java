package net.nayrus.noteblockmaster.block.composer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.sound.AdvancedInstrument;
import net.nayrus.noteblockmaster.utils.Utils;

public record Note(byte instrument, byte key, byte volume, byte pitch) {

    public static Note of(libs.felnull.fnnbs.Note nbsNote){
        byte instrument = (byte) Math.clamp(nbsNote.getInstrument(), 0, AdvancedInstrument.values().length - 1);
        byte key = (byte) (Math.clamp(nbsNote.getKey(), 3 + AdvancedNoteBlock.MIN_NOTE_VAL, 3 + AdvancedNoteBlock.MAX_NOTE_VAL) - 3);
        byte keyShift = (byte) Math.clamp((nbsNote.getPitch() / 100), AdvancedNoteBlock.MIN_NOTE_VAL - key , AdvancedNoteBlock.MAX_NOTE_VAL - key);
        key += keyShift;
        byte volume = (byte) (Math.clamp(nbsNote.getVelocity(), 0 , 100) * 0.2F);
        byte pitch = (byte) (nbsNote.getPitch() % 100);
        return new Note(instrument, key, volume, pitch);
    }

    public Note(int data){
        this(getInstrument(data), getKey(data), getVolume(data), getPitch(data));
    }

    public static final Codec<Note> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Codec.INT.fieldOf("data").forGetter(Note::packNote)
        ).apply(instance, Note::new)
    );

    @Override
    public String toString() {
        return  "Instrument: " + AdvancedInstrument.values()[this.instrument]+ ", " +
                "Key: " + Utils.NOTE_STRING[this.key]+ ", " +
                "Volume: " + this.volume * 5 + "%"+ ", " +
                "Pitch: " + this.pitch;
    }

    public int packNote(){
        return this.instrument() & 0xFF
                |       (this.key() & 0xFF) << 8
                |       (this.volume() & 0xFF) << 16
                |       (this.pitch() & 0xFF) << 24;
    }

    public static byte getInstrument(int data){
        return (byte) data;
    }

    public static byte getKey(int data){
        return (byte) (data >> 8);
    }

    public static byte getVolume(int data){
        return (byte) (data >> 16);
    }

    public static byte getPitch(int data){
        return (byte) (data >> 24);
    }
}
