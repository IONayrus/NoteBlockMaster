package net.nayrus.betterbeats.datagen;

import net.minecraft.data.PackOutput;
import net.nayrus.betterbeats.BetterBeats;
import net.nayrus.betterbeats.block.BlockRegistry;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class BetterBlockStateProvider extends BlockStateProvider {

    public BetterBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, BetterBeats.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(BlockRegistry.ADVANCED_NOTEBLOCK);
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
