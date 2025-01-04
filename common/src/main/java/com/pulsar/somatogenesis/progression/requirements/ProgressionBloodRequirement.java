package com.pulsar.somatogenesis.progression.requirements;

import com.pulsar.somatogenesis.item.BloodContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ProgressionBloodRequirement implements ProgressionRequirement {
    private final int bloodRequirement;
    private int submitted = 0;

    public ProgressionBloodRequirement(int bloodRequirement) {
        this.bloodRequirement = bloodRequirement;
    }

    @Override
    public void submit(Player player) {
        List<ItemStack> containers = player.getInventory().items.stream().filter(stack -> stack.getItem() instanceof BloodContainer).toList();
        for (ItemStack container : containers) {
            if (container.getItem() instanceof BloodContainer bloodContainer) {
                int toReduce = bloodRequirement - submitted;
                int reduceBy = Math.min(toReduce, bloodContainer.getBlood(container));
                bloodContainer.useBlood(container, reduceBy);
                submitted += reduceBy;
            }
            if (submitted >= bloodRequirement) return;
        }
    }

    @Override
    public boolean completed(Player player) {
        return submitted >= bloodRequirement;
    }

    @Override
    public double progress(Player player) {
        return (double)submitted / bloodRequirement;
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
        return Component.translatable("progression_requirement.blood.text",
                bloodRequirement, submitted);
    }
}
