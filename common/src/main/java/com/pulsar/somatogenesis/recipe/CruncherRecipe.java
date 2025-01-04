package com.pulsar.somatogenesis.recipe;

import com.google.gson.JsonObject;
import com.pulsar.somatogenesis.registry.SomatogenesisRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CruncherRecipe extends DigesterRecipe {
    public CruncherRecipe(ResourceLocation id, NonNullList<IngredientStack> ingredients, int blood, ResourceLocation lootTable, Optional<ResourceLocation> unlock) {
        super(id, ingredients, blood, lootTable, unlock);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SomatogenesisRecipes.CRUNCHER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return SomatogenesisRecipes.CRUNCHER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CruncherRecipe> {
        @Override
        public @NotNull CruncherRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
            IngredientStack ingredient = IngredientStack.fromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
            ResourceLocation lootTable = ResourceLocation.tryParse(GsonHelper.getAsString(jsonObject, "lootTable"));
            Optional<ResourceLocation> unlock = Optional.ofNullable(jsonObject.has("unlock") ? ResourceLocation.tryParse(GsonHelper.getAsString(jsonObject, "unlock", "")) : null);
            return new CruncherRecipe(id, NonNullList.withSize(1, ingredient), 0, lootTable, unlock);
        }

        @Override
        public @NotNull CruncherRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int i = buf.readVarInt();
            NonNullList<IngredientStack> ingredients = NonNullList.withSize(i, IngredientStack.EMPTY);
            ingredients.replaceAll(ignored -> new IngredientStack(Ingredient.fromNetwork(buf), buf.readInt()));
            ResourceLocation lootTable = buf.readResourceLocation();
            Optional<ResourceLocation> unlock = Optional.ofNullable(ResourceLocation.tryParse(buf.readUtf()));
            return new CruncherRecipe(id, ingredients, 0, lootTable, unlock);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CruncherRecipe recipe) {
            buf.writeVarInt(recipe.getIngredientList().size());
            for (IngredientStack ingredient : recipe.getIngredientList()) {
                ingredient.ingredient().toNetwork(buf);
                buf.writeInt(ingredient.count());
            }
            buf.writeResourceLocation(recipe.getLootTable());
            if (recipe.getUnlock().isPresent()) buf.writeUtf(recipe.getUnlock().toString());
            else buf.writeUtf("");
        }
    }

    public static class Type implements RecipeType<CruncherRecipe> {}
}
