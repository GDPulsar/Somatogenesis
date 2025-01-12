package com.pulsar.somatogenesis.entity.creatures.modules;

import com.pulsar.somatogenesis.entity.creatures.ModularCreatureEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface CreatureModule {
    ResourceLocation getId();
    CompoundTag writeNbt(CompoundTag nbt);
    void readNbt(CompoundTag nbt);

    boolean shouldUpdate(ModularCreatureEntity creature);
    void update(ModularCreatureEntity creature);
}
