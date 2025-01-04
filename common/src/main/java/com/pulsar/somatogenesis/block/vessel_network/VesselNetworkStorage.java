package com.pulsar.somatogenesis.block.vessel_network;

public interface VesselNetworkStorage {
    /**
     * Stores blood in this entity.
     * @param amount The amount to store.
     * @return The amount of blood remaining.
     */
    int addBlood(int amount);
    /**
     * Takes blood from this entity.
     * @param amount The amount to take.
     * @return The amount taken.
     */
    int takeBlood(int amount);
    /**
     * Get the amount of blood in this entity.
     * @return The blood stored.
     */
    int getBlood();
    /**
     * Get the maximum amount of blood this entity can store.
     * @return The maximum blood storage.
     */
    int getMaxBlood();
}
