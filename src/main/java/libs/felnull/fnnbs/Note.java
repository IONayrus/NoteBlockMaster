package libs.felnull.fnnbs;

import libs.felnull.fnnbs.instrument.Instrument;
import libs.felnull.fnnbs.instrument.VanillaInstrument;
import org.jetbrains.annotations.NotNull;

public interface Note {
    int getInstrument();

    int getKey();

    int getPanning();

    int getPitch();

    int getVelocity();

    default float getRawVelocity() {
        return (float) getVelocity() / 100f;
    }

    @NotNull
    default Instrument getInstrument(NBS nbs) {
        if (nbs.getVanillaInstrumentCount() > getInstrument() && VanillaInstrument.values().length > getInstrument())
            return VanillaInstrument.values()[getInstrument()];

        int cc = getInstrument() - nbs.getVanillaInstrumentCount();
        return nbs.getCustomInstruments().get(cc);
    }
}
