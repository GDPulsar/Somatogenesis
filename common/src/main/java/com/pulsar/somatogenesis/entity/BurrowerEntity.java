package com.pulsar.somatogenesis.entity;

import com.pulsar.somatogenesis.registry.SomatogenesisEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BurrowerEntity extends Mob {
    private static final EntityDataAccessor<State> DATA_STATE = SynchedEntityData.defineId(BurrowerEntity.class, EntityDataSerializer.simpleEnum(State.class));
    public final AnimationState runningAnimationState = new AnimationState();
    public final AnimationState run2idleAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState idle2runAnimationState = new AnimationState();
    public final AnimationState burrowingAnimationState = new AnimationState();
    public final AnimationState burrowedAnimationState = new AnimationState();
    public final AnimationState surfacingAnimationState = new AnimationState();

    public BurrowerEntity(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    public BurrowerEntity(Level level, Vec3 position) {
        super(SomatogenesisEntities.BURROWER.get(), level);
        this.setPos(position);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 20.0).add(Attributes.MOVEMENT_SPEED, 0.35).add(Attributes.ATTACK_DAMAGE, 3.0).add(Attributes.ARMOR, 2.0);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_STATE, State.IDLING);
    }

    private State getState() {
        return this.entityData.get(DATA_STATE);
    }

    private BurrowerEntity setState(State state) {
        this.entityData.set(DATA_STATE, state);
        return this;
    }

    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_STATE.equals(entityDataAccessor)) {
            State state = this.getState();
            this.resetAnimations();
            switch (state) {
                case IDLING -> this.idleAnimationState.startIfStopped(this.tickCount);
                case RUNNING -> this.runningAnimationState.startIfStopped(this.tickCount);
                case BURROWING -> this.burrowingAnimationState.startIfStopped(this.tickCount);
                case BURROWED -> this.burrowedAnimationState.startIfStopped(this.tickCount);
                case SURFACING -> this.surfacingAnimationState.startIfStopped(this.tickCount);
            }

            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(entityDataAccessor);
    }

    private void resetAnimations() {
        this.idleAnimationState.stop();
        this.idle2runAnimationState.stop();
        this.runningAnimationState.stop();
        this.run2idleAnimationState.stop();
        this.burrowingAnimationState.stop();
        this.burrowedAnimationState.stop();
        this.surfacingAnimationState.stop();
    }

    public BurrowerEntity transitionTo(State state) {
        switch (state) {
            case IDLING:
                this.setState(State.IDLING);
                break;
            case RUNNING:
                this.setState(State.RUNNING);
                break;
            case BURROWING:
                this.setState(State.BURROWING);
                break;
            case BURROWED:
                this.setState(State.BURROWED);
                break;
            case SURFACING:
                this.setState(State.SURFACING);
                break;
        }

        return this;
    }

    public void tick() {
        switch (this.getState()) {
            case BURROWING -> {
                this.emitDiggingParticles(this.burrowingAnimationState);
            }
        }

        super.tick();
    }

    private BurrowerEntity emitDiggingParticles(AnimationState animationState) {
        boolean bl = animationState.getAccumulatedTime() > 1700L && animationState.getAccumulatedTime() < 6000L;
        if (bl) {
            BlockPos blockPos = this.blockPosition();
            BlockState blockState = this.level().getBlockState(blockPos.below());
            if (blockState.getRenderShape() != RenderShape.INVISIBLE) {
                for(int i = 0; i < 30; ++i) {
                    Vec3 vec3 = Vec3.atCenterOf(blockPos).add(0.0, -0.6499999761581421, 0.0);
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
                }

                if (this.tickCount % 10 == 0) {
                    this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), blockState.getSoundType().getHitSound(), this.getSoundSource(), 0.5F, 0.5F, false);
                }
            }
        }

        return this;
    }

    public enum State {
        IDLING,
        RUNNING,
        BURROWING,
        BURROWED,
        SURFACING;
    }
}
