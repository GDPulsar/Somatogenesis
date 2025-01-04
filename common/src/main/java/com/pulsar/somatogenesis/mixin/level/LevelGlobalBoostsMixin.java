package com.pulsar.somatogenesis.mixin.level;

import com.pulsar.somatogenesis.accessor.GlobalBoostsAccessor;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Level.class)
public abstract class LevelGlobalBoostsMixin implements GlobalBoostsAccessor {
    @Unique int farmingBoost = 0;
    @Unique int miningBoost = 0;
    @Unique int craftingBoost = 0;

    @Override
    public void somatogenesis$activateFarmingBoost() {
        farmingBoost = 6000;
    }

    @Override
    public void somatogenesis$activateMiningBoost() {
        miningBoost = 6000;
    }

    @Override
    public void somatogenesis$activateCraftingBoost() {
        craftingBoost = 6000;
    }

    @Override
    public boolean somatogenesis$hasFarmingBoost() {
        return farmingBoost > 0;
    }

    @Override
    public boolean somatogenesis$hasMiningBoost() {
        return miningBoost > 0;
    }

    @Override
    public boolean somatogenesis$hasCraftingBoost() {
        return craftingBoost > 0;
    }

    @Override
    public void somatogenesis$tickBoosts() {
        if (farmingBoost > 0) farmingBoost--;
        if (miningBoost > 0) miningBoost--;
        if (craftingBoost > 0) craftingBoost--;
    }
}
