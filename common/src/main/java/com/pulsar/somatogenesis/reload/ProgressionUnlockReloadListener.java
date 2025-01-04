package com.pulsar.somatogenesis.reload;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.progression.ProgressionUnlock;
import com.pulsar.somatogenesis.progression.ProgressionUnlocks;
import com.pulsar.somatogenesis.progression.requirements.*;
import com.pulsar.somatogenesis.recipe.IngredientStack;
import com.pulsar.somatogenesis.registry.SomatogenesisItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class ProgressionUnlockReloadListener extends SimpleJsonResourceReloadListener {
    public ProgressionUnlockReloadListener() {
        super(new GsonBuilder().setPrettyPrinting().create(), "progression");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        for (Map.Entry<ResourceLocation, JsonElement> unlockData : object.entrySet()) {
            try {
                JsonObject json = unlockData.getValue().getAsJsonObject();
                JsonArray parentsJson = GsonHelper.getAsJsonArray(json, "parents", new JsonArray(0));
                ResourceLocation[] parents = new ResourceLocation[parentsJson.size()];
                for (int i = 0; i < parentsJson.size(); i++) {
                    parents[i] = ResourceLocation.tryParse(parentsJson.get(i).getAsString());
                }
                JsonArray requirementsJson = GsonHelper.getAsJsonArray(json, "requirements");
                ProgressionRequirement[] requirements = new ProgressionRequirement[requirementsJson.size()];
                for (int i = 0; i < requirementsJson.size(); i++) {
                    requirements[i] = getRequirement(requirementsJson.get(i).getAsJsonObject());
                }
                ProgressionUnlocks.ALL_UNLOCKS.add(() -> new ProgressionUnlock(
                        unlockData.getKey(),
                        SomatogenesisItems.ITEMS.getRegistrar().get(ResourceLocation.tryParse(GsonHelper.getAsString(json, "icon"))),
                        GsonHelper.getAsInt(json, "x"),
                        GsonHelper.getAsInt(json, "y"),
                        parents,
                        requirements));
            } catch (Exception e) {
                Somatogenesis.LOGGER.warn("Invalid Progression Unlock: {}. Reason: {}", unlockData.getKey(), e.getMessage());
            }
        }
    }

    private static ProgressionRequirement getRequirement(JsonObject json) {
        String type = GsonHelper.getAsString(json, "type");
        return switch (type) {
            case "blood" -> new ProgressionBloodRequirement(GsonHelper.getAsInt(json, "blood"));
            case "experience" -> new ProgressionExperienceRequirement(GsonHelper.getAsInt(json, "experience"));
            case "item" -> new ProgressionItemRequirement(IngredientStack.fromJson(GsonHelper.getAsJsonObject(json, "ingredient")));
            case "unlock" -> new ProgressionUnlockRequirement(ResourceLocation.tryParse(GsonHelper.getAsString(json, "id")));
            case "advancement" -> new ProgressionAdvancementRequirement(ResourceLocation.tryParse(GsonHelper.getAsString(json, "advancement")));
            default -> null;
        };
    }
}
