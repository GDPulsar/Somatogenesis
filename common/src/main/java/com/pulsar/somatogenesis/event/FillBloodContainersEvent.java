package com.pulsar.somatogenesis.event;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.player.Player;

public class FillBloodContainersEvent {
    @ExpectPlatform
    public static void fill(Player player, float damage) {
        throw new AssertionError();
    }
}
