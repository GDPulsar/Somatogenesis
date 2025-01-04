package com.pulsar.somatogenesis.util;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.accessor.ProgressionAccessor;
import com.pulsar.somatogenesis.progression.ProgressionData;
import com.pulsar.somatogenesis.registry.SomatogenesisAttributes;
import com.pulsar.somatogenesis.registry.SomatogenesisEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BloodUtils {
    public static float getBloodGainMultiplier(Player player) {
        return (float)(getBaseBloodGain(player) * player.getAttributeValue(SomatogenesisAttributes.BLOODLETTING.get()));
    }

    public static float getBloodlettingMultiplier(LivingEntity living) {
        float base = 1f;
        if (living.hasEffect(SomatogenesisEffects.HEMORRHAGED.get())) base *= 2f;
        return base;
    }

    public static float getBaseBloodGain(Player player) {
        ProgressionData progression = ((ProgressionAccessor)player).somatogenesis$getProgression();
        if (progression.unlocked(Somatogenesis.reloc("bloodletting_5"))) return 5f;
        if (progression.unlocked(Somatogenesis.reloc("bloodletting_4"))) return 2.5f;
        if (progression.unlocked(Somatogenesis.reloc("bloodletting_3"))) return 1.5f;
        if (progression.unlocked(Somatogenesis.reloc("bloodletting_2"))) return 1f;
        if (progression.unlocked(Somatogenesis.reloc("bloodletting_1"))) return 0.5f;
        return 0.25f;
    }
}
