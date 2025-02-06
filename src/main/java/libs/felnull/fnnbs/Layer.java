package libs.felnull.fnnbs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

public interface Layer {
    int getStereo();

    int getVolume();

    boolean isLock();

    @NotNull
    String getName();

    default float getRawVolume() {
        return (float) getVolume() / 100f;
    }

    @Nullable
    Note getNote(int tick);

    @NotNull
    @Unmodifiable
    Map<Integer, Note> getNotes();
}
