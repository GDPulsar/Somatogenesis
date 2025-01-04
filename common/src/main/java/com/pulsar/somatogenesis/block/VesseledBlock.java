package com.pulsar.somatogenesis.block;

import com.pulsar.somatogenesis.block.vessel_network.VesselNetworkConnector;
import net.minecraft.world.level.block.Block;

public class VesseledBlock extends Block implements VesselNetworkConnector {
    public VesseledBlock(Properties properties) {
        super(properties);
    }
}
