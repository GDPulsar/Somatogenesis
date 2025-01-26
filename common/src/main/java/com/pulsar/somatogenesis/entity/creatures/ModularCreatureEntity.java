package com.pulsar.somatogenesis.entity.creatures;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.entity.creatures.modules.*;
import com.pulsar.somatogenesis.entity.creatures.modules.mobs.CreeperAppendixModule;
import com.pulsar.somatogenesis.registry.SomatogenesisEntities;
import com.pulsar.somatogenesis.teaching.TeachingStats;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.function.BiConsumer;

public abstract class ModularCreatureEntity extends PathfinderMob implements VibrationSystem {
    private final HashMap<ResourceLocation, CreatureModule> modules = new HashMap<>();
    public final TeachingStats teachingStats = new TeachingStats();

    public ModularCreatureEntity(EntityType<ModularCreatureEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ModularCreatureEntity(Level level) {
        super(SomatogenesisEntities.MODULAR_CREATURE.get(), level);
    }

    public abstract ModuleStats getStats();

    LookAtPlayerGoal lookGoal = new LookAtPlayerGoal(this, Player.class, 8.0F);

    private void updateGoals() {
        this.goalSelector.removeGoal(lookGoal);
        if (this.hasModule(Somatogenesis.reloc("basic_sight")) || this.hasModule(Somatogenesis.reloc("ender_sight"))) {
            this.goalSelector.addGoal(10, lookGoal);
        }
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.updateGoals();
        this.addBehaviourGoals();
    }

    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1f, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, LivingEntity.class, false, entity -> {
            Somatogenesis.LOGGER.info("entity: {}, basic sight: {}, ender sight: {}, can attack: {}, line of sight: {}, distance: {}, follow range: {}",
                    entity, this.hasModule(Somatogenesis.reloc("basic_sight")), this.hasModule(Somatogenesis.reloc("ender_sight")),
                    this.canTargetEntity(entity), this.getSensing().hasLineOfSight(entity), entity.distanceTo(this), this.getAttribute(Attributes.FOLLOW_RANGE).getValue());
            if (this.hasModule(Somatogenesis.reloc("basic_sight"))) {
                return this.canTargetEntity(entity) && this.getSensing().hasLineOfSight(entity);
            } else if (this.hasModule(Somatogenesis.reloc("ender_sight"))) {
                return this.canTargetEntity(entity) && entity.distanceTo(this) <= this.getAttribute(Attributes.FOLLOW_RANGE).getValue() / 2f;
            }
            return true;
        }));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createLivingAttributes().add(Attributes.FOLLOW_RANGE).add(Attributes.ATTACK_DAMAGE).add(Attributes.ATTACK_KNOCKBACK).add(Attributes.MOVEMENT_SPEED, 0.1);
    }

    public int getLimbCount() {
        int limbs = 0;
        for (CreatureModule module : getModules()) {
            if (module.getType() == ModuleType.LIMB) limbs++;
        }
        return limbs;
    }

    public int getOrganCount() {
        int organs = 0;
        for (CreatureModule module : getModules()) {
            if (module.getType() == ModuleType.ORGAN) organs++;
        }
        return organs;
    }

    public int getSensorCount() {
        int sensors = 0;
        for (CreatureModule module : getModules()) {
            if (module.getType() == ModuleType.SENSOR) sensors++;
        }
        return sensors;
    }

    public int getAccessoryCount() {
        int accessories = 0;
        for (CreatureModule module : getModules()) {
            if (module.getType() == ModuleType.ACCESSORY) accessories++;
        }
        return accessories;
    }

    public boolean addModule(CreatureModule module) {
        boolean canAdd = switch (module.getType()) {
            case LIMB -> getLimbCount() < getStats().maxLimbCount();
            case ORGAN -> getOrganCount() < getStats().maxOrganCount();
            case SENSOR -> getSensorCount() < getStats().maxSensorCount();
            case ACCESSORY -> getAccessoryCount() < getStats().maxAccessoryCount();
        };
        if (canAdd) modules.put(module.getId(), module);
        return canAdd;
    }

    public boolean hasModule(ResourceLocation id) {
        return modules.containsKey(id);
    }

    public CreatureModule getModule(ResourceLocation id) {
        return modules.get(id);
    }

    public Collection<ResourceLocation> getModuleIds() {
        return modules.keySet();
    }

    public Collection<CreatureModule> getModules() {
        return modules.values();
    }

    @Override
    public void tick() {
        if (this.level() instanceof ServerLevel serverLevel) {
            Ticker.tick(serverLevel, this.vibrationData, this.vibrationUser);
        }
        super.tick();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.put("TeachingStats", teachingStats.writeNbt(new CompoundTag()));
        CompoundTag modulesNbt = new CompoundTag();
        for (ResourceLocation id : modules.keySet()) {
            modulesNbt.put(id.toString(), modules.get(id).writeNbt(new CompoundTag()));
        }
        compoundTag.put("Modules", modulesNbt);
        super.addAdditionalSaveData(compoundTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        teachingStats.readNbt(compoundTag.getCompound("TeachingStats"));
        for (String key : compoundTag.getCompound("Modules").getAllKeys()) {
            ResourceLocation id = ResourceLocation.tryParse(key);
            CreatureModule module = getModuleFromId(id);
            module.readNbt(compoundTag.getCompound("Modules").getCompound(key));
            modules.put(id, module);
        }
        for (ResourceLocation id : Set.copyOf(modules.keySet())) {
            if (!compoundTag.getCompound("Modules").contains(id.toString())) {
                modules.remove(id);
            }
        }
        this.updateGoals();
        super.readAdditionalSaveData(compoundTag);
    }

    public static CreatureModule getModuleFromId(ResourceLocation id) {
        if (id.equals(Somatogenesis.reloc("sculk"))) return new SculkModule();
        return new BasicSightModule();
    }

    public boolean canTargetEntity(@Nullable Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            return this.level() == entity.level() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity) && !this.isAlliedTo(entity) && livingEntity.getType() != EntityType.ARMOR_STAND && !livingEntity.isInvulnerable() && !livingEntity.isDeadOrDying() && this.level().getWorldBorder().isWithinBounds(livingEntity.getBoundingBox());
        }

        return false;
    }

    @Override
    public void die(DamageSource damageSource) {
        if (hasModule(Somatogenesis.reloc("creeper_appendix"))) {
            if (this.getModule(Somatogenesis.reloc("creeper_appendix")) instanceof CreeperAppendixModule creeperAppendix) {
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), creeperAppendix.getStrength(), Level.ExplosionInteraction.MOB);
            }
        }
        super.die(damageSource);
    }

    //region Sculk Module
    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> biConsumer) {
        Level var3 = this.level();
        if (var3 instanceof ServerLevel serverLevel) {
            biConsumer.accept(this.dynamicGameEventListener, serverLevel);
        }
    }

    private final DynamicGameEventListener<Listener> dynamicGameEventListener = new DynamicGameEventListener<>(new Listener(this));
    private final User vibrationUser = new VibrationUser();
    private final Data vibrationData = new Data();
    @Override
    public @NotNull Data getVibrationData() {
        return vibrationData;
    }

    @Override
    public @NotNull User getVibrationUser() {
        return vibrationUser;
    }

    class VibrationUser implements User {
        private final PositionSource positionSource = new EntityPositionSource(ModularCreatureEntity.this, ModularCreatureEntity.this.getEyeHeight());

        VibrationUser() {
        }

        public int getListenerRadius() {
            return 16;
        }

        public @NotNull PositionSource getPositionSource() {
            return this.positionSource;
        }

        public @NotNull TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.WARDEN_CAN_LISTEN;
        }

        public boolean canTriggerAvoidVibration() {
            return true;
        }

        public boolean canReceiveVibration(ServerLevel serverLevel, BlockPos blockPos, GameEvent gameEvent, GameEvent.Context context) {
            if (ModularCreatureEntity.this.hasModule(Somatogenesis.reloc("sculk")) && !ModularCreatureEntity.this.isNoAi() && !ModularCreatureEntity.this.isDeadOrDying() && !ModularCreatureEntity.this.getBrain().hasMemoryValue(MemoryModuleType.VIBRATION_COOLDOWN) && serverLevel.getWorldBorder().isWithinBounds(blockPos)) {
                Entity target = context.sourceEntity();
                if (target instanceof LivingEntity livingEntity) {
                    return ModularCreatureEntity.this.canTargetEntity(livingEntity);
                }

                return true;
            } else {
                return false;
            }
        }

        public void onReceiveVibration(ServerLevel serverLevel, BlockPos blockPos, GameEvent gameEvent, @Nullable Entity entity, @Nullable Entity entity2, float f) {
            if (!ModularCreatureEntity.this.isDeadOrDying()) {
                ModularCreatureEntity.this.brain.setMemoryWithExpiry(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
                serverLevel.broadcastEntityEvent(ModularCreatureEntity.this, (byte)61);
                ModularCreatureEntity.this.playSound(SoundEvents.WARDEN_TENDRIL_CLICKS, 5.0F, ModularCreatureEntity.this.getVoicePitch());
            }
        }
    }
    //endregion
}
