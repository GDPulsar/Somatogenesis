package com.pulsar.somatogenesis.block;

import com.pulsar.somatogenesis.block.vessel_network.VesselNetworkProviderEntity;
import com.pulsar.somatogenesis.item.BloodContainer;
import com.pulsar.somatogenesis.recipe.DigesterRecipe;
import com.pulsar.somatogenesis.recipe.DrainerRecipe;
import com.pulsar.somatogenesis.recipe.IngredientStack;
import com.pulsar.somatogenesis.registry.SomatogenesisBlocks;
import com.pulsar.somatogenesis.registry.SomatogenesisRecipes;
import com.pulsar.somatogenesis.util.ImplementedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DrainerBlockEntity extends VesselNetworkProviderEntity implements ImplementedInventory {
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private Player owner;
    private int drained = 0;
    private int drainTick = 0;

    public DrainerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SomatogenesisBlocks.DRAINER_ENTITY.get(), blockPos, blockState, 100);
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

    public void serverTick() {
        assert this.level != null;
        if (!this.getItems().isEmpty()) {
            Optional<? extends DrainerRecipe> recipe = this.level.getRecipeManager().getRecipeFor(SomatogenesisRecipes.DRAINER_TYPE.get(), this, level);
            if (recipe.isPresent()) {
                drainTick++;
                int newDrained = Math.round(recipe.get().getBlood() * ((float)drainTick / 300f));
                if (drained != newDrained) {
                    this.blood += 1;
                }
                if (drainTick >= 300) {
                    this.getItem(0).shrink(1);
                }
            }
        }
    }

    public void clientTick() {

    }

    @Override
    public void load(CompoundTag compoundTag) {
        drained = compoundTag.getInt("drained");
        drainTick = compoundTag.getInt("drainTick");
        ContainerHelper.loadAllItems(compoundTag, this.items);
        super.load(compoundTag);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        compoundTag.putInt("drained", drained);
        compoundTag.putInt("drainTick", drainTick);
        ContainerHelper.saveAllItems(compoundTag, this.items);
        super.saveAdditional(compoundTag);
    }
}
