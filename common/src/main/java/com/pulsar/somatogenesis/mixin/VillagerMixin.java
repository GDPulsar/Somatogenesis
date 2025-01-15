package com.pulsar.somatogenesis.mixin;

import com.pulsar.somatogenesis.registry.SomatogenesisAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {
    public VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "updateSpecialPrices", at = @At("HEAD"))
    private void somatogenesis$tradeCostAttribute(Player player, CallbackInfo ci) {
        double value = player.getAttributeValue(SomatogenesisAttributes.TRADE_COST.get());
        if (value != 1) {
            for (MerchantOffer offer : this.getOffers()) {
                int diff = (int)(offer.getBaseCostA().getCount() * (value-1));
                offer.addToSpecialPriceDiff(diff);
            }
        }
    }
}
