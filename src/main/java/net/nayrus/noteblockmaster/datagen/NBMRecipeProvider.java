package net.nayrus.noteblockmaster.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.nayrus.noteblockmaster.datagen.recipes.TunerRecipeBuilder;
import net.nayrus.noteblockmaster.setup.NBMTags;
import net.nayrus.noteblockmaster.setup.Registry;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class NBMRecipeProvider extends RecipeProvider implements IConditionBuilder{

    protected NBMRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    public static class Runner extends RecipeProvider.Runner{

        protected Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new NBMRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "NBM Recipes";
        }
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, Registry.TEMPOTUNER.get())
                .pattern("  N")
                .pattern("NN ")
                .pattern("I  ")
                .define('N', Tags.Items.NUGGETS_IRON).define('I', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(this.output);
        shaped(RecipeCategory.MISC, Registry.ADVANCED_NOTEBLOCK.get())
                .pattern(" N ")
                .pattern("NBN")
                .pattern(" W ")
                .define('N', Tags.Items.NUGGETS_GOLD).define('B', Items.NOTE_BLOCK).define('W', NBMTags.Items.TUNERS)
                .unlockedBy("has_waker", has(Registry.TEMPOTUNER)).save(this.output);
        shaped(RecipeCategory.MISC, Registry.CORE, 9)
                .pattern("BIB")
                .pattern("IEI")
                .pattern("TIN")
                .define('I', Tags.Items.INGOTS_IRON).define('E', Items.ENDER_PEARL).define('B', Items.BLAZE_POWDER)
                .define('T', Registry.TEMPOTUNER).define('N', Registry.NOTETUNER)
                .unlockedBy("has_waker", has(Registry.TEMPOTUNER)).save(this.output);
        shaped(RecipeCategory.MISC, Registry.SUSTAIN, 4)
                .pattern(" L ")
                .pattern("BCB")
                .pattern(" L ")
                .define('C', Registry.CORE).define('B', Items.BLUE_DYE).define('L', Items.LAPIS_LAZULI)
                .unlockedBy("has_core", has(Registry.CORE)).save(this.output);
        shaped(RecipeCategory.MISC, Registry.VOLUME, 4)
                .pattern(" W ")
                .pattern("FCF")
                .pattern(" W ")
                .define('C', Registry.CORE).define('W', Items.WHITE_WOOL).define('F', Items.FEATHER)
                .unlockedBy("has_core", has(Registry.CORE)).save(this.output);

        shapeless(RecipeCategory.MISC, Registry.COMPOSER)
                .requires(NBMTags.Items.TUNERS)
                .requires(Items.PAPER)
                .requires(Items.LAPIS_LAZULI)
                .unlockedBy("has_waker", has(NBMTags.Items.TUNERS)).save(this.output);

        //TODO fix notetuner recipe
        TunerRecipeBuilder.shaped(RecipeCategory.MISC, Registry.NOTETUNER.get())
                .pattern("PG")
                .pattern("WP")
                .define('W', Registry.TEMPOTUNER).define('P', Items.PURPLE_DYE).define('G', Items.GHAST_TEAR)
                .unlockedBy("has_waker", has(Registry.TEMPOTUNER)).save(this.output);
    }

}
