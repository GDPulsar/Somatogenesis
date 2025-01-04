package com.pulsar.somatogenesis.reload;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pulsar.somatogenesis.rune.Runes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RuneReloadListener extends SimpleJsonResourceReloadListener {
    public RuneReloadListener() {
        super(new GsonBuilder().setPrettyPrinting().create(), "runes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        for (Map.Entry<ResourceLocation, JsonElement> unlockData : object.entrySet()) {
            JsonObject json = unlockData.getValue().getAsJsonObject();
            JsonArray positions = GsonHelper.getAsJsonArray(json, "positions");
            List<Vector2i> drawPositions = new ArrayList<>();
            for (int i = 0; i < positions.size(); i++) {
                JsonArray posArray = positions.get(i).getAsJsonArray();
                drawPositions.add(new Vector2i(posArray.get(0).getAsInt(), posArray.get(1).getAsInt()));
            }
            Runes.add(unlockData.getKey(), new Runes.Rune(drawPositions, ResourceLocation.tryParse(GsonHelper.getAsString(json, "unlock"))));
        }
    }
}
