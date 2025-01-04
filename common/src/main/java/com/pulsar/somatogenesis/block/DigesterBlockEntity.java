package com.pulsar.somatogenesis.block;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.item.BloodContainer;
import com.pulsar.somatogenesis.recipe.DigesterRecipe;
import com.pulsar.somatogenesis.recipe.IngredientStack;
import com.pulsar.somatogenesis.registry.SomatogenesisRecipes;
import com.pulsar.somatogenesis.util.ImplementedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class DigesterBlockEntity extends BlockEntity implements ImplementedInventory {
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private Player owner;

    private DigesterRecipe currentRecipe = null;
    private int blood = 0;
    private int craftTick = 0;

    public DigesterBlockEntity(BlockEntityType<? extends DigesterBlockEntity> type, BlockPos blockPos, BlockState blockState) {
        super(type, blockPos, blockState);
    }

    public void transferFrom(ItemStack stack) {
        if (stack.getItem() instanceof BloodContainer container) {
            int leftover = container.getBlood(stack) - (this.getMaxBlood() - this.getBlood());
            addBlood(container.getBlood(stack));
            container.setBlood(stack, leftover);
        }
    }

    public int getMaxBlood() {
        return 0;
    }

    public void addBlood(int blood) {
        this.setBlood(this.getBlood() + blood);
    }

    public int getBlood() {
        return this.blood;
    }

    public void setBlood(int blood) {
        this.blood = Mth.clamp(blood, 0, getMaxBlood());
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 0);
    }

    public Player getOwner() {
        return this.owner;
    }

    public void setOwner(Player player) {
        this.owner = player;
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public @NotNull ItemStack addItem(ItemStack stack) {
        ItemStack current = stack.copy();
        for (int i = 0; i < this.getContainerSize(); i++) {
            if (this.getItem(i).isEmpty()) {
                this.setItem(i, current);
                return ItemStack.EMPTY;
            } else {
                if (this.getItem(i).isStackable() && this.getItem(i).is(current.getItem())) {
                    int toStack = Math.min(this.getItem(i).getMaxStackSize() - this.getItem(i).getCount(), current.getCount());
                    this.getItem(i).grow(toStack);
                    current.shrink(toStack);
                    if (current.getCount() == 0) return ItemStack.EMPTY;
                }
            }
        }
        return current;
    }

    public ItemStack takeItem() {
        for (int i = this.getContainerSize() - 1; i >= 0; i--) {
            if (!this.getItem(i).isEmpty()) {
                return this.removeItem(i, this.items.get(i).getCount());
            }
        }
        return ItemStack.EMPTY;
    }

    public void onBreak() {
        NonNullList<ItemStack> toDrop = NonNullList.create();
        toDrop.addAll(this.items);
        Containers.dropContents(Objects.requireNonNull(this.getLevel()), this.getBlockPos(), toDrop);
        this.clearContent();
    }

    public abstract RecipeType<? extends DigesterRecipe> getRecipeType();

    public void serverTick() {
        assert this.level != null;
        if (this.currentRecipe == null && !this.items.isEmpty()) {
            Optional<? extends DigesterRecipe> recipe = this.level.getRecipeManager().getRecipeFor(getRecipeType(), this, level);
            recipe.ifPresent(r -> this.currentRecipe = r);
        }
        if (this.currentRecipe != null) {
            if (!this.currentRecipe.matches(this, this.level)) {
                this.currentRecipe = null;
                return;
            }
            craftTick++;
            if (craftTick >= 200) {
                Vec3i normal = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getNormal();
                Vec3 direction = new Vec3(normal.getX(), 0.5f, normal.getZ()).scale(0.2f);
                Vec3 spawnPos = this.getBlockPos().getCenter().relative(this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING), 0.5);
                if (this.level instanceof ServerLevel serverLevel) {
                    LootParams.Builder builder = new LootParams.Builder(serverLevel);
                    ResourceLocation resourceLocation = this.currentRecipe.getLootTable();
                    LootParams lootParams = builder.create(LootContextParamSets.EMPTY);
                    LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(resourceLocation);
                    List<ItemStack> items = lootTable.getRandomItems(lootParams);
                    for (ItemStack item : items) {
                        ItemEntity result = new ItemEntity(this.level, spawnPos.x, spawnPos.y, spawnPos.z, item);
                        result.setDeltaMovement(direction.x + (Math.random() - 0.5f) * 0.035f, direction.y + (Math.random() - 0.5f) * 0.035f, direction.z + (Math.random() - 0.5f) * 0.035f);
                        result.hasImpulse = true;
                        this.level.addFreshEntity(result);
                    }
                }
                craftTick = 0;
                craft();

                this.currentRecipe = null;
                this.setChanged();
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
        this.setBlood(this.getBlood() - this.currentRecipe.getBlood());
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 0);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        blood = compoundTag.getInt("blood");
        craftTick = compoundTag.getInt("craftTick");
        ContainerHelper.loadAllItems(compoundTag, this.items);
        super.load(compoundTag);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        compoundTag.putInt("blood", blood);
        compoundTag.putInt("craftTick", craftTick);
        ContainerHelper.saveAllItems(compoundTag, this.items);
        super.saveAdditional(compoundTag);
    }
}
