package com.pulsar.somatogenesis.entity.creatures.modules;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.entity.creatures.ModularCreatureEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class NervesModule implements CreatureModule {
    @Override
    public ResourceLocation getId() {
        return Somatogenesis.reloc("nerves");
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
    public void update(ModularCreatureEntity creature) {}

    public BlockState getStandingBlock(ModularCreatureEntity creature) {
        return creature.getBlockStateOn();
    }

    public NerveState getNerveState(ModularCreatureEntity creature) {
        if (creature.isFallFlying() || creature.fallDistance >= 3f) {
            return NerveState.WIND;
        } else if (creature.isOnFire() || creature.isInLava()) {
            return NerveState.HOT;
        } else if (creature.isFreezing() || creature.isInPowderSnow) {
            return NerveState.COLD;
        } else if (creature.getLastHurtByMobTimestamp() <= creature.tickCount - 50) {
            return NerveState.PAIN;
        } else {
            return NerveState.NONE;
        }
    }

    public enum NerveState {
        NONE,
        WIND,
        HOT,
        COLD,
        PAIN
    }
}
