package com.pulsar.somatogenesis.compat.emi;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.compat.emi.recipes.BloodAltarEmiRecipe;
import com.pulsar.somatogenesis.registry.SomatogenesisBlocks;
import com.pulsar.somatogenesis.registry.SomatogenesisRecipes;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Function;

@EmiEntrypoint
public class SomatogenesisEmiPlugin implements EmiPlugin {
    public static final EmiRecipeCategory BLOOD_ALTAR = new EmiRecipeCategory(Somatogenesis.reloc("blood_altar"),
            EmiStack.of(SomatogenesisBlocks.TIER_1_BLOOD_ALTAR_ITEM.get()));

    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.addCategory(BLOOD_ALTAR);
        addAll(emiRegistry, SomatogenesisRecipes.BLOOD_ALTAR_TYPE.get(), BloodAltarEmiRecipe::new);
    }

    public <C extends Container, T extends Recipe<C>> void addAll(EmiRegistry registry, RecipeType<T> type, Function<T, EmiRecipe> constructor) {
        for (T recipe : registry.getRecipeManager().getAllRecipesFor(type)) {
            registry.addRecipe(constructor.apply(recipe));
        }
    }
}
