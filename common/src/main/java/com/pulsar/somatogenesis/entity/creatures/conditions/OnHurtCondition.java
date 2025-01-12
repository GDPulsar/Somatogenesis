package com.pulsar.somatogenesis.entity.creatures.conditions;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.entity.creatures.ModularCreatureEntity;
import com.pulsar.somatogenesis.entity.creatures.modules.NervesModule;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class OnHurtCondition implements CreatureCondition {
    @Override
    public ResourceLocation getId() {
        return Somatogenesis.reloc("on_hurt");
    }

    @Override
    public CompoundTag writeNbt(CompoundTag nbt) {
        return nbt;
    }

    @Override
    public void readNbt(CompoundTag nbt) {}

    @Override
    public boolean conditionMet(ModularCreatureEntity creature) {
        if (creature.hasModule(Somatogenesis.reloc("nerves"))) {
            if (creature.getModule(Somatogenesis.reloc("nerves")) instanceof NervesModule nervesModule) {
                return nervesModule.getNerveState(creature) == NervesModule.NerveState.PAIN;
            }
        }
        return false;
    }
}
