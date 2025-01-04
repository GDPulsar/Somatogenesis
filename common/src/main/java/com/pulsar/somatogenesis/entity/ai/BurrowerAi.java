package com.pulsar.somatogenesis.entity.ai;

import com.google.common.collect.ImmutableList;
import com.pulsar.somatogenesis.entity.BurrowerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.animal.sniffer.SnifferAi;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.hoglin.HoglinAi;
import net.minecraft.world.entity.schedule.Activity;

import java.util.Optional;
import java.util.Set;

public class BurrowerAi {
    public BurrowerAi() {}

    protected static Brain<?> makeBrain(Brain<BurrowerEntity> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initFightActivity(brain);
        //initBurrowedActivity(brain);
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<BurrowerEntity> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(new LookAtTargetSink(45, 120), new MoveToTargetSink(80, 160)));
    }

    private static void initIdleActivity(Brain<BurrowerEntity> brain) {
        brain.addActivity(Activity.IDLE, 5, ImmutableList.of(StartAttacking.create(BurrowerAi::findNearestValidAttackTarget)));
    }

    private static void initFightActivity(Brain<BurrowerEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(0.7F), MeleeAttack.create(15), StopAttackingIfTargetInvalid.create()), MemoryModuleType.ATTACK_TARGET);
    }

    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(BurrowerEntity burrower) {
        return burrower.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
    }
}
