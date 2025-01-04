package com.pulsar.somatogenesis.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleTypedBloodContainerItem extends SimpleBloodContainerItem implements TypedBloodContainer {
    private EntityType<? extends LivingEntity> bloodType;

    public SimpleTypedBloodContainerItem(Properties properties, int maxBlood) {
        super(properties, maxBlood);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        if (this.bloodType != null) {
            list.add(Component.translatable("tooltip.somatogenesis.blood_type").append(": ").append(
                    this.getBloodType().toString()).withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_RED)));
        }
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }

    @Override
    public EntityType<? extends LivingEntity> getBloodType() {
        return bloodType;
    }

    @Override
    public void setBloodType(EntityType<? extends LivingEntity> type) {
        this.bloodType = type;
    }

    @Override
    public void setTypeFrom(LivingEntity entity) {
        this.bloodType = (EntityType<? extends LivingEntity>)entity.getType();
    }
}
