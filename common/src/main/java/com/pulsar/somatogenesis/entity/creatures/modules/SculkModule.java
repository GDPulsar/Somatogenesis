package com.pulsar.somatogenesis.entity.creatures.modules;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.entity.creatures.ModularCreatureEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class SculkModule implements CreatureModule {
    @Override
    public ResourceLocation getId() {
        return Somatogenesis.reloc("sculk");
    }

    @Override
    public CompoundTag writeNbt(CompoundTag nbt) {
        return nbt;
    }

    @Override
    public void readNbt(CompoundTag nbt) {}

    @Override
    public boolean shouldUpdate(ModularCreatureEntity creature) {
        return false;
    }

    @Override
    public void update(ModularCreatureEntity creature) {

    }
}
