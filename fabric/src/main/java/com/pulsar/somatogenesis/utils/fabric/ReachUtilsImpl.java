package com.pulsar.somatogenesis.utils.fabric;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.world.entity.player.Player;

public class ReachUtilsImpl {
    public static double getBlockReach(Player player) {
        return ReachEntityAttributes.getReachDistance(player, 5f);
    }

    public static double getEntityReach(Player player) {
        return ReachEntityAttributes.getAttackRange(player, 3f);
    }
}
