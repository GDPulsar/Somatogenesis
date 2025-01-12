package com.pulsar.somatogenesis.mixin;

import com.pulsar.somatogenesis.accessor.TeachingAccessor;
import com.pulsar.somatogenesis.entity.creatures.ModularCreatureEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerTeachingMixin extends LivingEntity implements TeachingAccessor {
    protected PlayerTeachingMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    ModularCreatureEntity teachingCreature = null;

    @Override
    public ModularCreatureEntity somatogenesis$getTrainingCreature() {
        return teachingCreature;
    }

    @Override
    public void somatogenesis$copyFrom(ModularCreatureEntity data) {
        teachingCreature = data;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void somatogenesis$saveProgressionData(CompoundTag nbt, CallbackInfo ci) {
        if (!nbt.contains("somatogenesis")) nbt.put("somatogenesis", new CompoundTag());
        if (teachingCreature != null) nbt.getCompound("somatogenesis").put("teaching", teachingCreature.saveWithoutId(new CompoundTag()));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void somatogenesis$readProgressionData(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("somatogenesis")) {
            if (nbt.getCompound("somatogenesis").contains("progression")) {
                teachingCreature = new ModularCreatureEntity(this.level());
                teachingCreature.load(nbt.getCompound("somatogenesis").getCompound("progression"));
            }
        }
    }
}
