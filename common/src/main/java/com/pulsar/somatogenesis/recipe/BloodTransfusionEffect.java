package com.pulsar.somatogenesis.recipe;

import net.minecraft.world.entity.LivingEntity;

public interface BloodTransfusionEffect {
    void tick(LivingEntity living, double percent);
}
