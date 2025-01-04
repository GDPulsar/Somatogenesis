package com.pulsar.somatogenesis.registry;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.recipe.*;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class SomatogenesisRecipes {
    public static DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Somatogenesis.MOD_ID, Registries.RECIPE_TYPE);
    public static DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Somatogenesis.MOD_ID, Registries.RECIPE_SERIALIZER);

    public static RegistrySupplier<RecipeType<BloodAltarRecipe>> BLOOD_ALTAR_TYPE = TYPES.register("blood_altar", BloodAltarRecipe.Type::new);
    public static RegistrySupplier<RecipeSerializer<BloodAltarRecipe>> BLOOD_ALTAR_SERIALIZER = SERIALIZERS.register("blood_altar", BloodAltarRecipe.Serializer::new);

    public static RegistrySupplier<RecipeType<EvolutionTankRecipe>> EVOLUTION_TANK_TYPE = TYPES.register("evolution_tank", EvolutionTankRecipe.Type::new);
    public static RegistrySupplier<RecipeSerializer<EvolutionTankRecipe>> EVOLUTION_TANK_SERIALIZER = SERIALIZERS.register("evolution_tank", EvolutionTankRecipe.Serializer::new);

    public static RegistrySupplier<RecipeType<BloodTransfusionRecipe>> BLOOD_TRANSFUSION_TYPE = TYPES.register("blood_transfusion", BloodTransfusionRecipe.Type::new);
    public static RegistrySupplier<RecipeSerializer<BloodTransfusionRecipe>> BLOOD_TRANSFUSION_SERIALIZER = SERIALIZERS.register("blood_transfusion", BloodTransfusionRecipe.Serializer::new);

    public static RegistrySupplier<RecipeType<CruncherRecipe>> CRUNCHER_TYPE = TYPES.register("cruncher", CruncherRecipe.Type::new);
    public static RegistrySupplier<RecipeSerializer<CruncherRecipe>> CRUNCHER_SERIALIZER = SERIALIZERS.register("cruncher", CruncherRecipe.Serializer::new);

    public static RegistrySupplier<RecipeType<DrainerRecipe>> DRAINER_TYPE = TYPES.register("drainer", DrainerRecipe.Type::new);
    public static RegistrySupplier<RecipeSerializer<DrainerRecipe>> DRAINER_SERIALIZER = SERIALIZERS.register("drainer", DrainerRecipe.Serializer::new);
}
