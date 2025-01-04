package com.pulsar.somatogenesis.block;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.menu.EvolutionTankMenu;
import com.pulsar.somatogenesis.recipe.BloodAltarRecipe;
import com.pulsar.somatogenesis.recipe.EvolutionTankRecipe;
import com.pulsar.somatogenesis.recipe.IngredientStack;
import com.pulsar.somatogenesis.registry.SomatogenesisBlocks;
import com.pulsar.somatogenesis.registry.SomatogenesisRecipes;
import com.pulsar.somatogenesis.util.ImplementedInventory;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EvolutionTankBlockEntity extends BlockEntity implements ExtendedMenuProvider, ImplementedInventory {
    private final NonNullList<ItemStack> items = NonNullList.withSize(13, ItemStack.EMPTY);
    private Player owner;

    private EvolutionTankRecipe currentRecipe = null;
    private int craftTick = 0;
    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int i) {
            return switch (i) {
                case 0 -> craftTick;
                case 1 -> currentRecipe == null ? 0 : currentRecipe.getTime();
                default -> 0;
            };
        }

        @Override
        public void set(int i, int j) {
            if (i == 0) {
                craftTick = j;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public EvolutionTankBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SomatogenesisBlocks.EVOLUTION_TANK_ENTITY.get(), blockPos, blockState);
    }

    public Player getOwner() {
        return this.owner;
    }

    public void setOwner(Player player) {
        this.owner = player;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public void serverTick() {
        assert this.level != null;
        if (this.currentRecipe == null && !this.items.isEmpty()) {
            Optional<EvolutionTankRecipe> recipe = this.level.getRecipeManager().getRecipeFor(SomatogenesisRecipes.EVOLUTION_TANK_TYPE.get(), this, this.level);
            recipe.ifPresent(o -> {
                this.currentRecipe = o;
            });
        }
        if (this.currentRecipe != null) {
            if (!this.currentRecipe.matches(this, this.level)) {
                this.currentRecipe = null;
                return;
            }
            craftTick++;
            if (craftTick >= this.currentRecipe.getTime()) {
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
        if (this.getItem(0).isEmpty() || this.getItem(0).getCount() != 1
                || !this.currentRecipe.getInput().test(this.getItem(0))) return;
        for (IngredientStack ingredient : this.currentRecipe.getIngredientList()) {
            int toReduce = ingredient.count();
            for (int i = 1; i < 13; i++) {
                if (ingredient.ingredient().test(this.getItem(i))) {
                    int reduction = Math.min(toReduce, this.getItem(i).getCount());
                    this.getItem(i).shrink(reduction);
                    toReduce -= reduction;
                    if (toReduce == 0) break;
                }
            }
        }
        this.setItem(0, this.currentRecipe.getResult());
    }

    public void onBreak() {
        NonNullList<ItemStack> toDrop = NonNullList.create();
        toDrop.addAll(this.getItems());
        Containers.dropContents(Objects.requireNonNull(this.getLevel()), this.getBlockPos(), toDrop);
        this.clearContent();
    }

    @Override
    public void load(CompoundTag compoundTag) {
        craftTick = compoundTag.getInt("craftTick");
        ContainerHelper.loadAllItems(compoundTag, items);
        super.load(compoundTag);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        compoundTag.putInt("craftTick", craftTick);
        ContainerHelper.saveAllItems(compoundTag, items);
        super.saveAdditional(compoundTag);
    }

    @Override
    public void saveExtraData(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.getBlockPos());
    }

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new EvolutionTankMenu(i, inventory, this, containerData);
    }
}
