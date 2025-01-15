package com.pulsar.somatogenesis.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.pulsar.somatogenesis.registry.SomatogenesisAttributes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingAttributeMixin {
    @Shadow public abstract double getAttributeValue(Attribute arg);

    @ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder somatogenesis$addAttributes(AttributeSupplier.Builder original) {
        return original.add(SomatogenesisAttributes.BLOODLETTING.get()).add(SomatogenesisAttributes.JUMP_POWER.get()).add(SomatogenesisAttributes.SWIM_SPEED.get()).add(SomatogenesisAttributes.BREATH_DURATION.get());
    }

    @ModifyReturnValue(method = "getJumpPower", at = @At("RETURN"))
    private float somatogenesis$jumpPowerAttribute(float original) {
        return (float)(original * this.getAttributeValue(SomatogenesisAttributes.JUMP_POWER.get()));
    }

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V", ordinal = 0))
    private float somatogenesis$swimSpeedAttribute(float original) {
        return (float)(original * this.getAttributeValue(SomatogenesisAttributes.SWIM_SPEED.get()));
    }

    @ModifyExpressionValue(method = "jumpInLiquid", at = @At(value = "CONSTANT", args = "doubleValue=0.03999999910593033"))
    private double somatogenesis$swimSpeedAttributeUp(double original) {
        return (float)(original * this.getAttributeValue(SomatogenesisAttributes.SWIM_SPEED.get()));
    }

    @ModifyExpressionValue(method = "goDownInWater", at = @At(value = "CONSTANT", args = "doubleValue=-0.03999999910593033"))
    private double somatogenesis$swimSpeedAttributeDown(double original) {
        return (float)(original * this.getAttributeValue(SomatogenesisAttributes.SWIM_SPEED.get()));
    }

    @Redirect(method = "decreaseAirSupply", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private int somatogenesis$breathDurationAttribute(RandomSource instance, int i) {
        return (int)(instance.nextFloat() * i * this.getAttributeValue(SomatogenesisAttributes.BREATH_DURATION.get()));
    }
}
