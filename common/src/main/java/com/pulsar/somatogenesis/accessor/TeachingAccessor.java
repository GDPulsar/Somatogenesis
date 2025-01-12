package com.pulsar.somatogenesis.accessor;

import com.pulsar.somatogenesis.entity.creatures.ModularCreatureEntity;

public interface TeachingAccessor {
    ModularCreatureEntity somatogenesis$getTrainingCreature();
    void somatogenesis$copyFrom(ModularCreatureEntity data);
}
