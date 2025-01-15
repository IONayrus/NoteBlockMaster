package net.nayrus.noteblockmaster.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.setup.NBMTags;
import net.nayrus.noteblockmaster.setup.Registry;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class NBMItemTagProvider extends ItemTagsProvider {
    public NBMItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper helper) {
        super(output, lookupProvider, blockTags, NoteBlockMaster.MOD_ID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(NBMTags.Items.TUNERS).add(
                Registry.NOTETUNER.get(),
                Registry.TEMPOTUNER.get());
        tag(NBMTags.Items.CORES).add(
                Registry.SUSTAIN.get(),
                Registry.VOLUME.get());
    }
}
