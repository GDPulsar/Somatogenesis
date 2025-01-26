package com.pulsar.somatogenesis.entity.creatures.modules.mobs;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.entity.creatures.ModularCreatureEntity;
import com.pulsar.somatogenesis.entity.creatures.modules.CreatureModule;
import com.pulsar.somatogenesis.entity.creatures.modules.ModuleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class CreeperAppendixModule implements CreatureModule {
    float strength = 2.5f;

    public float getStrength() {
        return strength;
    }

    @Override
    public ResourceLocation getId() {
        return Somatogenesis.reloc("creeper_appendix");
    }

    @Override
    public CompoundTag writeNbt(CompoundTag nbt) {
        nbt.putFloat("strength", strength);
        return nbt;
    }

    @Override
    public void readNbt(CompoundTag nbt) {
        strength = nbt.getFloat("strength");
    }

    @Override
    public ModuleType getType() {
        return ModuleType.ORGAN;
    }

    @Override
    public double getWeight() {
        return 0.5;
    }

    @Override
    public boolean shouldUpdate(ModularCreatureEntity creature) {
        return false;
    }

    @Override
    public void update(ModularCreatureEntity creature) {

    }
}
