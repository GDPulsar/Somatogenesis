package com.pulsar.somatogenesis.progression.requirements;

import com.pulsar.somatogenesis.item.BloodContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ProgressionExperienceRequirement implements ProgressionRequirement {
    private final int experienceRequirement;
    private int submitted = 0;

    public ProgressionExperienceRequirement(int experienceRequirement) {
        this.experienceRequirement = experienceRequirement;
    }

    @Override
    public void submit(Player player) {
        int toSubmit = experienceRequirement - submitted;
        if (toSubmit > 0) {
            int removed = Math.min(player.experienceLevel, toSubmit);
            player.experienceLevel -= removed;
            submitted += removed;
        }
    }

    @Override
    public boolean completed(Player player) {
        return submitted >= experienceRequirement;
    }

    @Override
    public double progress(Player player) {
        return (double)submitted / experienceRequirement;
    }

    @Override
    public int submittedCount() {
        return submitted;
    }

    @Override
    public void fromSubmittedCount(int submitted) {
        this.submitted = submitted;
    }

    @Override
    public void clear() {
        this.submitted = 0;
    }

    @Override
    public Component getText() {
        return Component.translatable("progression_requirement.experience.text",
                experienceRequirement, submitted);
    }
}
