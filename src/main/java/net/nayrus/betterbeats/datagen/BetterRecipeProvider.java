package net.nayrus.betterbeats.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.nayrus.betterbeats.block.BlockRegistry;
import net.nayrus.betterbeats.item.ItemRegistry;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class BetterRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public BetterRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemRegistry.BEATWAKER.get())
                .pattern("  N")
                .pattern("NN ")
                .pattern("I  ")
                .define('N', Tags.Items.NUGGETS_IRON).define('I', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockRegistry.ADVANCED_NOTEBLOCK.get())
                .pattern(" N ")
                .pattern("NBN")
                .pattern(" W ")
                .define('N', Tags.Items.NUGGETS_GOLD).define('B', Items.NOTE_BLOCK).define('W', ItemRegistry.BEATWAKER.get())
                .unlockedBy("has_iron", has(ItemRegistry.BEATWAKER)).save(recipeOutput);
    }
}
