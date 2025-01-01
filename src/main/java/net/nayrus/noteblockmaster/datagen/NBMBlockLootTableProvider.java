package net.nayrus.noteblockmaster.datagen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.nayrus.noteblockmaster.utils.Registry;

import java.util.Set;

public class NBMBlockLootTableProvider extends BlockLootSubProvider {

    protected NBMBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags() , registries);
    }

    @Override
    protected void generate() {
        dropSelf(Registry.ADVANCED_NOTEBLOCK.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Registry.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
