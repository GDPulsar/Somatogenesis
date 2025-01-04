package com.pulsar.somatogenesis.mixin.level;

import com.pulsar.somatogenesis.accessor.BloodMoonAccessor;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Level.class)
public class LevelBloodMoonMixin implements BloodMoonAccessor {
    @Unique private boolean bloodMoon = false;

    @Override
    public void somatogenesis$setBloodMoon(boolean active) {
        this.bloodMoon = active;
    }

    @Override
    public boolean somatogenesis$isBloodMoon() {
        return this.bloodMoon;
    }
}
