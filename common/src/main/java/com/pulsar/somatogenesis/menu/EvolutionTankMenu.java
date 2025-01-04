package com.pulsar.somatogenesis.menu;

import com.pulsar.somatogenesis.registry.SomatogenesisMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EvolutionTankMenu extends AbstractContainerMenu {
    private Container inventory;
    private ContainerData containerData;
    private BlockPos pos;

    public EvolutionTankMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(13), new SimpleContainerData(2));
    }

    public EvolutionTankMenu(int i, Inventory inventory, FriendlyByteBuf buf) {
        this(i, inventory, buf.readBlockPos());
    }

    public EvolutionTankMenu(int i, Inventory playerInventory, BlockPos pos) {
        this(i, playerInventory, (Container)playerInventory.player.level().getBlockEntity(pos), new SimpleContainerData(2));
        this.pos = pos;
    }

    public EvolutionTankMenu(int syncId, Inventory playerInventory, Container inventory, ContainerData containerData) {
        super(SomatogenesisMenus.EVOLUTION_TANK_MENU.get(), syncId);
        checkContainerSize(inventory, 13);
        this.inventory = inventory;
        this.inventory.startOpen(playerInventory.player);
        this.containerData = containerData;
        this.pos = BlockPos.ZERO;
        this.addSlot(new Slot(this.inventory, 0, 80, 34));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                this.addSlot(new Slot(this.inventory, i * 2 + j + 1, 26 + j * 18, 18 + i * 18));
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                this.addSlot(new Slot(this.inventory, 6 + i * 2 + j + 1, 116 + j * 18, 18 + i * 18));
            }
        }
        this.addDataSlots(containerData);

        for(int i = 0; i < 3; i++) {
            for(int l = 0; l < 9; l++) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }

        for(int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getCraftTick() {
        return containerData.get(0);
    }

    public int getCraftTime() {
        return containerData.get(1);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasItem()) {
            ItemStack original = slot.getItem();
            stack = original.copy();
            if (invSlot < this.inventory.getContainerSize()) {
                if (!this.moveItemStackTo(original, this.inventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(original, 0, this.inventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (original.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return stack;
    }
}
