package libs.felnull.fnnbs.impl;

import libs.felnull.fnnbs.Layer;
import libs.felnull.fnnbs.Note;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class LayerImpl implements Layer {
    private final Map<Integer, Note> notes = new TreeMap<>();
    private String name;
    private boolean lock;
    private int volume = 100;
    private int stereo;

    protected void addNote(int tick, Note note) {
        notes.put(tick, note);
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setLock(boolean lock) {
        this.lock = lock;
    }

    protected void setStereo(int stereo) {
        this.stereo = stereo;
    }

    protected void setVolume(int volume) {
        this.volume = volume;
    }

    @Override
    public int getStereo() {
        return stereo;
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public boolean isLock() {
        return lock;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @Nullable Note getNote(int tick) {
        return notes.get(tick);
    }

    @NotNull
    @Override
    public Map<Integer, Note> getNotes() {
        return Collections.unmodifiableMap(notes);
    }

    @Override
    public String toString() {
        return "LayerImpl{" +
                "notes=" + notes +
                ", name='" + name + '\'' +
                ", lock=" + lock +
                ", volume=" + volume +
                ", stereo=" + stereo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LayerImpl layer = (LayerImpl) o;
        return lock == layer.lock && volume == layer.volume && stereo == layer.stereo && Objects.equals(notes, layer.notes) && Objects.equals(name, layer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notes, name, lock, volume, stereo);
    }
}
