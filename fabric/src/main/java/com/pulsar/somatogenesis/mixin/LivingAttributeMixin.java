package com.pulsar.somatogenesis.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.pulsar.somatogenesis.registry.SomatogenesisAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingAttributeMixin {
    @ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder somatogenesis$addAttributes(AttributeSupplier.Builder original) {
        return original.add(SomatogenesisAttributes.BLOODLETTING.get());
    }
}
