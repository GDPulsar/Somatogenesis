package com.pulsar.somatogenesis.progression.requirements;

import com.pulsar.somatogenesis.accessor.ProgressionAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class ProgressionUnlockRequirement implements ProgressionRequirement {
    private final ResourceLocation unlockId;
    private boolean unlocked = false;

    public ProgressionUnlockRequirement(ResourceLocation unlockId) {
        this.unlockId = unlockId;
    }

    @Override
    public void submit(Player player) {
        if (((ProgressionAccessor)player).somatogenesis$getProgression().unlocked(unlockId)) {
            unlocked = true;
        }
    }

    @Override
    public boolean completed(Player player) {
        return unlocked;
    }

    @Override
    public double progress(Player player) {
        return unlocked ? 1 : 0;
    }

    @Override
    public int submittedCount() {
        return unlocked ? 1 : 0;
    }

    @Override
    public void fromSubmittedCount(int submitted) {
        this.unlocked = submitted == 1;
    }

    @Override
    public void clear() {
        this.unlocked = false;
    }

    @Override
    public Component getText() {
        Player player = Minecraft.getInstance().player;
        return Component.translatable("progression_requirement.unlock.text", ((ProgressionAccessor)player).somatogenesis$getProgression().getUnlock(unlockId).getTitle());
    }
}
