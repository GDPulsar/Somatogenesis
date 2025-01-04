package com.pulsar.somatogenesis.progression;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.registry.SomatogenesisNetworking;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ProgressionData {
    private final HashMap<ResourceLocation, ProgressionUnlock> unlocks = new HashMap<>();
    private final Player player;

    public ProgressionData(Player player) {
        this.player = player;
        for (Supplier<ProgressionUnlock> unlockSupplier : ProgressionUnlocks.ALL_UNLOCKS) {
            ProgressionUnlock unlock = unlockSupplier.get();
            unlocks.put(unlock.id(), unlock);
        }
    }

    public void clear() {
        for (ProgressionUnlock unlock : getUnlocks()) {
            unlock.clear();
        }
    }

    public List<ProgressionUnlock> getUnlocks() {
        if (this.player instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToPlayer(serverPlayer, SomatogenesisNetworking.UPDATE_PROGRESSION, new FriendlyByteBuf(Unpooled.buffer()).writeNbt(this.writeNbt()));
        }
        return this.unlocks.values().stream().toList();
    }

    public ProgressionUnlock getUnlock(ResourceLocation id) {
        return this.unlocks.get(id);
    }

    public boolean unlocked(ProgressionUnlock unlock) {
        if (unlocks.containsKey(unlock.id())) {
            return unlocks.get(unlock.id()).completed(player);
        }
        return false;
    }

    public boolean unlocked(ResourceLocation unlockId) {
        if (unlocks.containsKey(unlockId)) {
            return unlocks.get(unlockId).completed(player);
        }
        return false;
    }

    public CompoundTag writeNbt() {
        CompoundTag nbt = new CompoundTag();
        for (Map.Entry<ResourceLocation, ProgressionUnlock> unlock : unlocks.entrySet()) {
            nbt.put(unlock.getKey().toString(), unlock.getValue().writeNbt());
        }
        return nbt;
    }

    public void readNbt(CompoundTag nbt) {
        for (String key : nbt.getAllKeys()) {
            ResourceLocation id = ResourceLocation.tryParse(key);
            if (unlocks.containsKey(id)) unlocks.get(id).readNbt(nbt.getCompound(key));
        }
    }
}
