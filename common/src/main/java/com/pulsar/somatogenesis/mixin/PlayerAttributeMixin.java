package com.pulsar.somatogenesis.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.pulsar.somatogenesis.registry.SomatogenesisAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Player.class)
public abstract class PlayerAttributeMixin extends LivingEntity {
    protected PlayerAttributeMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyReturnValue(method = "createAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder somatogenesis$addAttributes(AttributeSupplier.Builder original) {
        return original.add(SomatogenesisAttributes.TRADE_COST.get()).add(SomatogenesisAttributes.EXHAUSTION.get());
    }

    @ModifyArg(method = "causeFoodExhaustion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"))
    private float somatogenesis$exhaustionAttribute(float f) {
        return (float)(f * this.getAttributeValue(SomatogenesisAttributes.EXHAUSTION.get()));
    }
}
