package com.pulsar.somatogenesis.registry;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.effect.HemorrhagedEffect;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class SomatogenesisEffects {
    public static DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Somatogenesis.MOD_ID, Registries.MOB_EFFECT);

    public static RegistrySupplier<MobEffect> HEMORRHAGED = EFFECTS.register("hemorrhaged", HemorrhagedEffect::new);
}
