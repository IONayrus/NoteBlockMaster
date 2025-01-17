package net.nayrus.noteblockmaster.sound;

import net.minecraft.sounds.SoundEvent;
import net.nayrus.noteblockmaster.setup.Registry;

import java.util.function.Supplier;

public enum SustainedInstrument {

    SUSTAINED_HARP(Registry.SUSTAINED_HARP_SOUND),
    SUSTAINED_BASEDRUM(Registry.SUSTAINED_BASEDRUM_SOUND),
    SUSTAINED_SNARE(Registry.SUSTAINED_SNARE_SOUND),
    SUSTAINED_HAT(Registry.SUSTAINED_HAT_SOUND),
    SUSTAINED_BASS(Registry.SUSTAINED_BASS_SOUND),
    SUSTAINED_FLUTE(Registry.SUSTAINED_FLUTE_SOUND),
    SUSTAINED_BELL(Registry.SUSTAINED_BELL_SOUND),
    SUSTAINED_GUITAR(Registry.SUSTAINED_GUITAR_SOUND),
    SUSTAINED_CHIME(Registry.SUSTAINED_CHIME_SOUND),
    SUSTAINED_XYLOPHONE(Registry.SUSTAINED_XYLOPHONE_SOUND),
    SUSTAINED_IRON_XYLOPHONE(Registry.SUSTAINED_IRON_XYLOPHONE_SOUND),
    SUSTAINED_COW_BELL(Registry.SUSTAINED_COW_BELL_SOUND),
    SUSTAINED_DIDGERIDOO(Registry.SUSTAINED_DIDGERIDOO_SOUND),
    SUSTAINED_BIT(Registry.SUSTAINED_BIT_SOUND),
    SUSTAINED_BANJO(Registry.SUSTAINED_BANJO_SOUND),
    SUSTAINED_PLING(Registry.SUSTAINED_PLING_SOUND);

    private final Supplier<SoundEvent> soundEvent;

    SustainedInstrument(Supplier<SoundEvent> soundEvent){
        this.soundEvent = soundEvent;
    }

    public SoundEvent getSoundEvent() {
        return this.soundEvent.get();
    }

}
