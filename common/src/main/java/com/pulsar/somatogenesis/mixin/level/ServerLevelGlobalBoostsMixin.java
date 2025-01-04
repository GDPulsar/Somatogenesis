package com.pulsar.somatogenesis.mixin.level;

import com.pulsar.somatogenesis.accessor.BloodMoonAccessor;
import com.pulsar.somatogenesis.accessor.GlobalBoostsAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelGlobalBoostsMixin extends Level {
    protected ServerLevelGlobalBoostsMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void somatogenesis$tickBloodMoon(CallbackInfo ci) {
        GlobalBoostsAccessor boostsAccessor = (GlobalBoostsAccessor)(Object)this;
        boostsAccessor.somatogenesis$tickBoosts();
    }
}
