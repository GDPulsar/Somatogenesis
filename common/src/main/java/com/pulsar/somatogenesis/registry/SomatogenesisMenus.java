package com.pulsar.somatogenesis.registry;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.item.BloodCapsuleItem;
import com.pulsar.somatogenesis.item.BloodVialItem;
import com.pulsar.somatogenesis.menu.EvolutionTankMenu;
import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

public class SomatogenesisMenus {
    public static DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Somatogenesis.MOD_ID, Registries.MENU);

    public static RegistrySupplier<MenuType<EvolutionTankMenu>> EVOLUTION_TANK_MENU = MENUS.register("evolution_tank", () -> MenuRegistry.ofExtended(EvolutionTankMenu::new));
}
