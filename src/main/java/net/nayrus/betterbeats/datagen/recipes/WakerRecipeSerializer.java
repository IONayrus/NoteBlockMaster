package net.nayrus.betterbeats.datagen.recipes;

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

public class WakerRecipeSerializer implements RecipeSerializer<WakerRecipe> {

    public static final  WakerRecipeSerializer INSTANCE = new  WakerRecipeSerializer();

    @Override
    public MapCodec<WakerRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, WakerRecipe> streamCodec() {
        return STREAM_CODEC;
    }

    public static final MapCodec<WakerRecipe> CODEC = RecordCodecBuilder.mapCodec(
            p_340778_ -> p_340778_.group(
                            Codec.STRING.optionalFieldOf("group", "").forGetter(WakerRecipe::getGroup),
                            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(WakerRecipe::category),
                            ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
                            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.getResultItem(recipe.getDummyProvider())),
                            Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipe::showNotification)
                    )
                    .apply(p_340778_, WakerRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, WakerRecipe> STREAM_CODEC = StreamCodec.of(
            WakerRecipeSerializer::toNetwork, WakerRecipeSerializer::fromNetwork
    );

    private static WakerRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        String s = buffer.readUtf();
        CraftingBookCategory craftingbookcategory = buffer.readEnum(CraftingBookCategory.class);
        ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
        ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
        boolean flag = buffer.readBoolean();
        return new WakerRecipe(s, craftingbookcategory, shapedrecipepattern, itemstack, flag);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, WakerRecipe recipe) {
        buffer.writeUtf(recipe.getGroup());
        buffer.writeEnum(recipe.category());
        ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
        ItemStack.STREAM_CODEC.encode(buffer, recipe.getResultItem(recipe.getDummyProvider()));
        buffer.writeBoolean(recipe.showNotification());
    }
}
