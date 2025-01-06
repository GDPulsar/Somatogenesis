package com.pulsar.somatogenesis.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.accessor.ProgressionAccessor;
import com.pulsar.somatogenesis.block.BloodAltarBlockEntity;
import com.pulsar.somatogenesis.registry.SomatogenesisRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class BloodAltarRecipe implements Recipe<BloodAltarBlockEntity> {
    private final ResourceLocation id;
    private final NonNullList<IngredientStack> ingredients;
    private final int blood;
    private final ItemStack result;
    private final Optional<ResourceLocation> unlock;

    public BloodAltarRecipe(ResourceLocation id, NonNullList<IngredientStack> ingredients, int blood, ItemStack result, Optional<ResourceLocation> unlock) {
        this.id = id;
        this.ingredients = ingredients;
        this.blood = blood;
        this.result = result;
        this.unlock = unlock;
    }

    public @NotNull NonNullList<IngredientStack> getIngredientList() {
        return this.ingredients;
    }

    public int getBlood() {
        return this.blood;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public boolean unlocked(Player player) {
        if (this.unlock.isPresent() && player != null) {
            return ((ProgressionAccessor) player).somatogenesis$getProgression().unlocked(this.unlock.get());
        }
        return true;
    }

    public boolean matches(BloodAltarBlockEntity blockEntity, Level level) {
        if (this.unlock.isPresent() && blockEntity.getOwner() != null) {
            if (!((ProgressionAccessor)blockEntity.getOwner()).somatogenesis$getProgression().unlocked(this.unlock.get())) {
                //Somatogenesis.LOGGER.info("recipe {} not unlocked", this.getId());
                return false;
            }
        }
        for (IngredientStack ingredient : this.getIngredientList()) {
            int amount = ingredient.count();
            for (int i = 0; i < blockEntity.getContainerSize(); i++) {
                if (ingredient.ingredient().test(blockEntity.getItem(i))) {
                    int reduction = Math.min(amount, blockEntity.getItem(i).getCount());
                    amount -= reduction;
                    if (amount == 0) break;
                }
            }
            if (amount != 0) {
                //Somatogenesis.LOGGER.info("recipe {} not enough ingredients", this.getId());
                return false;
            }
        }
        for (int i = 0; i < blockEntity.getContainerSize(); i++) {
            if (blockEntity.getItem(i).isEmpty()) continue;
            boolean valid = false;
            for (IngredientStack ingredient : this.getIngredientList()) {
                if (ingredient.ingredient().test(blockEntity.getItem(i))) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                //Somatogenesis.LOGGER.info("recipe {} too many ingredients", this.getId());
                return false;
            }
        }

        //if (this.blood > blockEntity.getBlood()) Somatogenesis.LOGGER.info("recipe {} not enough blood", this.getId());
        return this.blood <= blockEntity.getBlood();
    }

    @Override
    public @NotNull ItemStack assemble(BloodAltarBlockEntity container, RegistryAccess registryAccess) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.result;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return this.id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SomatogenesisRecipes.BLOOD_ALTAR_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return SomatogenesisRecipes.BLOOD_ALTAR_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BloodAltarRecipe> {
        @Override
        public @NotNull BloodAltarRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
            NonNullList<IngredientStack> ingredients = itemsFromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredients"));
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for blood altar recipe");
            } else if (ingredients.size() > 8) {
                throw new JsonParseException("Too many ingredients for blood altar recipe");
            } else {
                ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
                int blood = GsonHelper.getAsInt(jsonObject, "blood");
                Optional<ResourceLocation> unlock = Optional.ofNullable(jsonObject.has("unlock") ? ResourceLocation.tryParse(GsonHelper.getAsString(jsonObject, "unlock", "")) : null);
                return new BloodAltarRecipe(id, ingredients, blood, result, unlock);
            }
        }

        private static NonNullList<IngredientStack> itemsFromJson(JsonArray jsonArray) {
            NonNullList<IngredientStack> ingredients = NonNullList.create();

            for(int i = 0; i < jsonArray.size(); ++i) {
                IngredientStack ingredient = IngredientStack.fromJson(jsonArray.get(i).getAsJsonObject());
                if (!ingredient.ingredient().isEmpty()) {
                    ingredients.add(ingredient);
                }
            }

            return ingredients;
        }

        @Override
        public @NotNull BloodAltarRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int i = buf.readVarInt();
            NonNullList<IngredientStack> ingredients = NonNullList.withSize(i, IngredientStack.EMPTY);
            ingredients.replaceAll(ignored -> new IngredientStack(Ingredient.fromNetwork(buf), buf.readInt()));
            int blood = buf.readInt();
            ItemStack result = buf.readItem();
            Optional<ResourceLocation> unlock = Optional.ofNullable(ResourceLocation.tryParse(buf.readUtf()));
            return new BloodAltarRecipe(id, ingredients, blood, result, unlock);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, BloodAltarRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());
            for (IngredientStack ingredient : recipe.ingredients) {
                ingredient.ingredient().toNetwork(buf);
                buf.writeInt(ingredient.count());
            }
            buf.writeInt(recipe.blood);
            buf.writeItem(recipe.result);
            if (recipe.unlock.isPresent()) buf.writeUtf(recipe.unlock.toString());
            else buf.writeUtf("");
        }
    }

    public static class Type implements RecipeType<BloodAltarRecipe> {}
}
