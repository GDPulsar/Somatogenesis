package com.pulsar.somatogenesis.block.vessel_network;

/**
 * <p>An interface enabling a block entity to act as a requester for the Vessel Network.</p>
 * <p>
 *     Requesters are allowed to request taking blood out of the network, and are unable to put blood into the network.
 *     Any and all blood added to this block entity will be stored, and not shared.
 * </p>
 */
public interface VesselNetworkRequester {
    /**
     * Try to take an amount of blood out of the connected network.
     * @param amount The amount to take.
     * @return The amount taken.
     */
    int requestBlood(int amount);
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
