package com.pulsar.somatogenesis.mixin;

import com.mojang.datafixers.util.Pair;
import com.pulsar.somatogenesis.accessor.TransfusionAccessor;
import com.pulsar.somatogenesis.recipe.BloodTransfusionRecipe;
import com.pulsar.somatogenesis.registry.SomatogenesisEntities;
import com.pulsar.somatogenesis.registry.SomatogenesisRecipes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingTransfusionMixin extends Entity implements TransfusionAccessor {
    public LivingTransfusionMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public abstract void kill();

    @Shadow public abstract boolean addEffect(MobEffectInstance mobEffectInstance);

    @Shadow @Nullable public abstract AttributeInstance getAttribute(Attribute attribute);

    @Unique HashMap<EntityType<?>, Integer> blood = new HashMap<>();
    @Unique List<Pair<Attribute, AttributeModifier>> modifiers = new ArrayList<>();

    @Override
    public HashMap<EntityType<?>, Integer> somatogenesis$getBlood() {
        return blood;
    }

    @Override
    public Integer somatogenesis$getBloodOf(EntityType<?> type) {
        return blood.getOrDefault(type, 0);
    }

    @Override
    public void somatogenesis$addBlood(EntityType<?> type, Integer amount) {
        blood.put(type, somatogenesis$getBloodOf(type) + amount);
        if (blood.get(type) == 0) blood.remove(type);
    }

    @Unique private void somatogenesis$updateAttributes() {
        if (!this.level().isClientSide) {
            for (Pair<Attribute, AttributeModifier> modifier : modifiers) {
                this.getAttribute(modifier.getFirst()).removePermanentModifier(modifier.getSecond().getId());
            }
            for (Map.Entry<EntityType<?>, Integer> entry : blood.entrySet()) {
                List<BloodTransfusionRecipe> valid = this.level().getRecipeManager().getAllRecipesFor(SomatogenesisRecipes.BLOOD_TRANSFUSION_TYPE.get())
                        .stream().filter((recipe) -> recipe.matches(entry.getKey())).toList();
                if (!valid.isEmpty()) {
                    for (Map.Entry<Attribute, AttributeModifier> modifier : valid.get(0).modifiers.entrySet()) {
                        AttributeModifier scaled = new AttributeModifier(modifier.getValue().getId(), "Blood Transfusion Modifier",
                                modifier.getValue().getAmount() * entry.getValue(), modifier.getValue().getOperation());
                        this.getAttribute(modifier.getKey()).removePermanentModifier(scaled.getId());
                        this.getAttribute(modifier.getKey()).addPermanentModifier(scaled);
                        this.modifiers.add(new Pair<>(modifier.getKey(), scaled));
                    }
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void somatogenesis$tickBlood(CallbackInfo ci) {
        if (this.tickCount % 100 == 0) somatogenesis$updateAttributes();
        if (this.tickCount % 20 == 0) {
            double sheVampireOnMyTransfusionTilIIssues = 0;
            for (Map.Entry<EntityType<?>, Integer> entry : blood.entrySet()) {
                List<BloodTransfusionRecipe> valid = this.level().getRecipeManager().getAllRecipesFor(SomatogenesisRecipes.BLOOD_TRANSFUSION_TYPE.get())
                        .stream().filter((recipe) -> recipe.matches(entry.getKey())).toList();
                if (!valid.isEmpty()) {
                    BloodTransfusionRecipe recipe = valid.get(0);
                    sheVampireOnMyTransfusionTilIIssues += entry.getValue() / recipe.problemThreshold;
                }
            }
            if (sheVampireOnMyTransfusionTilIIssues >= 3f) {
                this.kill();
            } else if (sheVampireOnMyTransfusionTilIIssues >= 2f) {
                this.addEffect(new MobEffectInstance(MobEffects.WITHER, 25, 0));
                this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 25, 1));
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 1));
            } else if (sheVampireOnMyTransfusionTilIIssues >= 1f) {
                this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 25, 0));
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 0));
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void somatogenesis$saveBloodData(CompoundTag nbt, CallbackInfo ci) {
        if (this.blood.isEmpty()) return;
        CompoundTag blood = new CompoundTag();
        for (Map.Entry<EntityType<?>, Integer> entry : this.blood.entrySet()) {
            blood.putInt(SomatogenesisEntities.ENTITY_TYPES.getRegistrar().getId(entry.getKey()).toString(), entry.getValue());
        }
        nbt.put("transfusions", blood);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void somatogenesis$readBloodData(CompoundTag nbt, CallbackInfo ci) {
        if (!nbt.contains("transfusions")) return;
        CompoundTag blood = nbt.getCompound("transfusions");
        for (String key : blood.getAllKeys()) {
            EntityType<?> type = SomatogenesisEntities.ENTITY_TYPES.getRegistrar().get(ResourceLocation.tryParse(key));
            this.blood.put(type, blood.getInt(key));
        }
    }
}
