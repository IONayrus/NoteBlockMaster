package net.nayrus.noteblockmaster.datagen;


import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.setup.Registry;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class NBMItemModelProvider extends ItemModelProvider {
    public NBMItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, NoteBlockMaster.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        handheldItem(Registry.TEMPOTUNER);
        handheldItem(Registry.NOTETUNER);

        basicItem(Registry.COMPOSER.get());
        basicItem(Registry.CORE.get());

        customRendered(Registry.SUSTAIN);
        customRendered(Registry.VOLUME);
    }

    private void handheldItem(DeferredItem<?> item) {
        withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/handheld")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID,"item/" + item.getId().getPath()));
    }

    private void customRendered(DeferredItem<?> item) {
        basicItem(item.get()).parent(new ModelFile.UncheckedModelFile("minecraft:builtin/entity"));
    }
}
