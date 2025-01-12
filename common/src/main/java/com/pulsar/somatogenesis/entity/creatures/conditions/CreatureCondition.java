package com.pulsar.somatogenesis.entity.creatures.conditions;

import com.pulsar.somatogenesis.entity.creatures.ModularCreatureEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface CreatureCondition {
    ResourceLocation getId();
    CompoundTag writeNbt(CompoundTag nbt);
    void readNbt(CompoundTag nbt);

    boolean conditionMet(ModularCreatureEntity creature);
}
