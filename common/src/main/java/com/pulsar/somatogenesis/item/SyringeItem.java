package com.pulsar.somatogenesis.item;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.accessor.TransfusionAccessor;
import com.pulsar.somatogenesis.recipe.BloodTransfusionRecipe;
import com.pulsar.somatogenesis.registry.SomatogenesisEntities;
import com.pulsar.somatogenesis.registry.SomatogenesisRecipes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SyringeItem extends Item {
    public SyringeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        if (itemStack.getOrCreateTag().contains("bloodType")) {
            String bloodType = itemStack.getOrCreateTag().getString("bloodType");
            EntityType<?> type = SomatogenesisEntities.ENTITY_TYPES.getRegistrar().get(ResourceLocation.tryParse(bloodType));
            list.add(Component.translatable("tooltip.somatogenesis.blood_type").append(": ").append(
                    type.getDescription()).withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_RED)));
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (player.getItemInHand(interactionHand).getOrCreateTag().contains("bloodType")) {
            if (player.isCrouching()) {
                String bloodType = player.getItemInHand(interactionHand).getOrCreateTag().getString("bloodType");
                EntityType<?> type = SomatogenesisEntities.ENTITY_TYPES.getRegistrar().get(ResourceLocation.tryParse(bloodType));
                List<BloodTransfusionRecipe> valid = level.getRecipeManager().getAllRecipesFor(SomatogenesisRecipes.BLOOD_TRANSFUSION_TYPE.get())
                        .stream().filter((recipe) -> recipe.matches(type)).toList();
                if (!valid.isEmpty()) {
                    ((TransfusionAccessor)player).somatogenesis$addBlood(type, 5);
                    player.getItemInHand(interactionHand).getOrCreateTag().remove("bloodType");
                }
            }
        } else {
            if (player.isCrouching()) {
                Set<Map.Entry<EntityType<?>, Integer>> transfusions = ((TransfusionAccessor) player).somatogenesis$getBlood().entrySet();
                if (!transfusions.isEmpty()) {
                    Map.Entry<EntityType<?>, Integer> random = transfusions.stream().toList().get(Mth.floor(Math.random() * transfusions.size()));
                    List<BloodTransfusionRecipe> valid = level.getRecipeManager().getAllRecipesFor(SomatogenesisRecipes.BLOOD_TRANSFUSION_TYPE.get())
                            .stream().filter((recipe) -> recipe.matches(random.getKey())).toList();
                    if (!valid.isEmpty()) {
                        ((TransfusionAccessor) player).somatogenesis$addBlood(random.getKey(), -5);
                        player.getItemInHand(interactionHand).getOrCreateTag().putString("bloodType", SomatogenesisEntities.ENTITY_TYPES.getRegistrar().getId(random.getKey()).toString());
                    }
                }
            }
        }
        return super.use(level, player, interactionHand);
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity attacked, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            if (!itemStack.getOrCreateTag().contains("bloodType")) {
                List<BloodTransfusionRecipe> valid = attacker.level().getRecipeManager().getAllRecipesFor(SomatogenesisRecipes.BLOOD_TRANSFUSION_TYPE.get())
                        .stream().filter((recipe) -> recipe.matches(attacked.getType())).toList();
                if (!valid.isEmpty()) {
                    itemStack.getOrCreateTag().putString("bloodType", SomatogenesisEntities.ENTITY_TYPES.getRegistrar().getId(attacked.getType()).toString());
                } else {
                    player.displayClientMessage(Component.translatable("somatogenesis.no_blood"), true);
                }
            }
        }
        return true;
    }
}
