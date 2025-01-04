package com.pulsar.somatogenesis.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.accessor.ProgressionAccessor;
import com.pulsar.somatogenesis.block.BloodAltarBlockEntity;
import com.pulsar.somatogenesis.block.EvolutionTankBlockEntity;
import com.pulsar.somatogenesis.registry.SomatogenesisRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class EvolutionTankRecipe implements Recipe<EvolutionTankBlockEntity> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final NonNullList<IngredientStack> ingredients;
    private final int time;
    private final Optional<CompoundTag> addedNbt;
    private final ItemStack result;
    private final Optional<ResourceLocation> unlock;

    public EvolutionTankRecipe(ResourceLocation id, Ingredient input, NonNullList<IngredientStack> ingredients, int time,
                               Optional<CompoundTag> addedNbt, ItemStack result, Optional<ResourceLocation> unlock) {
        this.id = id;
        this.input = input;
        this.ingredients = ingredients;
        this.time = time;
        this.addedNbt = addedNbt;
        this.result = result;
        this.unlock = unlock;
    }

    public @NotNull Ingredient getInput() {
        return this.input;
    }

    public @NotNull NonNullList<IngredientStack> getIngredientList() {
        return this.ingredients;
    }

    public int getTime() {
        return this.time;
    }

    public ItemStack getResult() {
        ItemStack stack = this.result.copy();
        if (this.addedNbt.isPresent()) {
            for (String key : this.addedNbt.get().getAllKeys()) {
                stack.getOrCreateTag().put(key, Objects.requireNonNull(this.addedNbt.get().get(key)));
            }
        }
        return stack;
    }

    public boolean matches(EvolutionTankBlockEntity blockEntity, Level level) {
        if (this.unlock.isPresent() && blockEntity.getOwner() != null) {
            if (!((ProgressionAccessor)blockEntity.getOwner()).somatogenesis$getProgression().unlocked(this.unlock.get())) {
                return false;
            }
        }
        for (IngredientStack ingredient : this.getIngredientList()) {
            int amount = ingredient.count();
            for (int i = 1; i < blockEntity.getContainerSize(); i++) {
                if (ingredient.ingredient().test(blockEntity.getItem(i))) {
                    int reduction = Math.min(amount, blockEntity.getItem(i).getCount());
                    amount -= reduction;
                    if (amount == 0) break;
                }
            }
            if (amount != 0) return false;
        }
        for (int i = 0; i < blockEntity.getContainerSize(); i++) {
            boolean valid = false;
            for (IngredientStack ingredient : this.getIngredientList()) {
                if (ingredient.ingredient().test(blockEntity.getItem(i))) {
                    valid = true;
                    break;
                }
            }
            if (!valid) return false;
        }

        return this.input.test(blockEntity.getItem(0)) && blockEntity.getItem(0).getCount() == 1;
    }

    @Override
    public @NotNull ItemStack assemble(EvolutionTankBlockEntity container, RegistryAccess registryAccess) {
        ItemStack stack = this.result.copy();
        if (this.addedNbt.isPresent()) {
            for (String key : this.addedNbt.get().getAllKeys()) {
                stack.getOrCreateTag().put(key, Objects.requireNonNull(this.addedNbt.get().get(key)));
            }
        }
        return stack;
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
        return SomatogenesisRecipes.EVOLUTION_TANK_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return SomatogenesisRecipes.EVOLUTION_TANK_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<EvolutionTankRecipe> {
        @Override
        public @NotNull EvolutionTankRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
            NonNullList<IngredientStack> ingredients = itemsFromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredients"));
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for evolution tank recipe");
            } else if (ingredients.size() > 8) {
                throw new JsonParseException("Too many ingredients for evolution tank recipe");
            } else {
                ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
                Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "input"));
                int time = GsonHelper.getAsInt(jsonObject, "time");
                Optional<ResourceLocation> unlock = Optional.ofNullable(jsonObject.has("unlock") ? ResourceLocation.tryParse(GsonHelper.getAsString(jsonObject, "unlock", "")) : null);
                Optional<CompoundTag> addedNbt = Optional.ofNullable(jsonObject.has("addedNbt") ? (CompoundTag)JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, jsonObject) : null);
                return new EvolutionTankRecipe(id, input, ingredients, time, addedNbt, result, unlock);
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
        public @NotNull EvolutionTankRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int i = buf.readVarInt();
            NonNullList<IngredientStack> ingredients = NonNullList.withSize(i, IngredientStack.EMPTY);
            ingredients.replaceAll(ignored -> new IngredientStack(Ingredient.fromNetwork(buf), buf.readInt()));
            Ingredient input = Ingredient.fromNetwork(buf);
            int time = buf.readInt();
            ItemStack result = buf.readItem();
            Optional<ResourceLocation> unlock = Optional.ofNullable(ResourceLocation.tryParse(buf.readUtf()));
            Optional<CompoundTag> addedNbt = Optional.ofNullable(buf.readNbt());
            return new EvolutionTankRecipe(id, input, ingredients, time, addedNbt, result, unlock);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, EvolutionTankRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());
            for (IngredientStack ingredient : recipe.ingredients) {
                ingredient.ingredient().toNetwork(buf);
                buf.writeInt(ingredient.count());
            }
            recipe.input.toNetwork(buf);
            buf.writeInt(recipe.time);
            buf.writeItem(recipe.result);
            buf.writeUtf(recipe.unlock.isPresent() ? recipe.unlock.toString() : "");
            buf.writeNbt(recipe.addedNbt.orElse(null));
        }
    }

    public static class Type implements RecipeType<EvolutionTankRecipe> {}
}
