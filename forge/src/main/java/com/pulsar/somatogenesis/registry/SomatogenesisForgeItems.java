package com.pulsar.somatogenesis.registry;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.item.ForgeBloodPendantItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SomatogenesisForgeItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Somatogenesis.MOD_ID);

    public static final RegistryObject<Item> BLOOD_PENDANT = ITEMS.register("blood_pendant", () -> new ForgeBloodPendantItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
}
