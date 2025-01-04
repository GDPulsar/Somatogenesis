package com.pulsar.somatogenesis.block.vessel_network;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class VesselNetworkConnectedEntity extends BlockEntity implements VesselNetworkConnected {
    private VesselNetwork network = null;

    public VesselNetworkConnectedEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public @Nullable VesselNetwork getConnectedNetwork() {
        return network;
    }

    @Override
    public void updateConnectedNetwork() {
        if (this.level != null) this.network = VesselNetwork.tryCreateNetwork(this.level, this.getBlockPos());
    }
}
