package net.nayrus.noteblockmaster.sound;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.nayrus.noteblockmaster.NoteBlockMaster.MOD_ID;

public class SoundRegistry {

    public static int SUSTAIN_LEVELS = 2;

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, MOD_ID);

    public static final Supplier<SoundEvent> SMITHING = SOUND_EVENTS.register("noteblock_upgrade", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID,"noteblock_upgrade")));

    public static final List<Supplier<SoundEvent>> SUSTAINED_HARP_SOUND = createSoundEvents("sustained_harp");
    public static final Supplier<SoundEvent> SUSTAINED_BASEDRUM_SOUND = SOUND_EVENTS.register("sustained_basedrum", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_basedrum")));
    public static final Supplier<SoundEvent> SUSTAINED_SNARE_SOUND = SOUND_EVENTS.register("sustained_snare", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_snare")));
    public static final Supplier<SoundEvent> SUSTAINED_HAT_SOUND = SOUND_EVENTS.register("sustained_hat", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_hat")));
    public static final List<Supplier<SoundEvent>> SUSTAINED_BASS_SOUND = createSoundEvents("sustained_bass");
    public static final List<Supplier<SoundEvent>> SUSTAINED_FLUTE_SOUND = createSoundEvents("sustained_flute");
    public static final Supplier<SoundEvent> SUSTAINED_BELL_SOUND = SOUND_EVENTS.register("sustained_bell", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_bell")));
    public static final Supplier<SoundEvent> SUSTAINED_GUITAR_SOUND = SOUND_EVENTS.register("sustained_guitar", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_guitar")));
    public static final Supplier<SoundEvent> SUSTAINED_CHIME_SOUND = SOUND_EVENTS.register("sustained_chime", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_chime")));
    public static final Supplier<SoundEvent> SUSTAINED_XYLOPHONE_SOUND = SOUND_EVENTS.register("sustained_xylophone", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_xylophone")));
    public static final Supplier<SoundEvent> SUSTAINED_IRON_XYLOPHONE_SOUND = SOUND_EVENTS.register("sustained_ironxylophone", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_ironxylophone")));
    public static final Supplier<SoundEvent> SUSTAINED_COW_BELL_SOUND = SOUND_EVENTS.register("sustained_cowbell", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_cowbell")));
    public static final Supplier<SoundEvent> SUSTAINED_DIDGERIDOO_SOUND = SOUND_EVENTS.register("sustained_didgeridoo", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_didgeridoo")));
    public static final Supplier<SoundEvent> SUSTAINED_BIT_SOUND = SOUND_EVENTS.register("sustained_bit", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_bit")));
    public static final Supplier<SoundEvent> SUSTAINED_BANJO_SOUND = SOUND_EVENTS.register("sustained_banjo", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_banjo")));
    public static final Supplier<SoundEvent> SUSTAINED_PLING_SOUND = SOUND_EVENTS.register("sustained_pling", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sustained_pling")));

    public static List<Supplier<SoundEvent>> createSoundEvents(String name){
        List<Supplier<SoundEvent>> list  = new ArrayList<>();
        for(int i = 1; i <= SUSTAIN_LEVELS; i++){
            int iRef = i;
            list.add(SOUND_EVENTS.register(name + iRef, () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MOD_ID, name+ iRef))));
        }
        return list;
    }

}
