package net.nayrus.betterbeats.datagen;


import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.nayrus.betterbeats.BetterBeats;
import net.nayrus.betterbeats.item.ItemRegistry;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class BetterItemModelProvider extends ItemModelProvider {
    public BetterItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, BetterBeats.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        handheldItem(ItemRegistry.BEATWAKER);
    }

    private ItemModelBuilder handheldItem(DeferredItem<?> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/handheld")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(BetterBeats.MOD_ID,"item/" + item.getId().getPath()));
    }
}
