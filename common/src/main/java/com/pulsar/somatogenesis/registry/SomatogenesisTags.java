package com.pulsar.somatogenesis.registry;

import com.pulsar.somatogenesis.Somatogenesis;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class SomatogenesisTags {
    public static TagKey<EntityType<?>> NO_BLOOD = TagKey.create(Registries.ENTITY_TYPE, Somatogenesis.reloc("no_blood"));
}
