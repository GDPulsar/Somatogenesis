package com.pulsar.somatogenesis.registry;

import com.pulsar.somatogenesis.criteria.TransfusionDangerCriteria;
import net.minecraft.advancements.CriteriaTriggers;

public class SomatogenesisAdvancements {
    public static final TransfusionDangerCriteria TRANSFUSION_DANGER = CriteriaTriggers.register(new TransfusionDangerCriteria());
}
