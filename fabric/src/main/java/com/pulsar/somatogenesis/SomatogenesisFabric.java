package com.pulsar.somatogenesis;

import com.pulsar.somatogenesis.registry.SomatogenesisFabricItems;
import net.fabricmc.api.ModInitializer;

public final class SomatogenesisFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Somatogenesis.init();

        SomatogenesisFabricItems.register();
    }
}
