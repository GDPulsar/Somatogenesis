package com.pulsar.somatogenesis.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class SimpleBloodContainerItem extends Item implements BloodContainer {
    private final int maxBlood;

    public SimpleBloodContainerItem(Properties properties, int maxBlood) {
        super(properties);
        this.maxBlood = maxBlood;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("tooltip.somatogenesis.blood").append(": ").append(String.valueOf(getBlood(itemStack)))
                .append("/").append(String.valueOf(this.maxBlood)).withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_RED)));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }

    @Override
    public int getMaxBlood() {
        return maxBlood;
    }

    @Override
    public void setBlood(ItemStack stack, int amount) {
        stack.getOrCreateTag().putInt("blood", Mth.clamp(amount, 0, maxBlood));
    }

    @Override
    public int getBlood(ItemStack stack) {
        return stack.getOrCreateTag().contains("blood") ? stack.getOrCreateTag().getInt("blood") : 0;
    }

    @Override
    public void addBlood(ItemStack stack, int amount) {
        if (!stack.getOrCreateTag().contains("blood")) stack.getOrCreateTag().putInt("blood", 0);
        setBlood(stack, getBlood(stack) + amount);
    }

    @Override
    public void useBlood(ItemStack stack, int amount) {
        if (!stack.getOrCreateTag().contains("blood")) stack.getOrCreateTag().putInt("blood", 0);
        setBlood(stack, getBlood(stack) - amount);
    }

    @Override
    public boolean hasBlood(ItemStack stack, int amount) {
        return stack.getOrCreateTag().contains("blood") && getBlood(stack) >= amount;
    }

    @Override
    public int tryTransferBlood(ItemStack stack, int amount) {
        int leftover = amount - (this.maxBlood - getBlood(stack));
        addBlood(stack, amount);
        return Math.max(leftover, 0);
    }
}
