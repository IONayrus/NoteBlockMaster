package net.nayrus.noteblockmaster.datagen;

import net.minecraft.data.PackOutput;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.sound.SoundRegistry;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class NBMLangProvider extends LanguageProvider {

    public NBMLangProvider(PackOutput output) {
        super(output, NoteBlockMaster.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(Registry.ADVANCED_NOTEBLOCK.get(), "Advanced Note Block");
        add(Registry.TUNINGCORE.get(), "Tuning Core");
        add(Registry.TEMPOTUNER.get(), "Tempo Tuner");
        add(Registry.NOTETUNER.get(), "Note Tuner");
        add(Registry.COMPOSER.get(), "Composers Note");
        add(Registry.CORE.get(), "Core Base");
        add(Registry.SUSTAIN.get(), "Sustaining Core");
        add(Registry.VOLUME.get(), "Mixing Core");
        //add("sounds.noteblockmaster.noteblock_smithing", "Note block upgraded");
        add("key.categories.noteblockmaster.nbm", "Note Block Master");
        add("key.noteblockmaster.opengui", "Open GUI");
        //add(SoundRegistry.SMITHING.get().getLocation().toLanguageKey("sounds"), "Note block upgraded");
        addSounds();
    }

    public void addSounds(){
        SoundRegistry.SOUND_EVENTS.getEntries().forEach((sound) ->
                add(sound.get().getLocation().toLanguageKey("sounds"), sound.get().getLocation().getPath() + " sound"));
    }
}
