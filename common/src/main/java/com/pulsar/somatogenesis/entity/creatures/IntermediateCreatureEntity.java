package com.pulsar.somatogenesis.entity.creatures;

import com.pulsar.somatogenesis.entity.creatures.modules.ModuleStats;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

public class IntermediateCreatureEntity extends ModularCreatureEntity {
    private final ModuleStats STATS = new ModuleStats(4, 8, 2, 2, 5.0);

    public IntermediateCreatureEntity(EntityType<ModularCreatureEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public ModuleStats getStats() {
        return STATS;
    }
}
