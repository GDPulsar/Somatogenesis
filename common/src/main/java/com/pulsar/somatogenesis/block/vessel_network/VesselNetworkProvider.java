package com.pulsar.somatogenesis.block.vessel_network;

/**
 * <p>An interface enabling a block entity to act as a provider for the Vessel Network.</p>
 * <p>
 *     Providers are allowed to provide blood into the network, and are unable to take blood from the network.
 *     Any and all blood added to this block entity will attempt to be added to the network first, and then stored otherwise.
 * </p>
 */
public interface VesselNetworkProvider {
    /**
     * Take an amount of blood from this entity.
     * @param amount The amount to take.
     * @return The amount taken.
     */
    int takeBlood(int amount);
    /**
     * Stores blood in this entity.
     * @param amount The amount to store.
     * @return The amount of blood remaining.
     */
    int addBlood(int amount);
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
