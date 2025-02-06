package libs.felnull.fnnbs;

import libs.felnull.fnnbs.impl.NBSImpl;
import libs.felnull.fnnbs.instrument.CustomInstrument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * <a href="https://opennbs.org/nbs">NBS Format</a>
 */
public interface NBS {
    @NotNull
    static NBS load(InputStream stream) throws IOException {
        return new NBSImpl(stream);
    }

    boolean isOld();

    int getVersion();

    int getVanillaInstrumentCount();

    int getSongLength();

    int getLayerCount();

    int getActualLayerCount();

    Layer getLayer(int num);

    @NotNull
    String getName();

    @NotNull
    String getAuthor();

    @NotNull
    String getOriginalAuthor();

    @NotNull
    String getDescription();

    int getTempo();

    boolean isAutoSaving();

    int getAutoSavingDuration();

    int getTimeSignature();

    int getMinutesSpent();

    int getLeftClicks();

    int getRightClicks();

    int getNoteBlocksAdded();

    int getNoteBlocksRemoved();

    @NotNull
    String getImportFileName();

    boolean isLoop();

    int getLoopCount();

    int getLoopStart();

    @NotNull
    @Unmodifiable
    List<Layer> getLayers();

    @NotNull
    @Unmodifiable
    List<CustomInstrument> getCustomInstruments();
}
