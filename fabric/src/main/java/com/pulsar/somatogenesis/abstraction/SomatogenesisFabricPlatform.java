package com.pulsar.somatogenesis.abstraction;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.function.Supplier;

@AutoService(SomatogenesisPlatform.class)
public class SomatogenesisFabricPlatform implements SomatogenesisPlatform {
    @Override
    public Loader getLoader() {
        return Loader.FABRIC;
    }

    @Override
    public boolean isPhysicalClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public <T> RegistrationProvider<T> createRegistrationProvider(ResourceKey<? extends Registry<T>> key, String namespace) {
        return new RegistrationProvider<T>() {
            private final Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(key.location());

            @Override
            public <I extends T> RegistryObject<T, I> register(String id, Supplier<? extends I> supplier) {
                ResourceLocation location = new ResourceLocation(namespace, id);
                ResourceKey<I> resourceKey = (ResourceKey<I>)ResourceKey.create(registry.key(), location);
                I object = Registry.register(registry, location, supplier.get());
                return new RegistryObject<>() {
                    @Override
                    public ResourceKey<I> getResourceKey() {
                        return resourceKey;
                    }

                    @Override
                    public ResourceLocation getId() {
                        return location;
                    }

                    @Override
                    public I get() {
                        return object;
                    }
                };
            }
        };
    }

    @Override
    public <T> RegistrationProvider<T> createNewRegistryProvider(ResourceKey<? extends Registry<T>> key, String namespace) {
        MappedRegistry<T> mappedRegistry = new MappedRegistry<>(key, Lifecycle.stable(), false);
        FabricRegistryBuilder<T, MappedRegistry<T>> builder = FabricRegistryBuilder.from(mappedRegistry);
        builder.attribute(RegistryAttribute.SYNCED);
        builder.buildAndRegister();
        return createRegistrationProvider(key, namespace);
    }
}