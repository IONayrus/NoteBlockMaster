package net.nayrus.betterbeats.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.nayrus.betterbeats.BetterBeats;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = BetterBeats.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        gen.addProvider(event.includeServer(), new LootTableProvider(output, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(BetterBlockLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));
        gen.addProvider(event.includeServer(), new BetterRecipeProvider(output, lookupProvider));

        BlockTagsProvider blockTagsProvider = new BetterBlockTagProvider(output, lookupProvider, helper);
        gen.addProvider(event.includeServer(), blockTagsProvider);
        gen.addProvider(event.includeServer(), new BetterItemTagProvider(output, lookupProvider, blockTagsProvider.contentsGetter(), helper));

        gen.addProvider(event.includeClient(), new BetterItemModelProvider(output, helper));
        gen.addProvider(event.includeClient(), new BetterBlockStateProvider(output, helper));
    }
}
