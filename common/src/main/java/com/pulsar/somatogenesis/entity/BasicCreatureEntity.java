package com.pulsar.somatogenesis.entity;

import com.pulsar.somatogenesis.registry.SomatogenesisEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BasicCreatureEntity extends Mob {
    public BasicCreatureEntity(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    public BasicCreatureEntity(Level level, Vec3 position) {
        super(SomatogenesisEntities.BASIC_CREATURE.get(), level);
        this.setPos(position);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createLivingAttributes().add(Attributes.FOLLOW_RANGE);
    }
}
