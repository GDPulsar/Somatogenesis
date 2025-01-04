package com.pulsar.somatogenesis.mixin;

import com.pulsar.somatogenesis.accessor.ProgressionAccessor;
import com.pulsar.somatogenesis.progression.ProgressionData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerProgressionMixin implements ProgressionAccessor {
    @Unique ProgressionData progressionData = new ProgressionData((Player)(Object)this);

    @Override
    public ProgressionData somatogenesis$getProgression() {
        return progressionData;
    }

    @Override
    public void somatogenesis$copyFrom(ProgressionData data) {
        this.progressionData = data;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void somatogenesis$saveProgressionData(CompoundTag nbt, CallbackInfo ci) {
        if (!nbt.contains("somatogenesis")) nbt.put("somatogenesis", new CompoundTag());
        nbt.getCompound("somatogenesis").put("progression", progressionData.writeNbt());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void somatogenesis$readProgressionData(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("somatogenesis")) {
            if (nbt.getCompound("somatogenesis").contains("progression")) {
                progressionData.readNbt(nbt.getCompound("somatogenesis").getCompound("progression"));
            }
        }
    }
}
