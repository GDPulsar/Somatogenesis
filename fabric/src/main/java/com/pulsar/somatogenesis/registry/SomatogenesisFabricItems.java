package com.pulsar.somatogenesis.registry;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.item.FabricBloodPendantItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class SomatogenesisFabricItems {
    public static Item BLOOD_PENDANT;

    public static void register() {
        BLOOD_PENDANT = register(new FabricBloodPendantItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)), "blood_pendant");
    }

    private static Item register(Item item, String id) {
        return Registry.register(BuiltInRegistries.ITEM, Somatogenesis.reloc(id), item);
    }
}
