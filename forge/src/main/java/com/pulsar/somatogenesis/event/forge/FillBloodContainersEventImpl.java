package com.pulsar.somatogenesis.event.forge;

import com.pulsar.somatogenesis.item.BloodContainer;
import com.pulsar.somatogenesis.util.BloodUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class FillBloodContainersEventImpl {
    public static void fill(Player player, float damage) {
        if (CuriosApi.getCuriosInventory(player).resolve().isPresent()) {
            ICuriosItemHandler curios = CuriosApi.getCuriosInventory(player).resolve().get();
            if (curios.isEquipped((stack) -> stack.getItem() instanceof BloodContainer)) {
                ItemStack containerStack = curios.findCurios((stack) -> stack.getItem() instanceof BloodContainer).get(0).stack();
                if (containerStack.getItem() instanceof BloodContainer container) {
                    container.addBlood(containerStack, (int) (damage * BloodUtils.getBloodGainMultiplier(player)));
                }
            }
        }
    }
}
