package com.pulsar.somatogenesis.progression.requirements;

import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class ProgressionAdvancementRequirement implements ProgressionRequirement {
    private final ResourceLocation advancementId;
    private boolean completed = false;

    public ProgressionAdvancementRequirement(ResourceLocation advancementId) {
        this.advancementId = advancementId;
    }

    @Override
    public void submit(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            ServerAdvancementManager advancementManager = Objects.requireNonNull(serverPlayer.getServer()).getAdvancements();
            Advancement advancement = advancementManager.getAdvancement(advancementId);
            if (advancement != null) {
                if (serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone()) {
                    completed = true;
                }
            }
        }
    }

    @Override
    public boolean completed(Player player) {
        return completed;
    }

    @Override
    public double progress(Player player) {
        return completed ? 1 : 0;
    }

    @Override
    public int submittedCount() {
        return completed ? 1 : 0;
    }

    @Override
    public void fromSubmittedCount(int submitted) {
        completed = submitted == 1;
    }

    @Override
    public void clear() {
        completed = false;
    }

    @Override
    public Component getText() {
        return Component.translatable("progression_requirement.advancement.text",
                Component.translatable("advancements." + advancementId.getPath().replace("/",".") + ".title"));
    }
}
