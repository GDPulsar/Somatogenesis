package com.pulsar.somatogenesis.progression.requirements;

import com.mojang.datafixers.types.Func;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.Function;
import java.util.function.Supplier;

public class ProgressionFunctionRequirement implements ProgressionRequirement {
    private final Function<Player, Boolean> function;
    private boolean completed = false;

    public ProgressionFunctionRequirement(Function<Player, Boolean> function) {
        this.function = function;
    }

    @Override
    public void submit(Player player) {
        if (function.apply(player)) {
            completed = true;
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
        return Component.translatable("progression_requirement.function.text");
    }
}
