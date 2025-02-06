package libs.felnull.fnnbs.impl;


import libs.felnull.fnnbs.Layer;
import libs.felnull.fnnbs.NBS;
import libs.felnull.fnnbs.impl.instrument.CustomInstrumentImpl;
import libs.felnull.fnnbs.instrument.CustomInstrument;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NBSImpl implements NBS {
    private final boolean old;
    private final int version;
    private final int vanillaInstrumentCount;
    private final int songLength;
    private final int layerCount;
    private final String name;
    private final String author;
    private final String originalAuthor;
    private final String description;
    private final int tempo;
    private final boolean autoSaving;
    private final int autoSavingDuration;
    private final int timeSignature;
    private final int minutesSpent;
    private final int leftClicks;
    private final int rightClicks;
    private final int noteBlocksAdded;
    private final int noteBlocksRemoved;
    private final String importFileName;
    private final boolean loop;
    private final int loopCount;
    private final int loopStart;
    private final List<LayerImpl> layers = new ArrayList<>();
    private final List<CustomInstrumentImpl> customInstruments = new ArrayList<>();

    public NBSImpl(InputStream stream) throws IOException {
        int fl = readShort(stream);
        this.old = fl != 0;
        if (old) {
            this.version = 0;
            this.vanillaInstrumentCount = 10;
            this.songLength = fl;
        } else {
            this.version = stream.read();
            this.vanillaInstrumentCount = stream.read();
            this.songLength = readShort(stream);
        }
        if (this.version > 5)
            throw new RuntimeException("Unsupported nbs version");

        this.layerCount = readShort(stream);
        for (int i = 0; i < layerCount; i++) {
            layers.add(new LayerImpl());
        }

        this.name = readString(stream);
        this.author = readString(stream);
        this.originalAuthor = readString(stream);
        this.description = readString(stream);
        this.tempo = readShort(stream);
        this.autoSaving = readBoolean(stream);
        this.autoSavingDuration = stream.read();
        this.timeSignature = stream.read();
        this.minutesSpent = readInt(stream);
        this.leftClicks = readInt(stream);
        this.rightClicks = readInt(stream);
        this.noteBlocksAdded = readInt(stream);
        this.noteBlocksRemoved = readInt(stream);
        this.importFileName = readString(stream);
        if (old) {
            this.loop = false;
            this.loopCount = -1;
            this.loopStart = -1;
        } else {
            this.loop = readBoolean(stream);
            this.loopCount = stream.read();
            this.loopStart = readShort(stream);
        }

        int tick = -1;
        int nextJT = readShort(stream);
        while (nextJT != 0) {
            tick += nextJT;
            int layer = -1;
            int nextJL = readShort(stream);
            while (nextJL != 0) {
                layer += nextJL;
                int instrument = stream.read();
                int key = stream.read();
                int velocity = old ? 100 : stream.read();
                int panning = old ? 100 : stream.read();
                int pitch = old ? 0 : readSignedShort(stream);
                NoteImpl note = new NoteImpl(instrument, key, velocity, panning, pitch);

                int ls = layers.size();
                if (ls <= layer) {
                    for (int i = 0; i <= ls - layer; i++)
                        layers.add(new LayerImpl());
                }

                layers.get(layer).addNote(tick, note);
                nextJL = readShort(stream);
            }
            nextJT = readShort(stream);
        }

        for (int i = 0; i < layerCount; i++) {
            LayerImpl layer = layers.get(i);

            layer.setName(readString(stream));
            if (!old)
                layer.setLock(readBoolean(stream));

            layer.setVolume(stream.read());

            if (!old)
                layer.setStereo(stream.read());
        }

        int cic = stream.read();
        if (cic != -1) {
            for (int i = 0; i < cic; i++) {
                customInstruments.add(new CustomInstrumentImpl(readString(stream), readString(stream), stream.read(), readBoolean(stream)));
            }
        }
    }

    private static int readSignedShort(InputStream stream) throws IOException {
        return stream.read() + (stream.read() << 8);
    }

    private static boolean readBoolean(InputStream stream) throws IOException {
        return stream.read() >= 1;
    }

    private static String readString(InputStream stream) throws IOException {
        int len = readInt(stream);

        if (len < 0)
            throw new IOException("Invalid string length");

        byte[] dat = new byte[len];
        int ren = stream.read(dat);

        if (ren < 0)
            throw new IOException("String length mismatch");

        return new String(dat, StandardCharsets.UTF_8);
    }

    private static int readInt(InputStream stream) throws IOException {
        return stream.read() + (stream.read() << 8) + (stream.read() << 16) + (stream.read() << 24);
    }

    private static int readShort(InputStream stream) throws IOException {
        return (stream.read()) + (stream.read() << 8);
    }


    @Override
    public @NotNull List<Layer> getLayers() {
        return Collections.unmodifiableList(layers);
    }

    @NotNull
    @Override
    public List<CustomInstrument> getCustomInstruments() {
        return Collections.unmodifiableList(customInstruments);
    }

    @Override
    public boolean isOld() {
        return old;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public int getVanillaInstrumentCount() {
        return vanillaInstrumentCount;
    }

    @Override
    public int getSongLength() {
        return songLength;
    }

    @Override
    public int getLayerCount() {
        return layerCount;
    }

    public int getActualLayerCount() {
        return layers.size();
    }

    @Override
    public Layer getLayer(int num) {
        return layers.get(num);
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String getAuthor() {
        return author;
    }

    @Override
    public @NotNull String getOriginalAuthor() {
        return originalAuthor;
    }

    @Override
    public @NotNull String getDescription() {
        return description;
    }

    @Override
    public int getTempo() {
        return tempo;
    }

    @Override
    public boolean isAutoSaving() {
        return autoSaving;
    }

    @Override
    public int getAutoSavingDuration() {
        return autoSavingDuration;
    }

    @Override
    public int getTimeSignature() {
        return timeSignature;
    }

    @Override
    public int getMinutesSpent() {
        return minutesSpent;
    }

    @Override
    public int getLeftClicks() {
        return leftClicks;
    }

    @Override
    public int getRightClicks() {
        return rightClicks;
    }

    @Override
    public int getNoteBlocksAdded() {
        return noteBlocksAdded;
    }

    @Override
    public int getNoteBlocksRemoved() {
        return noteBlocksRemoved;
    }

    @Override
    public @NotNull String getImportFileName() {
        return importFileName;
    }

    @Override
    public boolean isLoop() {
        return loop;
    }

    @Override
    public int getLoopCount() {
        return loopCount;
    }

    @Override
    public int getLoopStart() {
        return loopStart;
    }

    @Override
    public String toString() {
        return "NBSImpl{" +
                "old=" + old +
                ", version=" + version +
                ", vanillaInstrumentCount=" + vanillaInstrumentCount +
                ", songLength=" + songLength +
                ", layerCount=" + layerCount +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", originalAuthor='" + originalAuthor + '\'' +
                ", description='" + description + '\'' +
                ", tempo=" + tempo +
                ", autoSaving=" + autoSaving +
                ", autoSavingDuration=" + autoSavingDuration +
                ", timeSignature=" + timeSignature +
                ", minutesSpent=" + minutesSpent +
                ", leftClicks=" + leftClicks +
                ", rightClicks=" + rightClicks +
                ", noteBlocksAdded=" + noteBlocksAdded +
                ", noteBlocksRemoved=" + noteBlocksRemoved +
                ", importFileName='" + importFileName + '\'' +
                ", loop=" + loop +
                ", loopCount=" + loopCount +
                ", loopStart=" + loopStart +
                ", layers=" + layers.size() +
                ", customInstruments=" + customInstruments.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NBSImpl nbs = (NBSImpl) o;
        return old == nbs.old && version == nbs.version && vanillaInstrumentCount == nbs.vanillaInstrumentCount && songLength == nbs.songLength && layerCount == nbs.layerCount && tempo == nbs.tempo && autoSaving == nbs.autoSaving && autoSavingDuration == nbs.autoSavingDuration && timeSignature == nbs.timeSignature && minutesSpent == nbs.minutesSpent && leftClicks == nbs.leftClicks && rightClicks == nbs.rightClicks && noteBlocksAdded == nbs.noteBlocksAdded && noteBlocksRemoved == nbs.noteBlocksRemoved && loop == nbs.loop && loopCount == nbs.loopCount && loopStart == nbs.loopStart && Objects.equals(name, nbs.name) && Objects.equals(author, nbs.author) && Objects.equals(originalAuthor, nbs.originalAuthor) && Objects.equals(description, nbs.description) && Objects.equals(importFileName, nbs.importFileName) && Objects.equals(layers, nbs.layers) && Objects.equals(customInstruments, nbs.customInstruments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(old, version, vanillaInstrumentCount, songLength, layerCount, name, author, originalAuthor, description, tempo, autoSaving, autoSavingDuration, timeSignature, minutesSpent, leftClicks, rightClicks, noteBlocksAdded, noteBlocksRemoved, importFileName, loop, loopCount, loopStart, layers, customInstruments);
    }
}
