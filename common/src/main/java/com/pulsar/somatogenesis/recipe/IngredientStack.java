package com.pulsar.somatogenesis.recipe;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

public record IngredientStack(Ingredient ingredient, int count) {
    public static IngredientStack EMPTY = new IngredientStack(Ingredient.EMPTY, 0);

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("ingredient", this.ingredient.toJson());
        json.addProperty("count", this.count);
        return json;
    }

    public static IngredientStack fromJson(JsonObject json) {
        return new IngredientStack(Ingredient.fromJson(json.get("ingredient")), GsonHelper.getAsInt(json, "count"));
    }
}
