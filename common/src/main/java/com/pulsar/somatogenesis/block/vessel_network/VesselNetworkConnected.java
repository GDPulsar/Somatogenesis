package com.pulsar.somatogenesis.block.vessel_network;

import org.jetbrains.annotations.Nullable;

public interface VesselNetworkConnected {
    @Nullable VesselNetwork getConnectedNetwork();
    void updateConnectedNetwork();
}
