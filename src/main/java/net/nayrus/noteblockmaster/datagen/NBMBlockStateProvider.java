package net.nayrus.noteblockmaster.datagen;

import net.minecraft.data.PackOutput;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.utils.Registry;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class NBMBlockStateProvider extends BlockStateProvider {

    public NBMBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, NoteBlockMaster.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(Registry.ADVANCED_NOTEBLOCK);
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
