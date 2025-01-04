package com.pulsar.somatogenesis.mixin;

import com.pulsar.somatogenesis.accessor.ProgressionAccessor;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerProgressionMixin {
    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void somatogenesis$restoreProgression(ServerPlayer serverPlayer, boolean bl, CallbackInfo ci) {
        ProgressionAccessor thisProgression = (ProgressionAccessor)(Object)this;
        ProgressionAccessor otherProgression = (ProgressionAccessor)serverPlayer;
        thisProgression.somatogenesis$copyFrom(otherProgression.somatogenesis$getProgression());
    }
}
