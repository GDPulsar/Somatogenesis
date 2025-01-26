package com.pulsar.somatogenesis.item;

import com.pulsar.somatogenesis.accessor.TransfusionAccessor;
import com.pulsar.somatogenesis.recipe.BloodTransfusionRecipe;
import com.pulsar.somatogenesis.registry.SomatogenesisRecipes;
import com.pulsar.somatogenesis.registry.SomatogenesisTags;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SyringeItem extends SimpleTypedBloodContainerItem {
    public SyringeItem(Properties properties) {
        super(properties, 400);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        player.startUsingItem(interactionHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity player, ItemStack stack, int i) {
        if (!player.isCrouching()) {
            if (ProjectileUtil.getHitResultOnViewVector(player, (entity) -> entity instanceof LivingEntity, 5) instanceof EntityHitResult entityHitResult) {
                if (!entityHitResult.getEntity().getType().is(SomatogenesisTags.NO_BLOOD) && entityHitResult.getEntity() instanceof LivingEntity living) {
                    if (stack.getOrCreateTag().contains("bloodType")) {
                        if (getBloodType(stack) != living.getType()) {
                            player.stopUsingItem();
                            return;
                        }
                    }
                    int useTime = this.getUseDuration(stack) - i;
                    if (useTime % 10 == 0 && useTime >= 10) {
                        List<BloodTransfusionRecipe> valid = living.level().getRecipeManager().getAllRecipesFor(SomatogenesisRecipes.BLOOD_TRANSFUSION_TYPE.get())
                                .stream().filter((recipe) -> recipe.matches(living.getType())).toList();
                        if (!valid.isEmpty()) {
                            setBloodType(stack, (EntityType<? extends LivingEntity>)living.getType());
                            addBlood(stack, 20);
                            player.playSound(SoundEvents.BOTTLE_FILL);
                        } else {
                            ((Player)player).displayClientMessage(Component.translatable("somatogenesis.no_blood"), true);
                        }
                    }
                    return;
                }
            }
            player.stopUsingItem();
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity player, int i) {
        int useTime = this.getUseDuration(stack) - i;
        if (useTime > 20) {
            if (stack.getOrCreateTag().contains("bloodType")) {
                if (player.isCrouching()) {
                    EntityType<?> type = getBloodType(stack);
                    List<BloodTransfusionRecipe> valid = level.getRecipeManager().getAllRecipesFor(SomatogenesisRecipes.BLOOD_TRANSFUSION_TYPE.get())
                            .stream().filter((recipe) -> recipe.matches(type)).toList();
                    if (!valid.isEmpty()) {
                        ((TransfusionAccessor)player).somatogenesis$addBlood(type, getBlood(stack) / 20);
                        setBloodType(stack, null);
                        setBlood(stack, 0);
                    }
                }
            } else {
                if (player.isCrouching()) {
                    Set<Map.Entry<EntityType<?>, Integer>> transfusions = ((TransfusionAccessor) player).somatogenesis$getBlood().entrySet();
                    if (!transfusions.isEmpty()) {
                        Map.Entry<EntityType<?>, Integer> random = transfusions.stream().toList().get(Mth.floor(Math.random() * transfusions.size()));
                        if (getBloodType(stack) != null) {
                            random = Map.entry(getBloodType(stack), ((TransfusionAccessor)player).somatogenesis$getBloodOf(getBloodType(stack)));
                        }
                        Map.Entry<EntityType<?>, Integer> finalRandom = random;
                        List<BloodTransfusionRecipe> valid = level.getRecipeManager().getAllRecipesFor(SomatogenesisRecipes.BLOOD_TRANSFUSION_TYPE.get())
                                .stream().filter((recipe) -> recipe.matches(finalRandom.getKey())).toList();
                        if (!valid.isEmpty()) {
                            int canTake = Math.min(random.getValue(), (getMaxBlood() - getBlood(stack)) / 20);
                            ((TransfusionAccessor) player).somatogenesis$addBlood(random.getKey(), -canTake);
                            addBlood(stack, canTake * 20);
                            setBloodType(stack, (EntityType<? extends LivingEntity>) random.getKey());
                        }
                    }
                }
            }
        }
    }
}
