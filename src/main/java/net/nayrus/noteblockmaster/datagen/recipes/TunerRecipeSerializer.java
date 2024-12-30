package net.nayrus.noteblockmaster.datagen.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public class TunerRecipeSerializer implements RecipeSerializer<TunerRecipe> {

    public static final TunerRecipeSerializer INSTANCE = new TunerRecipeSerializer();

    @Override
    public MapCodec<TunerRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, TunerRecipe> streamCodec() {
        return STREAM_CODEC;
    }

    public static final MapCodec<TunerRecipe> CODEC = RecordCodecBuilder.mapCodec(
            p_340778_ -> p_340778_.group(
                            Codec.STRING.optionalFieldOf("group", "").forGetter(TunerRecipe::getGroup),
                            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(TunerRecipe::category),
                            ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
                            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.getResultItem(recipe.getDummyProvider())),
                            Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipe::showNotification)
                    )
                    .apply(p_340778_, TunerRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TunerRecipe> STREAM_CODEC = StreamCodec.of(
            TunerRecipeSerializer::toNetwork, TunerRecipeSerializer::fromNetwork
    );

    private static TunerRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        String s = buffer.readUtf();
        CraftingBookCategory craftingbookcategory = buffer.readEnum(CraftingBookCategory.class);
        ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
        ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
        boolean flag = buffer.readBoolean();
        return new TunerRecipe(s, craftingbookcategory, shapedrecipepattern, itemstack, flag);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, TunerRecipe recipe) {
        buffer.writeUtf(recipe.getGroup());
        buffer.writeEnum(recipe.category());
        ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
        ItemStack.STREAM_CODEC.encode(buffer, recipe.getResultItem(recipe.getDummyProvider()));
        buffer.writeBoolean(recipe.showNotification());
    }
}
