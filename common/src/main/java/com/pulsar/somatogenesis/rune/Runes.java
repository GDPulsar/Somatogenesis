package com.pulsar.somatogenesis.rune;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.accessor.ProgressionAccessor;
import com.pulsar.somatogenesis.progression.ProgressionData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Runes {
    private static final HashMap<ResourceLocation, Rune> ALL_RUNES = new HashMap<>();

    public static void add(ResourceLocation id, Rune rune) {
        ALL_RUNES.put(id, rune);
    }

    public static Rune get(ResourceLocation id) {
        return ALL_RUNES.get(id);
    }

    public static ResourceLocation getId(Rune rune) {
        for (Map.Entry<ResourceLocation, Rune> entry : ALL_RUNES.entrySet()) {
            if (entry.getValue() == rune) return entry.getKey();
        }
        return null;
    }

    public static List<Rune> getAll() {
        return ALL_RUNES.values().stream().toList();
    }

    public static List<Rune> getAvailable(Player player) {
        ProgressionData progression = ((ProgressionAccessor)player).somatogenesis$getProgression();
        List<Rune> valid = new ArrayList<>();
        for (Rune rune : ALL_RUNES.values()) {
            if (progression.unlocked(rune.unlock())) valid.add(rune);
        }
        return valid;
    }

    public static Rune getBestMatch(List<Vector2i> drawn) {
        Rune best = null;
        double bestMatch = 0;
        for (Map.Entry<ResourceLocation, Rune> entry : ALL_RUNES.entrySet()) {
            Rune rune = entry.getValue();
            double matchPercent = getMatchPercent(drawn, rune);
            if (matchPercent > bestMatch) {
                best = rune;
                bestMatch = matchPercent;
            }
        }
        return best;
    }

    public static Rune getBestMatch(List<Vector2i> drawn, Player player) {
        Rune best = null;
        double bestMatch = 0;
        ProgressionData progression = ((ProgressionAccessor)player).somatogenesis$getProgression();
        for (Map.Entry<ResourceLocation, Rune> entry : ALL_RUNES.entrySet()) {
            if (progression.unlocked(entry.getValue().unlock())) {
                Rune rune = entry.getValue();
                double matchPercent = getMatchPercent(drawn, rune);
                if (matchPercent > bestMatch) {
                    best = rune;
                    bestMatch = matchPercent;
                }
            }
        }
        return best;
    }

    public static double getMatchPercent(List<Vector2i> drawn, Rune rune) {
        return Math.max(
                Math.max(
                        getMatchPercent(drawn, rune, 0),
                        getMatchPercent(drawn, rune, 1)
                ),
                Math.max(
                        getMatchPercent(drawn, rune, 2),
                        getMatchPercent(drawn, rune, 3)
                )
        );
    }

    public static double getMatchPercent(List<Vector2i> drawn, Rune rune, int rotation) {
        int minX = 0, minY = 0, maxX = 0, maxY = 0;
        for (Vector2i pos : drawn) {
            minX = Math.min(minX, pos.x);
            minY = Math.min(minY, pos.y);
            maxX = Math.max(maxX, pos.x);
            maxY = Math.max(maxY, pos.y);
        }
        int middleX = maxX - Mth.abs(minX);
        int middleY = maxY - Mth.abs(minY);
        int total = rune.positions().size();
        int matching = 0;
        for (Vector2i pos : drawn) {
            Vector2i test = switch (rotation) {
                case 1 -> new Vector2i(-pos.y, pos.x);
                case 2 -> new Vector2i(-pos.x, -pos.y);
                case 3 -> new Vector2i(pos.y, -pos.x);
                default -> pos;
            };
            if (rune.positions().contains(new Vector2i(middleX + test.x, middleY + test.y))) matching++;
        }
        double drawnPercent = (double)drawn.size() / total;
        if (drawnPercent > 1.0) drawnPercent = 2 - drawnPercent;
        return ((double)matching / drawn.size()) * drawnPercent;
    }

    public record Rune(List<Vector2i> positions, ResourceLocation unlock) {}
}
