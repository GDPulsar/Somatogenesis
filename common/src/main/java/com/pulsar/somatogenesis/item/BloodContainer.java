package com.pulsar.somatogenesis.item;

import net.minecraft.world.item.ItemStack;

public interface BloodContainer {
    int getMaxBlood();

    void setBlood(ItemStack stack, int amount);
    int getBlood(ItemStack stack);

    void addBlood(ItemStack stack, int amount);
    void useBlood(ItemStack stack, int amount);
    boolean hasBlood(ItemStack stack, int amount);

    int tryTransferBlood(ItemStack stack, int amount);
}
