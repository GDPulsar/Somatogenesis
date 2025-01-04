package com.pulsar.somatogenesis.registry;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.abstraction.RegistrationProvider;
import com.pulsar.somatogenesis.abstraction.RegistryObject;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class SomatogenesisAttributes {
    public static Attribute make(final String name, final double base, final double min, final double max) {
        return new RangedAttribute("attribute.name.generic." + Somatogenesis.MOD_ID + "." + name, base, min, max).setSyncable(true);
    }

    public static final RegistrationProvider<Attribute> ATTRIBUTES = RegistrationProvider.get(Registries.ATTRIBUTE, Somatogenesis.MOD_ID);

    public static RegistryObject<Attribute, Attribute> BLOODLETTING = ATTRIBUTES.register("bloodletting", () -> make("bloodletting", 1, 0.0, 1024.0));
}
