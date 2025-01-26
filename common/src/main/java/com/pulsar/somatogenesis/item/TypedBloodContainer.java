package com.pulsar.somatogenesis.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface TypedBloodContainer extends BloodContainer {
    EntityType<? extends LivingEntity> getBloodType(ItemStack stack);
    void setBloodType(ItemStack stack, EntityType<? extends LivingEntity> type);

    default void setTypeFrom(ItemStack stack, LivingEntity entity) {
        this.setBloodType(stack, (EntityType<? extends LivingEntity>)entity.getType());
    }
}
