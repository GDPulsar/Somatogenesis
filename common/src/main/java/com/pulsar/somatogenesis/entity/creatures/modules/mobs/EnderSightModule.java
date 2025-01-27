package com.pulsar.somatogenesis.entity.creatures.modules.mobs;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.entity.creatures.ModularCreatureEntity;
import com.pulsar.somatogenesis.entity.creatures.modules.CreatureModule;
import com.pulsar.somatogenesis.entity.creatures.modules.ModuleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class EnderSightModule implements CreatureModule {
    @Override
    public ResourceLocation getId() {
        return Somatogenesis.reloc("ender_sight");
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
        return 0.15;
    }

    @Override
    public boolean shouldUpdate(ModularCreatureEntity creature) {
        return false;
    }

    @Override
    public void update(ModularCreatureEntity creature) {

    }
}
