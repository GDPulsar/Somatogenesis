package com.pulsar.somatogenesis.registry;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.item.*;
import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class SomatogenesisItems {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(Somatogenesis.MOD_ID, Registries.ITEM);

    public static RegistrySupplier<BloodVialItem> BLOOD_VIAL = ITEMS.register("blood_vial", () -> new BloodVialItem(
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));
    public static RegistrySupplier<BloodCapsuleItem> BLOOD_CAPSULE = ITEMS.register("blood_capsule", () -> new BloodCapsuleItem(
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));
    public static RegistrySupplier<BloodTankItem> BLOOD_TANK = ITEMS.register("blood_tank", () -> new BloodTankItem(
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));

    public static RegistrySupplier<SyringeItem> SYRINGE = ITEMS.register("syringe", () -> new SyringeItem(
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));
    public static RegistrySupplier<BloodMirrorItem> BLOOD_MIRROR = ITEMS.register("blood_mirror", () -> new BloodMirrorItem(
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));

    public static Tier BONE_MATERIAL = new Tier() {
        @Override
        public int getUses() { return 180; }

        @Override
        public float getSpeed() { return 6f; }

        @Override
        public float getAttackDamageBonus() { return 1f; }

        @Override
        public int getLevel() { return 1; }

        @Override
        public int getEnchantmentValue() { return 12; }

        @Override
        public @NotNull Ingredient getRepairIngredient() { return Ingredient.of(Items.BONE); }
    };

    public static RegistrySupplier<SwordItem> BONE_SWORD = ITEMS.register("bone_sword", () -> new SwordItem(BONE_MATERIAL, 5, -2.9f,
            new Item.Properties().stacksTo(1).rarity(Rarity.COMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)) {
        @Override
        public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.putAll(super.getDefaultAttributeModifiers(equipmentSlot));
            if (equipmentSlot == EquipmentSlot.MAINHAND) builder.put(SomatogenesisAttributes.BLOODLETTING.get(), new AttributeModifier(UUID.fromString("b8a5e2f8-d2db-4bfe-b4ca-1cdfbf4f7771"), "Bone Tool", 1.0d, AttributeModifier.Operation.MULTIPLY_TOTAL));
            return builder.build();
        }
    });
    public static RegistrySupplier<PickaxeItem> BONE_PICKAXE = ITEMS.register("bone_pickaxe", () -> new PickaxeItem(BONE_MATERIAL, 0, -2.5f,
            new Item.Properties().stacksTo(1).rarity(Rarity.COMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)) {
        @Override
        public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.putAll(super.getDefaultAttributeModifiers(equipmentSlot));
            if (equipmentSlot == EquipmentSlot.MAINHAND) builder.put(SomatogenesisAttributes.BLOODLETTING.get(), new AttributeModifier(UUID.fromString("b8a5e2f8-d2db-4bfe-b4ca-1cdfbf4f7771"), "Bone Tool", 1.0d, AttributeModifier.Operation.MULTIPLY_TOTAL));
            return builder.build();
        }
    });
    public static RegistrySupplier<AxeItem> BONE_AXE = ITEMS.register("bone_axe", () -> new AxeItem(BONE_MATERIAL, 7, -3.2f,
            new Item.Properties().stacksTo(1).rarity(Rarity.COMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)) {
        @Override
        public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.putAll(super.getDefaultAttributeModifiers(equipmentSlot));
            if (equipmentSlot == EquipmentSlot.MAINHAND) builder.put(SomatogenesisAttributes.BLOODLETTING.get(), new AttributeModifier(UUID.fromString("b8a5e2f8-d2db-4bfe-b4ca-1cdfbf4f7771"), "Bone Tool", 1.0d, AttributeModifier.Operation.MULTIPLY_TOTAL));
            return builder.build();
        }
    });
    public static RegistrySupplier<ShovelItem> BONE_SHOVEL = ITEMS.register("bone_shovel", () -> new ShovelItem(BONE_MATERIAL, 0, -1.5f,
            new Item.Properties().stacksTo(1).rarity(Rarity.COMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)) {
        @Override
        public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.putAll(super.getDefaultAttributeModifiers(equipmentSlot));
            if (equipmentSlot == EquipmentSlot.MAINHAND) builder.put(SomatogenesisAttributes.BLOODLETTING.get(), new AttributeModifier(UUID.fromString("b8a5e2f8-d2db-4bfe-b4ca-1cdfbf4f7771"), "Bone Tool", 1.0d, AttributeModifier.Operation.MULTIPLY_TOTAL));
            return builder.build();
        }
    });
    public static RegistrySupplier<HoeItem> BONE_HOE = ITEMS.register("bone_hoe", () -> new HoeItem(BONE_MATERIAL, 0, -2f,
            new Item.Properties().stacksTo(1).rarity(Rarity.COMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)) {
        @Override
        public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.putAll(super.getDefaultAttributeModifiers(equipmentSlot));
            if (equipmentSlot == EquipmentSlot.MAINHAND) builder.put(SomatogenesisAttributes.BLOODLETTING.get(), new AttributeModifier(UUID.fromString("b8a5e2f8-d2db-4bfe-b4ca-1cdfbf4f7771"), "Bone Tool", 1.0d, AttributeModifier.Operation.MULTIPLY_TOTAL));
            return builder.build();
        }
    });

    public static RegistrySupplier<Item> BLOOD_BUCKET = ITEMS.register("blood_bucket", () -> new ArchitecturyBucketItem(SomatogenesisFluids.BLOOD_SOURCE,
            new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET).rarity(Rarity.UNCOMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));
    public static RegistrySupplier<Item> EMPTY_CAPSULE = ITEMS.register("empty_capsule", () -> new Item(
            new Item.Properties().stacksTo(1).rarity(Rarity.COMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));
    public static RegistrySupplier<Item> EVOLUTIONARY_MIXTURE_CAPSULE = ITEMS.register("evolutionary_mixture_capsule", () -> new Item(
            new Item.Properties().stacksTo(1).craftRemainder(EMPTY_CAPSULE.get()).rarity(Rarity.UNCOMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));

    public static RegistrySupplier<Item> SIMPLE_BLOOD_GEM = ITEMS.register("simple_blood_gem", () -> new Item(
            new Item.Properties().rarity(Rarity.COMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));
    public static RegistrySupplier<Item> REFINED_BLOOD_GEM = ITEMS.register("refined_blood_gem", () -> new Item(
            new Item.Properties().rarity(Rarity.COMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));
    public static RegistrySupplier<Item> PRISTINE_BLOOD_GEM = ITEMS.register("pristine_blood_gem", () -> new Item(
            new Item.Properties().rarity(Rarity.COMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));
    public static RegistrySupplier<Item> INFESTED_BLOOD_GEM = ITEMS.register("infested_blood_gem", () -> new Item(
            new Item.Properties().rarity(Rarity.COMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));
    public static RegistrySupplier<Item> UNSTABLE_BLOOD_GEM = ITEMS.register("unstable_blood_gem", () -> new Item(
            new Item.Properties().rarity(Rarity.COMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));

    public static RegistrySupplier<Item> BASIC_CREATURE = ITEMS.register("basic_creature", () -> new ArchitecturySpawnEggItem(SomatogenesisEntities.BASIC_CREATURE,
            0, 0, new Item.Properties().rarity(Rarity.COMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));

    public static RegistrySupplier<Item> FLESH = ITEMS.register("flesh", () -> new Item(
            new Item.Properties().rarity(Rarity.COMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));
}
