package com.pulsar.somatogenesis.abstraction;

import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.ServiceLoader;

public interface SomatogenesisPlatform {
    SomatogenesisPlatform INSTANCE = Util.make(() -> {
        final ServiceLoader<SomatogenesisPlatform> loader = ServiceLoader.load(SomatogenesisPlatform.class);
        final Iterator<SomatogenesisPlatform> iterator = loader.iterator();
        if (!iterator.hasNext()) {
            throw new RuntimeException("Platform instance not found!");
        } else {
            final SomatogenesisPlatform platform = iterator.next();
            if (iterator.hasNext()) {
                throw new RuntimeException("More than one platform instance was found!");
            }
            return platform;
        }
    });

    enum Loader {
        FORGE,
        FABRIC
    }

    Loader getLoader();

    boolean isPhysicalClient();

    Path getConfigDir();

    <T> RegistrationProvider<T> createRegistrationProvider(ResourceKey<? extends Registry<T>> key, String namespace);

    <T> RegistrationProvider<T> createNewRegistryProvider(ResourceKey<? extends Registry<T>> key, String namespace);
}