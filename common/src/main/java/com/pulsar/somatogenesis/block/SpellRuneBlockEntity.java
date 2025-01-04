package com.pulsar.somatogenesis.block;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.accessor.BloodMoonAccessor;
import com.pulsar.somatogenesis.accessor.GlobalBoostsAccessor;
import com.pulsar.somatogenesis.registry.SomatogenesisBlocks;
import com.pulsar.somatogenesis.registry.SomatogenesisEffects;
import com.pulsar.somatogenesis.rune.Runes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2i;

import java.util.HashSet;
import java.util.List;

public class SpellRuneBlockEntity extends BlockEntity {
    private Direction face = Direction.DOWN;
    private HashSet<Vector2i> drawn = new HashSet<>();
    private Runes.Rune highlight = null;
    private int highlightIndex = 0;
    private Runes.Rune rune = null;

    public SpellRuneBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SomatogenesisBlocks.SPELL_RUNE_ENTITY.get(), blockPos, blockState);
    }

    public void setFace(Direction direction) {
        this.face = direction;
    }

    public Direction getFace() {
        return this.face;
    }

    public HashSet<Vector2i> getDrawn() {
        return this.drawn;
    }

    public void setDrawn(List<Vector2i> drawn) {
        this.drawn = new HashSet<>(drawn);
    }

    public void changeHighlight(Player player) {
        if (highlight != null) highlightIndex = (highlightIndex + 1) % Runes.getAll().size();
        highlight = Runes.getAvailable(player).get(highlightIndex);
    }

    public Runes.Rune getHighlight() {
        return this.highlight;
    }

    public void setRune(Runes.Rune rune) {
        this.rune = rune;
    }

    int cooldownTimer = 0;
    public void tick(Level level, BlockState state, BlockPos pos) {
        if (rune != null && cooldownTimer <= 0) {
            ResourceLocation id = Runes.getId(rune);
            GlobalBoostsAccessor boostsAccessor = (GlobalBoostsAccessor)level;
            LivingEntity nearest = level.getNearestEntity(LivingEntity.class, TargetingConditions.DEFAULT, null,
                    pos.getCenter().x, pos.getCenter().y, pos.getCenter().z, AABB.ofSize(pos.getCenter(), 3, 3, 3));
            Somatogenesis.LOGGER.info("path: {}", id.getPath());
            switch (id.getPath()) {
                case "blood_moon":
                    if (level.isNight()) {
                        ((BloodMoonAccessor)level).somatogenesis$setBloodMoon(true);
                        level.destroyBlock(pos, false);
                        break;
                    }
                    cooldownTimer = 100;
                    break;
                case "farming_ritual":
                    if (!boostsAccessor.somatogenesis$hasFarmingBoost()) {
                        if (nearest instanceof Animal) {
                            boostsAccessor.somatogenesis$activateFarmingBoost();
                            nearest.kill();
                            level.destroyBlock(pos, false);
                            break;
                        }
                    }
                    cooldownTimer = 100;
                    break;
                case "mining_ritual":
                    if (!boostsAccessor.somatogenesis$hasMiningBoost()) {
                        if (nearest instanceof Mob) {
                            boostsAccessor.somatogenesis$activateMiningBoost();
                            nearest.kill();
                            level.destroyBlock(pos, false);
                            break;
                        }
                    }
                    cooldownTimer = 100;
                    break;
                case "crafting_ritual":
                    if (!boostsAccessor.somatogenesis$hasCraftingBoost()) {
                        if (nearest instanceof Animal) {
                            boostsAccessor.somatogenesis$activateCraftingBoost();
                            nearest.kill();
                            level.destroyBlock(pos, false);
                            break;
                        }
                    }
                    cooldownTimer = 100;
                    break;
                case "rain_ritual":
                    if (!level.isRaining() && level instanceof ServerLevel serverLevel) {
                        serverLevel.setWeatherParameters(0, 6000, true, false);
                        serverLevel.destroyBlock(pos, false);
                        break;
                    }
                    cooldownTimer = 100;
                    break;
                case "hemorrhage":
                    if (nearest != null) {
                        nearest.addEffect(new MobEffectInstance(SomatogenesisEffects.HEMORRHAGED.get(), 1200));
                        level.destroyBlock(pos, false);
                        break;
                    }
                    cooldownTimer = 50;
                    break;
                case "ignite":
                    if (cooldownTimer == 1) {
                        level.destroyBlock(pos, false);
                        level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                        break;
                    }
                    cooldownTimer = 50;
                    break;
                case "launch":
                    if (nearest != null) {
                        nearest.addDeltaMovement(new Vec3(0, 1.5, 0));
                        nearest.hasImpulse = true;
                        level.destroyBlock(pos, false);
                        break;
                    }
                    cooldownTimer = 50;
                    break;
            }
        }
        if (cooldownTimer > 0) cooldownTimer--;
    }

    public void draw(Vec3 targetPos) {
        Vec3 projected = targetPos.subtract(this.getBlockPos().getCenter()).with(this.face.getAxis(), 0);
        Vector2i pos;
        if (this.face.getAxis().isVertical()) pos = new Vector2i(Mth.floor(projected.x * 16f), Mth.floor(projected.z * 16f));
        else pos = new Vector2i(Mth.floor(projected.get(this.face.getCounterClockWise().getAxis()) * 16f), Mth.floor(projected.y * 16f));
        drawn.add(pos);
        this.setChanged();
    }

    public void draw(Vec3 targetPos, int radius) {
        Vec3 projected = targetPos.subtract(this.getBlockPos().getCenter()).with(this.face.getAxis(), 0);
        Vector2i pos;
        if (this.face.getAxis().isVertical())
            pos = new Vector2i(Mth.floor(projected.x * 16f), Mth.floor(projected.z * 16f));
        else
            pos = new Vector2i(Mth.floor(projected.get(this.face.getCounterClockWise().getAxis()) * 16f), Mth.floor(projected.y * 16f));
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                if (Mth.lengthSquared(x, y) <= radius * radius) {
                    Vector2i adding = new Vector2i(pos.x + x, pos.y + y);
                    drawn.add(adding);
                }
            }
        }
        this.setChanged();
    }

    @Override
    public void load(CompoundTag nbt) {
        face = Direction.byName(nbt.getString("face"));
        ListTag drawn = nbt.getList("drawn", ListTag.TAG_INT_ARRAY);
        for (int i = 0; i < drawn.size(); i++) {
            this.drawn.add(new Vector2i(drawn.getIntArray(i)[0], drawn.getIntArray(i)[1]));
        }
        super.load(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putString("face", this.face.toString());
        ListTag drawn = new ListTag();
        for (Vector2i d : this.drawn) {
            drawn.add(new IntArrayTag(new int[]{d.x, d.y}));
        }
        nbt.put("drawn", drawn);
        super.saveAdditional(nbt);
    }
}
