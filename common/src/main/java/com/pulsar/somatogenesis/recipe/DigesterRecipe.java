package com.pulsar.somatogenesis.recipe;

import com.pulsar.somatogenesis.accessor.ProgressionAccessor;
import com.pulsar.somatogenesis.block.DigesterBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class DigesterRecipe implements Recipe<DigesterBlockEntity> {
    private final ResourceLocation id;
    private final NonNullList<IngredientStack> ingredients;
    private final int blood;
    private final ResourceLocation lootTable;
    private final Optional<ResourceLocation> unlock;

    public DigesterRecipe(ResourceLocation id, NonNullList<IngredientStack> ingredients, int blood, ResourceLocation lootTable, Optional<ResourceLocation> unlock) {
        this.id = id;
        this.ingredients = ingredients;
        this.blood = blood;
        this.lootTable = lootTable;
        this.unlock = unlock;
    }

    public @NotNull NonNullList<IngredientStack> getIngredientList() {
        return this.ingredients;
    }

    public int getBlood() {
        return this.blood;
    }

    public ResourceLocation getLootTable() {
        return this.lootTable;
    }

    public Optional<ResourceLocation> getUnlock() {
        return this.unlock;
    }

    public boolean matches(DigesterBlockEntity digester, Level level) {
        if (this.unlock.isPresent() && digester.getOwner() != null) {
            if (!((ProgressionAccessor)digester.getOwner()).somatogenesis$getProgression().unlocked(this.unlock.get())) {
                return false;
            }
        }
        for (IngredientStack ingredient : this.getIngredientList()) {
            int amount = ingredient.count();
            for (int i = 0; i < digester.getContainerSize(); i++) {
                if (ingredient.ingredient().test(digester.getItem(i))) {
                    int reduction = Math.min(amount, digester.getItem(i).getCount());
                    amount -= reduction;
                    if (amount == 0) break;
                }
            }
            if (amount != 0) {
                return false;
            }
        }
        for (int i = 0; i < digester.getContainerSize(); i++) {
            boolean valid = false;
            for (IngredientStack ingredient : this.getIngredientList()) {
                if (ingredient.ingredient().test(digester.getItem(i))) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                return false;
            }
        }

        return this.getBlood() <= digester.getBlood();
    }

    @Override
    public @NotNull ItemStack assemble(DigesterBlockEntity container, RegistryAccess registryAccess) {
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
}
