package com.pulsar.somatogenesis.item;

import com.pulsar.somatogenesis.accessor.TransfusionAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.Map;

public class BloodMirrorItem extends Item {
    public BloodMirrorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        player.startUsingItem(interactionHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof Player player) {
            TransfusionAccessor transfusions = (TransfusionAccessor)player;
            MutableComponent text = Component.translatable("somatogenesis.transfusion_status");
            int i = 0;
            for (Map.Entry<EntityType<?>, Integer> entry : transfusions.somatogenesis$getBlood().entrySet()) {
                text.append(entry.getKey().getDescription()).append(": ").append(entry.getValue().toString()).append("%");
                i++;
                if (i < transfusions.somatogenesis$getBlood().size()) {
                    text.append(", ");
                }
            }
            player.sendSystemMessage(text);
        }
        return super.finishUsingItem(itemStack, level, livingEntity);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 40;
    }
}
