package com.pulsar.somatogenesis.registry;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.menu.EvolutionTankMenu;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class SomatogenesisTabs {
    public static DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Somatogenesis.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static RegistrySupplier<CreativeModeTab> SOMATOGENESIS_TAB = TABS.register("somatogenesis", () -> CreativeTabRegistry.create(
            Component.translatable("category.somatogenesis"), () -> new ItemStack(SomatogenesisBlocks.TIER_1_BLOOD_ALTAR_ITEM.get())
    ));
}
