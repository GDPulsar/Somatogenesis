package com.pulsar.somatogenesis.entity.creatures.modules;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.entity.creatures.ModularCreatureEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class BasicSightModule implements CreatureModule {
    @Override
    public ResourceLocation getId() {
        return Somatogenesis.reloc("basic_sight");
    }

    @Override
    public CompoundTag writeNbt(CompoundTag nbt) {
        return nbt;
    }

    @Override
    public void readNbt(CompoundTag nbt) {}

    @Override
    public ModuleType getType() {
        return ModuleType.SENSOR;
    }

    @Override
    public double getWeight() {
        return 0.1;
    }

    @Override
    public boolean shouldUpdate(ModularCreatureEntity creature) {
        return true;
    }

    @Override
    public void update(ModularCreatureEntity creature) {

    }
}
