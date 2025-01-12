package com.pulsar.somatogenesis.registry;

import com.pulsar.somatogenesis.entity.BasicCreatureEntity;
import com.pulsar.somatogenesis.entity.BurrowerEntity;
import com.pulsar.somatogenesis.entity.client.model.BasicCreatureModel;
import com.pulsar.somatogenesis.entity.client.model.BurrowerModel;
import com.pulsar.somatogenesis.entity.client.renderer.BasicCreatureRenderer;
import com.pulsar.somatogenesis.entity.client.renderer.BurrowerRenderer;
import com.pulsar.somatogenesis.entity.client.renderer.ModularCreatureRenderer;
import com.pulsar.somatogenesis.entity.creatures.ModularCreatureEntity;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class SomatogenesisEntities {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create("somatogenesis", Registries.ENTITY_TYPE);

    public static RegistrySupplier<EntityType<BurrowerEntity>> BURROWER = ENTITY_TYPES.register("burrower", () -> EntityType.Builder
            .<BurrowerEntity>of(BurrowerEntity::new, MobCategory.CREATURE).canSpawnFarFromPlayer().clientTrackingRange(20).updateInterval(1).sized(1.5f, 1f).build("burrower"));
    public static RegistrySupplier<EntityType<BasicCreatureEntity>> BASIC_CREATURE = ENTITY_TYPES.register("basic_creature", () -> EntityType.Builder
            .<BasicCreatureEntity>of(BasicCreatureEntity::new, MobCategory.CREATURE).canSpawnFarFromPlayer().clientTrackingRange(20).updateInterval(1).sized(0.5f, 1f).build("basic_creature"));
    public static RegistrySupplier<EntityType<ModularCreatureEntity>> MODULAR_CREATURE = ENTITY_TYPES.register("modular_creature", () -> EntityType.Builder
            .<ModularCreatureEntity>of(ModularCreatureEntity::new, MobCategory.CREATURE).canSpawnFarFromPlayer().clientTrackingRange(20).updateInterval(1).sized(0.5f, 1f).build("modular_creature"));

    public static void registerCommon() {
        EntityAttributeRegistry.register(SomatogenesisEntities.BURROWER, BurrowerEntity::createAttributes);
        EntityAttributeRegistry.register(SomatogenesisEntities.BASIC_CREATURE, BasicCreatureEntity::createAttributes);
        EntityAttributeRegistry.register(SomatogenesisEntities.MODULAR_CREATURE, ModularCreatureEntity::createAttributes);
    }

    public static void registerClient() {
        EntityModelLayerRegistry.register(BurrowerModel.LAYER_LOCATION, BurrowerModel::createBodyLayer);
        EntityModelLayerRegistry.register(BasicCreatureModel.LAYER_LOCATION, BasicCreatureModel::createBodyLayer);

        EntityRendererRegistry.register(SomatogenesisEntities.BURROWER, BurrowerRenderer::new);
        EntityRendererRegistry.register(SomatogenesisEntities.BASIC_CREATURE, BasicCreatureRenderer::new);
        EntityRendererRegistry.register(SomatogenesisEntities.MODULAR_CREATURE, ModularCreatureRenderer::new);
    }
}
