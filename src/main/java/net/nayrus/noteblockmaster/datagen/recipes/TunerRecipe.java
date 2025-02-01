package net.nayrus.noteblockmaster.datagen.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.item.TunerItem;

public class TunerRecipe extends ShapedRecipe {

    final ItemStack result;

    public TunerRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification) {
        super(group, category, pattern, result, showNotification);
        this.result = result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remainingItems = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for (int i = 0; i < remainingItems.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof TunerItem) {
                remainingItems.set(i, ItemStack.EMPTY);
            } else if (!stack.isEmpty() && !stack.getCraftingRemainder().isEmpty()) {
                remainingItems.set(i, stack.getCraftingRemainder());
            }
        }
        return remainingItems;
    }

    @Override
    public RecipeSerializer<? extends ShapedRecipe> getSerializer() {
        return NoteBlockMaster.TUNER_RECIPE_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<TunerRecipe> {
        public static final MapCodec<TunerRecipe> CODEC = RecordCodecBuilder.mapCodec(
                p_340778_ -> p_340778_.group(
                                Codec.STRING.optionalFieldOf("group", "").forGetter(TunerRecipe::group),
                                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(TunerRecipe::category),
                                ShapedRecipePattern.MAP_CODEC.forGetter(p_311733_ -> p_311733_.pattern),
                                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(p_311730_ -> p_311730_.result),
                                Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(TunerRecipe::showNotification)
                        )
                        .apply(p_340778_, TunerRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, TunerRecipe> STREAM_CODEC = StreamCodec.of(
                TunerRecipe.Serializer::toNetwork, TunerRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<TunerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TunerRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static TunerRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String s = buffer.readUtf();
            CraftingBookCategory craftingbookcategory = buffer.readEnum(CraftingBookCategory.class);
            ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
            boolean flag = buffer.readBoolean();
            return new TunerRecipe(s, craftingbookcategory, shapedrecipepattern, itemstack, flag);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, TunerRecipe recipe) {
            buffer.writeUtf(recipe.group());
            buffer.writeEnum(recipe.category());
            ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeBoolean(recipe.showNotification());
        }
    }
}