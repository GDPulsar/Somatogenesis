package com.pulsar.somatogenesis.recipe;

import com.google.gson.JsonObject;
import com.pulsar.somatogenesis.block.DrainerBlockEntity;
import com.pulsar.somatogenesis.registry.SomatogenesisRecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DrainerRecipe implements Recipe<DrainerBlockEntity> {
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final int blood;

    public DrainerRecipe(ResourceLocation id, Ingredient ingredient, int blood) {
        this.id = id;
        this.ingredient = ingredient;
        this.blood = blood;
    }

    public @NotNull Ingredient getIngredient() {
        return this.ingredient;
    }

    public int getBlood() {
        return this.blood;
    }

    public boolean matches(DrainerBlockEntity drainer, Level level) {
        for (int i = 0; i < drainer.getContainerSize(); i++) {
            if (ingredient.test(drainer.getItem(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(DrainerBlockEntity container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SomatogenesisRecipes.DRAINER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return SomatogenesisRecipes.DRAINER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<DrainerRecipe> {
        @Override
        public @NotNull DrainerRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
            int blood = GsonHelper.getAsInt(jsonObject, "blood");
            return new DrainerRecipe(id, ingredient, blood);
        }

        @Override
        public @NotNull DrainerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient ingredient = Ingredient.fromNetwork(buf);
            int blood = buf.readInt();
            return new DrainerRecipe(id, ingredient, blood);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, DrainerRecipe recipe) {
            recipe.ingredient.toNetwork(buf);
            buf.writeInt(recipe.blood);
        }
    }

    public static class Type implements RecipeType<DrainerRecipe> {}
}
