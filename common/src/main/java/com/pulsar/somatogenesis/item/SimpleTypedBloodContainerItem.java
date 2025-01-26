package com.pulsar.somatogenesis.item;

import com.pulsar.somatogenesis.registry.SomatogenesisEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleTypedBloodContainerItem extends SimpleBloodContainerItem implements TypedBloodContainer {
    public SimpleTypedBloodContainerItem(Properties properties, int maxBlood) {
        super(properties, maxBlood);
    }

    @Override
    public void setBlood(ItemStack stack, int amount) {
        super.setBlood(stack, amount);
        if (amount <= 0) {
            this.setBloodType(stack, null);
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        if (itemStack.getOrCreateTag().contains("bloodType")) {
            String bloodType = itemStack.getOrCreateTag().getString("bloodType");
            EntityType<?> type = SomatogenesisEntities.ENTITY_TYPES.getRegistrar().get(ResourceLocation.tryParse(bloodType));
            list.add(Component.translatable("tooltip.somatogenesis.blood_type").append(": ").append(
                    type.toString()).withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_RED)));
        }
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }

    @Override
    public EntityType<? extends LivingEntity> getBloodType(ItemStack stack) {
        if (stack.getOrCreateTag().contains("bloodType")) {
            String bloodType = stack.getOrCreateTag().getString("bloodType");
            return (EntityType<? extends LivingEntity>)SomatogenesisEntities.ENTITY_TYPES.getRegistrar().get(ResourceLocation.tryParse(bloodType));
        }
        return null;
    }

    @Override
    public void setBloodType(ItemStack stack, EntityType<? extends LivingEntity> type) {
        if (type == null) {
            stack.getOrCreateTag().remove("bloodType");
            return;
        }
        stack.getOrCreateTag().putString("bloodType", SomatogenesisEntities.ENTITY_TYPES.getRegistrar().getId(type).toString());
    }
}
