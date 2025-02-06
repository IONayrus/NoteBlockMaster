package libs.felnull.fnnbs.impl;


import libs.felnull.fnnbs.Note;

import java.util.Objects;

public class NoteImpl implements Note {
    private final int instrument;
    private final int key;
    private final int velocity;
    private final int panning;
    private final int pitch;

    public NoteImpl(int instrument, int key, int velocity, int panning, int pitch) {
        this.instrument = instrument;
        this.key = key;
        this.velocity = velocity;
        this.panning = panning;
        this.pitch = pitch;
    }

    @Override
    public int getInstrument() {
        return instrument;
    }

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public int getPanning() {
        return panning;
    }

    @Override
    public int getPitch() {
        return pitch;
    }

    @Override
    public int getVelocity() {
        return velocity;
    }

    @Override
    public String toString() {
        return "NoteImpl{" +
                "instrument=" + instrument +
                ", key=" + key +
                ", velocity=" + velocity +
                ", panning=" + panning +
                ", pitch=" + pitch +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteImpl note = (NoteImpl) o;
        return instrument == note.instrument && key == note.key && velocity == note.velocity && panning == note.panning && pitch == note.pitch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instrument, key, velocity, panning, pitch);
    }
}
