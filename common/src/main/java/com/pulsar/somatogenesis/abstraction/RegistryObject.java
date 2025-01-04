package com.pulsar.somatogenesis.abstraction;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface RegistryObject<R, T extends R> extends Supplier<T> {
    ResourceKey<T> getResourceKey();

    ResourceLocation getId();

    @Override
    T get();
}
