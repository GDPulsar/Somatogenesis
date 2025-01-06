package com.pulsar.somatogenesis.block;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.block.vessel_network.VesselNetworkStorageEntity;
import com.pulsar.somatogenesis.item.BloodContainer;
import com.pulsar.somatogenesis.recipe.BloodAltarRecipe;
import com.pulsar.somatogenesis.recipe.IngredientStack;
import com.pulsar.somatogenesis.registry.SomatogenesisBlocks;
import com.pulsar.somatogenesis.registry.SomatogenesisRecipes;
import com.pulsar.somatogenesis.util.ImplementedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class BloodAltarBlockEntity extends VesselNetworkStorageEntity implements ImplementedInventory {
    private final NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);
    private Player owner;

    private BloodAltarRecipe currentRecipe = null;
    private int craftTick = 0;

    public BloodAltarBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SomatogenesisBlocks.BLOOD_ALTAR_ENTITY.get(), blockPos, blockState, 5000);
    }

    @Override
    public int addBlood(int amount) {
        int val = super.addBlood(amount);
        sync();
        return val;
    }

    @Override
    public int takeBlood(int amount) {
        int val = super.takeBlood(amount);
        sync();
        return val;
    }

    public void transferFrom(ItemStack stack) {
        if (stack.getItem() instanceof BloodContainer container) {
            int leftover = container.getBlood(stack) - (this.getMaxBlood() - this.getBlood());
            addBlood(container.getBlood(stack));
            container.setBlood(stack, leftover);
        }
    }

    public Player getOwner() {
        return this.owner;
    }

    public void setOwner(Player player) {
        this.owner = player;
    }

    @Override
    public int getMaxBlood() {
        BloodAltarBlock.Tier tier = this.getBlockState().getValue(BloodAltarBlock.TIER);
        return switch (tier) {
            case IRON -> 5000;
            case DIAMOND -> 15000;
            case FLESH -> 25000;
            case BONE -> 40000;
            case LIVING -> 50000;
        };
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public @NotNull ItemStack addItem(ItemStack stack) {
        ItemStack current = stack.copy();
        for (int i = 0; i < 8; i++) {
            if (this.getItem(i).isEmpty()) {
                this.setItem(i, current);
                sync();
                return ItemStack.EMPTY;
            } else {
                if (this.getItem(i).isStackable() && this.getItem(i).is(current.getItem())) {
                    int toStack = Math.min(this.getItem(i).getMaxStackSize() - this.getItem(i).getCount(), current.getCount());
                    this.getItem(i).grow(toStack);
                    current.shrink(toStack);
                    if (current.getCount() == 0) {
                        sync();
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
        return current;
    }

    public ItemStack takeItem() {
        for (int i = 7; i >= 0; i--) {
            if (!this.getItem(i).isEmpty()) {
                sync();
                return this.removeItem(i, this.items.get(i).getCount());
            }
        }
        sync();
        return ItemStack.EMPTY;
    }

    public void sync() {
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 0);
    }

    public void onBreak() {
        NonNullList<ItemStack> toDrop = NonNullList.create();
        toDrop.addAll(this.items);
        Containers.dropContents(Objects.requireNonNull(this.getLevel()), this.getBlockPos(), toDrop);
        this.clearContent();
    }

    public void serverTick() {
        assert this.level != null;
        if (this.currentRecipe == null && !this.items.isEmpty()) {
            Optional<BloodAltarRecipe> recipe = this.level.getRecipeManager().getRecipeFor(SomatogenesisRecipes.BLOOD_ALTAR_TYPE.get(), this, this.level);
            recipe.ifPresent(o -> {
                Somatogenesis.LOGGER.info("found valid recipe: {}", o.getId());
                this.currentRecipe = o;
            });
        }
        if (this.currentRecipe != null) {
            if (!this.currentRecipe.matches(this, this.level)) {
                this.currentRecipe = null;
                return;
            }
            craftTick++;
            if (craftTick >= 200) {
                Vec3 spawnPos = this.getBlockPos().above().getCenter();
                ItemEntity result = new ItemEntity(this.level, spawnPos.x, spawnPos.y, spawnPos.z, this.currentRecipe.getResult());
                this.level.addFreshEntity(result);
                craftTick = 0;
                craft();

                this.currentRecipe = null;
                this.setChanged();
            }
            if (this.level instanceof ServerLevel serverLevel) {
                int particleRate = 20;
                if (this.craftTick >= 75) particleRate = 10;
                if (this.craftTick >= 150) particleRate = 6;
                if (this.craftTick >= 175) particleRate = 4;
                if (this.craftTick % particleRate == 0) {
                    for (int i = 0; i < 12; i++) {
                        float angle = (Mth.TWO_PI / 12 * i) + craftTick / 100f;
                        Vec3 pos = this.getBlockPos().above().getCenter().add(new Vec3(Mth.sin(angle), 0f, Mth.cos(angle)));
                        serverLevel.sendParticles(new DustParticleOptions(DustParticleOptions.REDSTONE_PARTICLE_COLOR, 0.75f), pos.x, pos.y, pos.z, 1, 0.1f, 0.1f, 0.1f, 0f);
                    }
                }
            }
        } else {
            this.craftTick = 0;
        }
    }

    public void clientTick() {

    }

    public void craft() {
        if (this.currentRecipe == null) return;
        for (IngredientStack ingredient : this.currentRecipe.getIngredientList()) {
            int toReduce = ingredient.count();
            for (int i = 0; i < this.getContainerSize(); i++) {
                if (ingredient.ingredient().test(this.getItem(i))) {
                    int reduction = Math.min(toReduce, this.getItem(i).getCount());
                    this.getItem(i).shrink(reduction);
                    toReduce -= reduction;
                    if (toReduce == 0) break;
                }
            }
        }
        this.takeBlood(this.currentRecipe.getBlood());
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 0);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("craftTick", craftTick);
        ContainerHelper.saveAllItems(tag, this.items, true);
        return tag;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        craftTick = compoundTag.getInt("craftTick");
        this.items.clear();
        ContainerHelper.loadAllItems(compoundTag, this.items);
        super.load(compoundTag);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        compoundTag.putInt("craftTick", craftTick);
        ContainerHelper.saveAllItems(compoundTag, this.items);
        super.saveAdditional(compoundTag);
    }
}
