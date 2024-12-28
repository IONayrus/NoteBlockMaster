package net.nayrus.betterbeats.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.nayrus.betterbeats.BetterBeats;
import net.nayrus.betterbeats.block.BlockRegistry;
import net.nayrus.betterbeats.item.ItemRegistry;
import net.nayrus.betterbeats.util.BetterTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class BetterItemTagProvider extends ItemTagsProvider {
    public BetterItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper helper) {
        super(output, lookupProvider, blockTags, BetterBeats.MOD_ID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BetterTags.Items.BETTER_BEATS)
                .add(ItemRegistry.BEATWAKER.get())
                .add(BlockRegistry.ADVANCED_NOTEBLOCK.asItem());
    }
}
