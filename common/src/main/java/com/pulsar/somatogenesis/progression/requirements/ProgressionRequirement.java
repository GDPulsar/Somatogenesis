package com.pulsar.somatogenesis.progression.requirements;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public interface ProgressionRequirement {
    void submit(Player player);
    boolean completed(Player player);
    double progress(Player player);
    int submittedCount();
    void fromSubmittedCount(int submitted);
    void clear();

    Component getText();
}
