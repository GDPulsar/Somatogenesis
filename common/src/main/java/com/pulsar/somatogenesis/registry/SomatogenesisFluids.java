package com.pulsar.somatogenesis.registry;

import com.pulsar.somatogenesis.Somatogenesis;
import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

public class SomatogenesisFluids {
    public static DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Somatogenesis.MOD_ID, Registries.FLUID);

    public static RegistrySupplier<FlowingFluid> BLOOD_SOURCE;
    public static RegistrySupplier<FlowingFluid> BLOOD_SOURCE_FLOWING;
    public static ArchitecturyFluidAttributes BLOOD_ATTRIBUTES = SimpleArchitecturyFluidAttributes.ofSupplier(() -> BLOOD_SOURCE_FLOWING, () -> BLOOD_SOURCE)
            .blockSupplier(() -> SomatogenesisBlocks.BLOOD_FLUID_BLOCK)
            .bucketItemSupplier(() -> SomatogenesisItems.BLOOD_BUCKET)
            .density(2000).sourceTexture(Somatogenesis.reloc("block/blood_still")).flowingTexture(Somatogenesis.reloc("block/blood_flow"))
            .viscosity(2500).slopeFindDistance(4).dropOff(2).explosionResistance(50f);

    public static RegistrySupplier<FlowingFluid> EVOLUTIONARY_MIXTURE;
    public static RegistrySupplier<FlowingFluid> EVOLUTIONARY_MIXTURE_FLOWING;
    public static ArchitecturyFluidAttributes EVOLUTIONARY_MIXTURE_ATTRIBUTES = SimpleArchitecturyFluidAttributes.ofSupplier(() -> EVOLUTIONARY_MIXTURE_FLOWING, () -> EVOLUTIONARY_MIXTURE)
            .blockSupplier(() -> SomatogenesisBlocks.EVOLUTIONARY_MIXTURE_FLUID_BLOCK)
            .bucketItemSupplier(() -> SomatogenesisItems.EVOLUTIONARY_MIXTURE_CAPSULE)
            .density(4000).sourceTexture(Somatogenesis.reloc("block/evolutionary_mixture_still")).flowingTexture(Somatogenesis.reloc("block/evolutionary_mixture_flow"))
            .viscosity(5000).slopeFindDistance(8).dropOff(1).explosionResistance(100f);

    public static void register() {
        BLOOD_SOURCE = FLUIDS.register("blood", () -> new ArchitecturyFlowingFluid.Source(BLOOD_ATTRIBUTES));
        BLOOD_SOURCE_FLOWING = FLUIDS.register("blood_flowing", () -> new ArchitecturyFlowingFluid.Flowing(BLOOD_ATTRIBUTES));

        EVOLUTIONARY_MIXTURE = FLUIDS.register("evolutionary_mixture", () -> new ArchitecturyFlowingFluid.Source(EVOLUTIONARY_MIXTURE_ATTRIBUTES));
        EVOLUTIONARY_MIXTURE_FLOWING = FLUIDS.register("evolutionary_mixture_flowing", () -> new ArchitecturyFlowingFluid.Flowing(EVOLUTIONARY_MIXTURE_ATTRIBUTES));
    }
}
