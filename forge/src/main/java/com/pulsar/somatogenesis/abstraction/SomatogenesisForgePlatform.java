package com.pulsar.somatogenesis.abstraction;

import com.google.auto.service.AutoService;
import com.pulsar.somatogenesis.Somatogenesis;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@AutoService(SomatogenesisPlatform.class)
public class SomatogenesisForgePlatform implements SomatogenesisPlatform {
    public static final List<DeferredRegister<?>> REGISTERS = new ArrayList<>();
    public static final List<DeferredRegister<?>> NEW_REGISTRIES = new ArrayList<>();

    @Override
    public Loader getLoader() {
        return Loader.FORGE;
    }

    @Override
    public boolean isPhysicalClient() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public <T> RegistrationProvider<T> createRegistrationProvider(ResourceKey<? extends Registry<T>> key, String namespace) {
        ForgeRegistrationProvider<T> provider = new ForgeRegistrationProvider<>(key, null, namespace);
        if (!REGISTERS.contains(provider.deferredRegister)) {
            REGISTERS.add(provider.deferredRegister);
        }
        return provider;
    }

    @Override
    public <T> RegistrationProvider<T> createNewRegistryProvider(ResourceKey<? extends Registry<T>> key, String namespace) {
        DeferredRegister<T> registry = DeferredRegister.create(key, Somatogenesis.MOD_ID);
        ForgeRegistrationProvider<T> provider = new ForgeRegistrationProvider<>(key, registry, namespace);
        if (!REGISTERS.contains(provider.deferredRegister)) {
            REGISTERS.add(provider.deferredRegister);
        }
        NEW_REGISTRIES.add(registry);
        return provider;
    }

    static class ForgeRegistrationProvider<T> implements RegistrationProvider<T> {
        private final ResourceKey<? extends Registry<T>> key;
        private final DeferredRegister<T> deferredRegister;
        private final String namespace;

        ForgeRegistrationProvider(ResourceKey<? extends Registry<T>> key, DeferredRegister<T> registry, String namespace) {
            this.key = key;
            this.deferredRegister = registry;
            this.namespace = namespace;
        }

        @Override
        public <I extends T> RegistryObject<T, I> register(String id, Supplier<? extends I> supplier) {
            ResourceLocation location = new ResourceLocation(namespace, id);
            ResourceKey<I> resourceKey = (ResourceKey<I>)ResourceKey.create(key, location);
            net.minecraftforge.registries.RegistryObject<? extends I> obj = deferredRegister.register(id, supplier);
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
                    return obj.get();
                }
            };
        }
    }
}
