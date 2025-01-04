package com.pulsar.somatogenesis.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public interface TypedBloodContainer extends BloodContainer {
    EntityType<? extends LivingEntity> getBloodType();
    void setBloodType(EntityType<? extends LivingEntity> type);

    void setTypeFrom(LivingEntity entity);
}
