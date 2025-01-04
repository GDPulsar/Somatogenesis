package com.pulsar.somatogenesis.block.vessel_network;

import com.pulsar.somatogenesis.Somatogenesis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class VesselNetwork {
    private final HashMap<BlockPos, VesselNetworkRequester> requesters = new HashMap<>();
    private final HashMap<BlockPos, VesselNetworkStorage> storages = new HashMap<>();
    private final HashMap<BlockPos, VesselNetworkProvider> providers = new HashMap<>();

    private final BlockPos pos;

    public VesselNetwork(BlockPos pos) {
        this.pos = pos;
    }

    /**
     * Requests an amount of blood from the network.
     * @param amount The amount to request.
     * @return The amount received.
     */
    public int requestBlood(int amount) {
        int received = 0;
        if (getStorages().isEmpty()) return 0;
        while (received < amount) {
            List<VesselNetworkStorage> sorting = new ArrayList<>(getStorages().stream().toList());
            sorting.sort((a, b) -> {
                int sign = Mth.sign(((float)b.getBlood() / b.getMaxBlood()) - ((float)a.getBlood() / a.getMaxBlood()));
                if (sign == 0) return Mth.sign(b.getMaxBlood() - a.getMaxBlood());
                else return sign;
            });
            VesselNetworkStorage first = sorting.get(0);
            VesselNetworkStorage second = sorting.get(1);
            if (first == null) break;
            if (second == null) {
                int taken = first.takeBlood(amount - received);
                if (taken == 0) break;
                received += taken;
            } else {
                float currentPercent = (float)first.getBlood() / first.getMaxBlood();
                float firstDelta = 1f / first.getMaxBlood();
                float targetPercent = (float)second.getBlood() / second.getMaxBlood();
                int reduceAmount = Mth.ceil(Mth.abs(targetPercent - currentPercent) / firstDelta);
                if (reduceAmount == 0) reduceAmount = 1;
                int taken = first.takeBlood(reduceAmount);
                if (taken == 0) break;
                received += taken;
            }
        }
        return received;
    }

    /**
     * Empties an amount of blood into the network.
     * @param amount The amount to empty.
     * @return The amount remaining.
     */
    public int emptyBlood(int amount) {
        int emptied = 0;
        while (emptied < amount) {
            List<VesselNetworkStorage> sorting = new ArrayList<>(getStorages().stream().toList());
            sorting.sort((a, b) -> {
                int sign = Mth.sign(((float)a.getBlood() / a.getMaxBlood()) - ((float)b.getBlood() / b.getMaxBlood()));
                if (sign == 0) return Mth.sign(a.getMaxBlood() - b.getMaxBlood());
                else return sign;
            });
            VesselNetworkStorage first = sorting.get(0);
            VesselNetworkStorage second = sorting.get(1);
            if (first == null) break;
            if (second == null) {
                int added = first.addBlood(amount - emptied);
                if (added != 0) break;
                emptied += added;
            } else {
                float currentPercent = (float)first.getBlood() / first.getMaxBlood();
                float firstDelta = 1f / first.getMaxBlood();
                float targetPercent = (float)second.getBlood() / second.getMaxBlood();
                int increaseAmount = Mth.ceil((targetPercent - currentPercent) / firstDelta);
                if (increaseAmount == 0) increaseAmount = 1;
                int added = first.addBlood(increaseAmount);
                if (added != 0) break;
                emptied += added;
            }
        }
        return emptied;
    }

    private VesselNetworkStorage getSecondFullestStorage() {
        if (getStorages().size() < 2) return null;
        List<VesselNetworkStorage> sorting = new ArrayList<>(getStorages().stream().toList());
        sorting.sort((a, b) -> {
            int sign = Mth.sign(((float)b.getBlood() / b.getMaxBlood()) - ((float)a.getBlood() / a.getMaxBlood()));
            if (sign == 0) return Mth.sign(b.getMaxBlood() - a.getMaxBlood());
            else return sign;
        });
        return sorting.get(1);
    }

    public Collection<BlockPos> getRequesterPositions() {
        return requesters.keySet();
    }

    public Collection<VesselNetworkRequester> getRequesters() {
        return requesters.values();
    }

    public Collection<BlockPos> getStoragePositions() {
        return storages.keySet();
    }

    public Collection<VesselNetworkStorage> getStorages() {
        return storages.values();
    }

    public Collection<BlockPos> getProviderPositions() {
        return providers.keySet();
    }

    public Collection<VesselNetworkProvider> getProviders() {
        return providers.values();
    }

    public void updateNetwork(Level level) {
        this.updateConnected(level, pos, Set.of(), 0);
    }

    public static @Nullable VesselNetwork tryCreateNetwork(Level level, BlockPos pos) {
        if (level.isClientSide) return null;
        Optional<VesselNetwork> found = findNetwork(level, pos, Set.of(), 0);
        return found.orElseGet(() -> new VesselNetwork(pos));
    }

    private static final int MAX_SEARCH_DEPTH = 32;
    private static Optional<VesselNetwork> findNetwork(Level level, BlockPos pos, Set<BlockPos> searched, int depth) {
        if (level.isClientSide) return Optional.empty();
        if (searched.contains(pos)) return Optional.empty();
        if (level.isLoaded(pos)) {
            if (!(level.getBlockState(pos).getBlock() instanceof VesselNetworkConnector)
                    && !(level.getBlockEntity(pos) instanceof VesselNetworkConnected)) return Optional.empty();
        } else {
            return Optional.empty();
        }
        if (depth >= MAX_SEARCH_DEPTH) return Optional.empty();
        searched.add(pos);
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity != null) {
            if (entity instanceof VesselNetworkConnected connected) {
                return Optional.ofNullable(connected.getConnectedNetwork());
            }
        }
        for (Direction direction : Direction.values()) {
            if (!searched.contains(pos.relative(direction))) {
                if (level.getBlockState(pos.relative(direction)).getBlock() instanceof VesselNetworkConnector || level.getBlockEntity(pos) instanceof VesselNetworkConnected) {
                    Optional<VesselNetwork> network = findNetwork(level, pos.relative(direction), searched, depth + 1);
                    Somatogenesis.LOGGER.info("search complete for block pos: {}. present: {}", pos.relative(direction), network.isPresent());
                    if (network.isPresent()) return network;
                }
            }
        }
        return Optional.empty();
    }

    private void updateConnected(Level level, BlockPos pos, Set<BlockPos> searched, int depth) {
        if (level.isClientSide) return;
        if (searched.contains(pos)) return;
        if (!(level.getBlockState(pos).getBlock() instanceof VesselNetworkConnector)) return;
        if (depth >= MAX_SEARCH_DEPTH) return;
        searched.add(pos);
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity != null) {
            if (entity instanceof VesselNetworkProvider provider) {
                providers.put(pos, provider);
            }
            if (entity instanceof VesselNetworkStorage storage) {
                storages.put(pos, storage);
            }
            if (entity instanceof VesselNetworkRequester requester) {
                requesters.put(pos, requester);
            }
        }
        for (Direction direction : Direction.values()) {
            if (!searched.contains(pos.relative(direction))) {
                if (level.getBlockState(pos.relative(direction)).getBlock() instanceof VesselNetworkConnector) {
                    updateConnected(level, pos.relative(direction), searched, depth + 1);
                }
            }
        }
    }
}
