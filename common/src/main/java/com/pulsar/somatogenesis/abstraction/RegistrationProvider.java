package com.pulsar.somatogenesis.abstraction;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public interface RegistrationProvider<T> {
    static <T> RegistrationProvider<T> get(ResourceKey<? extends Registry<T>> key, String namespace) {
        return SomatogenesisPlatform.INSTANCE.createRegistrationProvider(key, namespace);
    }

    static <T> RegistrationProvider<T> newRegistry(ResourceKey<? extends Registry<T>> key, String namespace) {
        return SomatogenesisPlatform.INSTANCE.createNewRegistryProvider(key, namespace);
    }

    <I extends T> RegistryObject<T, I> register(String id, Supplier<? extends I> supplier);
}
