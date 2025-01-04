package com.pulsar.somatogenesis.compat.emi.recipes;

import com.pulsar.somatogenesis.compat.emi.SomatogenesisEmiPlugin;
import com.pulsar.somatogenesis.recipe.BloodAltarRecipe;
import com.pulsar.somatogenesis.registry.SomatogenesisBlocks;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class BloodAltarEmiRecipe extends BasicEmiRecipe {
    private final BloodAltarRecipe recipe;

    public BloodAltarEmiRecipe(BloodAltarRecipe recipe) {
        super(SomatogenesisEmiPlugin.BLOOD_ALTAR, recipe.getId(), 170, 60);
        inputs = new ArrayList<>();
        inputs.addAll(recipe.getIngredientList().stream().map(s -> EmiIngredient.of(s.ingredient(), s.count())).toList());
        outputs = List.of(EmiStack.of(recipe.getResult()));
        this.recipe = recipe;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(EmiStack.of(SomatogenesisBlocks.BLOOD_ALTAR.get()), 50, 25).drawBack(false);

        int startX = Math.max(20, 60 - inputs.size() * 10);
        for (int i = 0; i < inputs.size(); i++) {
            widgets.addSlot(inputs.get(i), startX + i * 20, 0);
        }

        int textWidth = Minecraft.getInstance().font.width(Component.literal(recipe.getBlood() + "mB"));
        widgets.addText(Component.literal(recipe.getBlood() + "mB"), 143 - textWidth / 2, 5, 0xFF0000, true);
        widgets.addSlot(outputs.get(0), 130, 20).large(true).recipeContext(this);
        widgets.addFillingArrow(100, 25, 4000);
    }

    @Override
    public boolean hideCraftable() {
        return recipe.unlocked(Minecraft.getInstance().player);
    }
}
