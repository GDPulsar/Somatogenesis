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
    public static RegistryObject<Attribute, Attribute> TRADE_COST = ATTRIBUTES.register("trade_cost", () -> make("trade_cost", 1, 0.0, 1024.0));
    public static RegistryObject<Attribute, Attribute> JUMP_POWER = ATTRIBUTES.register("jump_power", () -> make("jump_power", 1, 0.0, 1024.0));
    public static RegistryObject<Attribute, Attribute> SWIM_SPEED = ATTRIBUTES.register("swim_speed", () -> make("swim_speed", 1, 0.0, 1024.0));
    public static RegistryObject<Attribute, Attribute> EXHAUSTION = ATTRIBUTES.register("exhaustion", () -> make("exhaustion", 1, 0.0, 1024.0));
    public static RegistryObject<Attribute, Attribute> BREATH_DURATION = ATTRIBUTES.register("breath_duration", () -> make("breath_duration", 1, 0.0, 1024.0));
}
