package com.pulsar.somatogenesis.accessor;

import com.pulsar.somatogenesis.progression.ProgressionData;

public interface ProgressionAccessor {
    ProgressionData somatogenesis$getProgression();
    void somatogenesis$copyFrom(ProgressionData data);
}
