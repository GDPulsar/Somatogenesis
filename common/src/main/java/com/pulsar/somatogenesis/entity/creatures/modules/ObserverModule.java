package com.pulsar.somatogenesis.entity.creatures.modules;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.entity.creatures.ModularCreatureEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class ObserverModule implements CreatureModule {
    @Override
    public ResourceLocation getId() {
        return Somatogenesis.reloc("observer");
    }

    @Override
    public CompoundTag writeNbt(CompoundTag nbt) {
        return nbt;
    }

    @Override
    public void readNbt(CompoundTag nbt) {}

    BlockPos lastPos = null;
    BlockState lastState = null;
    @Override
    public boolean shouldUpdate(ModularCreatureEntity creature) {
        BlockPos observing = BlockPos.containing(creature.position().add(creature.getForward()));
        if (observing.equals(lastPos)) {
            BlockState currentState = creature.level().getBlockState(observing);
            if (lastState == null) {
                lastState = currentState;
                return true;
            }
            if (!lastState.is(currentState.getBlock())) return true;
            for (Property<?> property : currentState.getValues().keySet()) {
                if (!lastState.hasProperty(property) || lastState.getValue(property) != currentState.getValue(property)) {
                    lastState = currentState;
                    return true;
                }
            }
            lastState = currentState;
            return false;
        } else {
            lastState = creature.level().getBlockState(observing);
            return true;
        }
    }

    @Override
    public void update(ModularCreatureEntity creature) {}
}
