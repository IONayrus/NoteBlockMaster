package net.nayrus.noteblockmaster.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.sound.SoundRegistry;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredBlock;

public class NBMLangProvider extends LanguageProvider {

    public NBMLangProvider(PackOutput output) {
        super(output, NoteBlockMaster.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addBlockWithItem(Registry.ADVANCED_NOTEBLOCK, "Advanced Note Block");
        addBlockWithItem(Registry.COMPOSER, "Composer");
        add(Registry.TUNINGCORE.get(), "Tuning Core");
        add(Registry.TEMPOTUNER.get(), "Tempo Tuner");
        add(Registry.NOTETUNER.get(), "Note Tuner");
        add(Registry.COMPOSITION.get(), "Composers Note");
        add(Registry.CORE.get(), "Core Base");
        add(Registry.SUSTAIN.get(), "Sustaining Core");
        add(Registry.VOLUME.get(), "Mixing Core");
        //add("sounds.noteblockmaster.noteblock_smithing", "Note block upgraded");
        add("key.categories.noteblockmaster.nbm", "Note Block Master");
        add("key.noteblockmaster.openoffhandgui", "Open Offhand GUI");
        //add(SoundRegistry.SMITHING.get().getLocation().toLanguageKey("sounds"), "Note block upgraded");
        addSounds();
        add("text.config.updated", "Updated local configs. Restart your client to apply");
        add("text.lowres.enable", "Activated low resolution render to save fps");
        add("text.lowres.disable", "Low resolution render deactivated");
        add("text.config.desync_warning", "[WARNING] Advanced Note Block info render may be partially disabled. Click here to synchronize your local config with the server.");
        add("text.config.save_sync_hovertext", "Synchronize & Safe conig");
    }

    public void addBlockWithItem(DeferredBlock<Block> block, String name) {
        add(block.get(), name);
        add(Registry.BLOCK_ITEMS.get(block).get(), name);
    }

    public void addSounds(){
        SoundRegistry.SOUND_EVENTS.getEntries().forEach((sound) ->
                add(sound.get().location().toLanguageKey("sounds"), sound.get().location().getPath() + " sound"));
    }
}
