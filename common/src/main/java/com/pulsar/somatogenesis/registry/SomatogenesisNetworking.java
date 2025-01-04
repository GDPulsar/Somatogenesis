package com.pulsar.somatogenesis.registry;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.accessor.ProgressionAccessor;
import com.pulsar.somatogenesis.progression.ProgressionData;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class SomatogenesisNetworking {
    public static final ResourceLocation SUBMIT_UNLOCK = Somatogenesis.reloc("submit_unlock");
    public static final ResourceLocation UPDATE_PROGRESSION = Somatogenesis.reloc("update_progression");

    public static void registerCommon() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SUBMIT_UNLOCK, (buf, context) -> {
            Player player = context.getPlayer();
            ProgressionAccessor progressionAccessor = (ProgressionAccessor)player;
            ResourceLocation unlockId = buf.readResourceLocation();
            progressionAccessor.somatogenesis$getProgression().getUnlock(unlockId).submit(player);
        });
    }

    public static void registerClient() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, UPDATE_PROGRESSION, (buf, context) -> {
            ProgressionAccessor progressionAccessor = (ProgressionAccessor)Minecraft.getInstance().player;
            progressionAccessor.somatogenesis$getProgression().readNbt(Objects.requireNonNull(buf.readNbt()));
        });
    }
}
