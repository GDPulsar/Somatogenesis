package com.pulsar.somatogenesis.event.fabric;

import com.pulsar.somatogenesis.item.BloodContainer;
import com.pulsar.somatogenesis.util.BloodUtils;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FillBloodContainersEventImpl {
    public static void fill(Player player, float damage) {
        if (TrinketsApi.getTrinketComponent(player).isPresent()) {
            TrinketComponent trinkets = TrinketsApi.getTrinketComponent(player).get();
            if (trinkets.isEquipped((stack) -> stack.getItem() instanceof BloodContainer)) {
                ItemStack containerStack = trinkets.getEquipped((stack) -> stack.getItem() instanceof BloodContainer).get(0).getB();
                if (containerStack.getItem() instanceof BloodContainer container) {
                    container.addBlood(containerStack, (int) (damage * BloodUtils.getBloodGainMultiplier(player)));
                }
            }
        }
    }
}
