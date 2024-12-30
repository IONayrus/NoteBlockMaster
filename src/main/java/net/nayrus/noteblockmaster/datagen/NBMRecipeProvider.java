package net.nayrus.noteblockmaster.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.nayrus.noteblockmaster.datagen.recipes.TunerRecipeBuilder;
import net.nayrus.noteblockmaster.util.Registry;
import net.nayrus.noteblockmaster.util.NBMTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class NBMRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public NBMRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registry.TEMPOTUNER.get())
                .pattern("  N")
                .pattern("NN ")
                .pattern("I  ")
                .define('N', Tags.Items.NUGGETS_IRON).define('I', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registry.ADVANCED_NOTEBLOCK.get())
                .pattern(" N ")
                .pattern("NBN")
                .pattern(" W ")
                .define('N', Tags.Items.NUGGETS_GOLD).define('B', Items.NOTE_BLOCK).define('W', NBMTags.Items.TUNERS)
                .unlockedBy("has_waker", has(Registry.TEMPOTUNER)).save(recipeOutput);

        TunerRecipeBuilder.shaped(RecipeCategory.MISC, Registry.NOTETUNER.get())
                .pattern("PG")
                .pattern("WP")
                .define('W', Registry.TEMPOTUNER).define('P', Items.PURPLE_DYE).define('G', Items.GHAST_TEAR)
                .unlockedBy("has_waker", has(Registry.TEMPOTUNER)).save(recipeOutput);
    }
}