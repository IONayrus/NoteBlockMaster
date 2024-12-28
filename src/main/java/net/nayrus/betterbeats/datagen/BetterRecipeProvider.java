package net.nayrus.betterbeats.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.nayrus.betterbeats.block.BlockRegistry;
import net.nayrus.betterbeats.datagen.recipes.WakerRecipeBuilder;
import net.nayrus.betterbeats.item.ItemRegistry;
import net.nayrus.betterbeats.util.BetterTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class BetterRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public BetterRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.BEAT_WAKER.get())
                .pattern("  N")
                .pattern("NN ")
                .pattern("I  ")
                .define('N', Tags.Items.NUGGETS_IRON).define('I', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.ADVANCED_NOTEBLOCK.get())
                .pattern(" N ")
                .pattern("NBN")
                .pattern(" W ")
                .define('N', Tags.Items.NUGGETS_GOLD).define('B', Items.NOTE_BLOCK).define('W', BetterTags.Items.BEATWAKERS)
                .unlockedBy("has_waker", has(ItemRegistry.BEAT_WAKER)).save(recipeOutput);

        WakerRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.NOTE_WAKER.get())
                .pattern("PG")
                .pattern("WP")
                .define('W', ItemRegistry.BEAT_WAKER).define('P', Items.PURPLE_DYE).define('G', Items.GHAST_TEAR)
                .unlockedBy("has_waker", has(ItemRegistry.BEAT_WAKER)).save(recipeOutput);
    }
}
