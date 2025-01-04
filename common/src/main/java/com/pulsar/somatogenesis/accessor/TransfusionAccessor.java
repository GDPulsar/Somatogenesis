package com.pulsar.somatogenesis.accessor;

import net.minecraft.world.entity.EntityType;

import java.util.HashMap;

public interface TransfusionAccessor {
    HashMap<EntityType<?>, Integer> somatogenesis$getBlood();
    Integer somatogenesis$getBloodOf(EntityType<?> type);
    void somatogenesis$addBlood(EntityType<?> type, Integer amount);
}
