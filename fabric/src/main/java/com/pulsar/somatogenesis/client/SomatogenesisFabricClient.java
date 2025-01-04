package com.pulsar.somatogenesis.client;

import com.pulsar.somatogenesis.Somatogenesis;
import net.fabricmc.api.ClientModInitializer;

public final class SomatogenesisFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Somatogenesis.initClient();
    }
}
