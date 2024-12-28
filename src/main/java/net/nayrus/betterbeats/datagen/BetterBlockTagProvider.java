package net.nayrus.betterbeats.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.nayrus.betterbeats.BetterBeats;
import net.nayrus.betterbeats.block.BlockRegistry;
import net.nayrus.betterbeats.util.BetterTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BetterBlockTagProvider extends BlockTagsProvider {
    public BetterBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, BetterBeats.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BetterTags.Blocks.BETTER_BLOCKS)
                .add(BlockRegistry.ADVANCED_NOTEBLOCK.get());
    }
}
