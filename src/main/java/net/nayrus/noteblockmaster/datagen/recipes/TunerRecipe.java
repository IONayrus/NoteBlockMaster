package net.nayrus.noteblockmaster.datagen.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.item.Tuner;

import java.util.Optional;
import java.util.stream.Stream;


public class TunerRecipe extends ShapedRecipe {

    public TunerRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification) {
        super(group, category, pattern, result, showNotification);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remainingItems = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for (int i = 0; i < remainingItems.size(); i++) {
            ItemStack stack = input.getItem(i);

            if (stack.getItem() instanceof Tuner) {
                // Consume this specific item
                remainingItems.set(i, ItemStack.EMPTY);
            } else if (!stack.isEmpty() && stack.hasCraftingRemainingItem()) {
                remainingItems.set(i, stack.getCraftingRemainingItem());
            }
        }

        return remainingItems;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NoteBlockMaster.TUNER_RECIPE_SERIALIZER.get();
    }

    public HolderLookup.Provider getDummyProvider(){
        return new HolderLookup.Provider() {
            @Override
            public Stream<ResourceKey<? extends Registry<?>>> listRegistries() {
                return Stream.empty();
            }

            @Override
            public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> registryKey) {
                return Optional.empty();
            }
        };
    }
}