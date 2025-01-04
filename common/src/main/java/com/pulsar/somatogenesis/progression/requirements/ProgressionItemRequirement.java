package com.pulsar.somatogenesis.progression.requirements;

import com.pulsar.somatogenesis.recipe.IngredientStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class ProgressionItemRequirement implements ProgressionRequirement {
    private final IngredientStack ingredient;
    private int submittedCount = 0;

    public ProgressionItemRequirement(IngredientStack ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public void submit(Player player) {
        int toSubmit = ingredient.count() - submittedCount;
        if (toSubmit > 0) {
            submittedCount += ContainerHelper.clearOrCountMatchingItems(player.getInventory(), ingredient.ingredient(), toSubmit, false);
        }
    }

    @Override
    public boolean completed(Player player) {
        return submittedCount >= ingredient.count();
    }

    @Override
    public double progress(Player player) {
        return 0;
    }

    @Override
    public int submittedCount() {
        return submittedCount;
    }

    @Override
    public void fromSubmittedCount(int submitted) {
        submittedCount = submitted;
    }

    @Override
    public void clear() {
        this.submittedCount = 0;
    }

    @Override
    public Component getText() {
        if (ingredient.ingredient().getItems().length == 0) return Component.literal("Something is wrong here...");
        Item item = ingredient.ingredient().getItems()[(Minecraft.getInstance().player.tickCount / 50) % ingredient.ingredient().getItems().length].getItem();
        return Component.translatable("progression_requirement.item.text",
                ingredient.count(), item.getDefaultInstance().getDisplayName(), submittedCount);
    }
}
