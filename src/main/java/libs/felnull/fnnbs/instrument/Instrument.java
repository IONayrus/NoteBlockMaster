package libs.felnull.fnnbs.instrument;

import org.jetbrains.annotations.NotNull;

public interface Instrument {
    @NotNull
    String getSoundName();

    boolean isVanillaNote();

    float getDefaultPitch();
}
