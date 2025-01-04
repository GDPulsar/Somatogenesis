package com.pulsar.somatogenesis.accessor;

public interface GlobalBoostsAccessor {
    void somatogenesis$activateFarmingBoost();
    void somatogenesis$activateMiningBoost();
    void somatogenesis$activateCraftingBoost();

    void somatogenesis$tickBoosts();

    boolean somatogenesis$hasFarmingBoost();
    boolean somatogenesis$hasMiningBoost();
    boolean somatogenesis$hasCraftingBoost();
}
