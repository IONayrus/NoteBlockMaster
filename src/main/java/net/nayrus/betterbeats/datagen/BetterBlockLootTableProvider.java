package net.nayrus.betterbeats.datagen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.nayrus.betterbeats.block.BlockRegistry;

import java.util.Set;

public class BetterBlockLootTableProvider extends BlockLootSubProvider {

    protected BetterBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags() , registries);
    }

    @Override
    protected void generate() {
        dropSelf(BlockRegistry.ADVANCED_NOTEBLOCK.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BlockRegistry.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
