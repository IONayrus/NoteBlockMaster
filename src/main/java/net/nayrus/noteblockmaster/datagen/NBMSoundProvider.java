package net.nayrus.noteblockmaster.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.setup.Registry;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

import java.util.function.Supplier;

public class NBMSoundProvider extends SoundDefinitionsProvider {

    protected NBMSoundProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, NoteBlockMaster.MOD_ID, helper);
    }

    @Override
    public void registerSounds() {
        add(Registry.SMITHING, SoundDefinition.definition()
                .with(sound(ResourceLocation.withDefaultNamespace("block/smithing_table/smithing_table2")))
                .subtitle("sounds.noteblockmaster.noteblock_smithing")
        );
        addBasicLocalSound(Registry.SUSTAINED_HARP_SOUND, "sustained_harp");
        addBasicLocalSound(Registry.SUSTAINED_BASS_SOUND, "sustained_bass");
        addBasicLocalSound(Registry.SUSTAINED_BASEDRUM_SOUND, "sustained_basedrum");
        addBasicLocalSound(Registry.SUSTAINED_BANJO_SOUND, "sustained_banjo");
        addBasicLocalSound(Registry.SUSTAINED_FLUTE_SOUND, "sustained_flute");
        addBasicLocalSound(Registry.SUSTAINED_SNARE_SOUND, "sustained_snare");
        addBasicLocalSound(Registry.SUSTAINED_HAT_SOUND, "sustained_hat");
        addBasicLocalSound(Registry.SUSTAINED_CHIME_SOUND, "sustained_chime");
        addBasicLocalSound(Registry.SUSTAINED_COW_BELL_SOUND, "sustained_cowbell");
        addBasicLocalSound(Registry.SUSTAINED_BIT_SOUND, "sustained_bit");
        addBasicLocalSound(Registry.SUSTAINED_PLING_SOUND, "sustained_pling");
        addBasicLocalSound(Registry.SUSTAINED_XYLOPHONE_SOUND, "sustained_xylophone");
        addBasicLocalSound(Registry.SUSTAINED_IRON_XYLOPHONE_SOUND, "sustained_ironxylophone");
        addBasicLocalSound(Registry.SUSTAINED_GUITAR_SOUND, "sustained_guitar");
        addBasicLocalSound(Registry.SUSTAINED_DIDGERIDOO_SOUND, "sustained_didgeridoo");
        addBasicLocalSound(Registry.SUSTAINED_BELL_SOUND, "sustained_bell");
    }

    private void addBasicLocalSound(Supplier<SoundEvent> sustainedSound, String name){
        add(sustainedSound, SoundDefinition.definition()
                .with(sound(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, name)))
                .subtitle("sounds.noteblockmaster.".concat(name))
        );
    }
}
