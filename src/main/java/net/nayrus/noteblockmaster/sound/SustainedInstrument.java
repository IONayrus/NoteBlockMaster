package net.nayrus.noteblockmaster.sound;

import net.minecraft.sounds.SoundEvent;
import net.nayrus.noteblockmaster.utils.FinalTuple;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public enum SustainedInstrument {

    SUSTAINED_HARP(SoundRegistry.SUSTAINED_HARP_SOUND),
    SUSTAINED_BASS(SoundRegistry.SUSTAINED_BASS_SOUND),
    SUSTAINED_BASEDRUM(SoundRegistry.SUSTAINED_BASEDRUM_SOUND),
    SUSTAINED_SNARE(SoundRegistry.SUSTAINED_SNARE_SOUND),
    SUSTAINED_HAT(SoundRegistry.SUSTAINED_HAT_SOUND),
    SUSTAINED_GUITAR(SoundRegistry.SUSTAINED_GUITAR_SOUND),
    SUSTAINED_FLUTE(SoundRegistry.SUSTAINED_FLUTE_SOUND),
    SUSTAINED_BELL(SoundRegistry.SUSTAINED_BELL_SOUND),
    SUSTAINED_CHIME(SoundRegistry.SUSTAINED_CHIME_SOUND),
    SUSTAINED_XYLOPHONE(SoundRegistry.SUSTAINED_XYLOPHONE_SOUND),
    SUSTAINED_IRON_XYLOPHONE(SoundRegistry.SUSTAINED_IRON_XYLOPHONE_SOUND),
    SUSTAINED_COW_BELL(SoundRegistry.SUSTAINED_COW_BELL_SOUND),
    SUSTAINED_DIDGERIDOO(SoundRegistry.SUSTAINED_DIDGERIDOO_SOUND),
    SUSTAINED_BIT(SoundRegistry.SUSTAINED_BIT_SOUND),
    SUSTAINED_BANJO(SoundRegistry.SUSTAINED_BANJO_SOUND),
    SUSTAINED_PLING(SoundRegistry.SUSTAINED_PLING_SOUND);

    private final List<Supplier<SoundEvent>> soundEvents;
    private final int[] durations;

    SustainedInstrument(Supplier<SoundEvent> soundEvent){
        List<Supplier<SoundEvent>> list = new ArrayList<>();
        list.add(soundEvent);
        this.soundEvents = list;
        this.durations = new int[]{1000};
    }

    SustainedInstrument(FinalTuple<List<Supplier<SoundEvent>>, int[]> soundsWithDuration){
        this.soundEvents = soundsWithDuration.getA();
        this.durations =soundsWithDuration.getB();
    }

    public SoundEvent getSoundEvent(int susIndex) {
        return this.soundEvents.get(susIndex < this.soundEvents.size() ? susIndex : this.soundEvents.size()-1).get();
    }

    public int getSustainTime(int susIndex) {
        return this.durations[(susIndex < this.soundEvents.size() ? susIndex : this.soundEvents.size()-1)];
    }

    public int getSize(){
        return this.soundEvents.size();
    }
}
