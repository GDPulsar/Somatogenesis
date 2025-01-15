package com.pulsar.somatogenesis.block;

import com.pulsar.somatogenesis.item.BloodContainer;
import com.pulsar.somatogenesis.registry.SomatogenesisBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BloodAltarBlock extends BaseEntityBlock {
    public static final EnumProperty<Tier> TIER = EnumProperty.create("tier", Tier.class);

    public BloodAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TIER);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void destroy(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        if (levelAccessor.getBlockEntity(blockPos) instanceof BloodAltarBlockEntity entity) {
            entity.onBreak();
        }
        super.destroy(levelAccessor, blockPos, blockState);
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        serverLevel.playSound(null, blockPos, SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, SoundSource.BLOCKS, 1f, 0.5f);
        super.randomTick(blockState, serverLevel, blockPos, randomSource);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BloodAltarBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return (level1, blockPos, blockState1, blockEntity) -> {
            if (blockEntity instanceof BloodAltarBlockEntity altar) {
                if (level1.isClientSide) {
                    altar.clientTick();
                } else {
                    altar.serverTick();
                }
            }
        };
    }

    @Override
    public @NotNull InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!level.isClientSide) {
            if (level.getBlockEntity(blockPos) instanceof BloodAltarBlockEntity blockEntity) {
                if (blockEntity.getOwner() == null) {
                    blockEntity.setOwner(player);
                }
            }
            if (!player.getItemInHand(interactionHand).isEmpty()) {
                ItemStack stack = player.getItemInHand(interactionHand);
                if (level.getBlockEntity(blockPos) instanceof BloodAltarBlockEntity blockEntity) {
                    if (stack.getItem() instanceof BloodContainer && !player.isCrouching()) {
                        blockEntity.transferFrom(stack);
                        return InteractionResult.SUCCESS;
                    }
                    ItemStack result = blockEntity.addItem(stack);
                    player.setItemInHand(interactionHand, result);
                    return InteractionResult.SUCCESS;
                }
            } else {
                if (level.getBlockEntity(blockPos) instanceof BloodAltarBlockEntity blockEntity) {
                    player.addItem(blockEntity.takeItem());
                    return InteractionResult.SUCCESS;
                }
            }
        } else {
            return InteractionResult.CONSUME;
        }
        return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    @Override
    public Item asItem() {
        return SomatogenesisBlocks.TIER_1_BLOOD_ALTAR_ITEM.get();
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return (switch (blockState.getValue(TIER)) {
            case IRON -> SomatogenesisBlocks.TIER_1_BLOOD_ALTAR_ITEM.get();
            case DIAMOND -> SomatogenesisBlocks.TIER_2_BLOOD_ALTAR_ITEM.get();
            case FLESH -> SomatogenesisBlocks.TIER_3_BLOOD_ALTAR_ITEM.get();
            case BONE -> SomatogenesisBlocks.TIER_4_BLOOD_ALTAR_ITEM.get();
            case LIVING -> SomatogenesisBlocks.TIER_5_BLOOD_ALTAR_ITEM.get();
        }).getDefaultInstance();
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        if (blockPlaceContext.getItemInHand().is(SomatogenesisBlocks.TIER_1_BLOOD_ALTAR_ITEM.get())) return this.defaultBlockState().setValue(TIER, Tier.IRON);
        if (blockPlaceContext.getItemInHand().is(SomatogenesisBlocks.TIER_2_BLOOD_ALTAR_ITEM.get())) return this.defaultBlockState().setValue(TIER, Tier.DIAMOND);
        if (blockPlaceContext.getItemInHand().is(SomatogenesisBlocks.TIER_3_BLOOD_ALTAR_ITEM.get())) return this.defaultBlockState().setValue(TIER, Tier.FLESH);
        if (blockPlaceContext.getItemInHand().is(SomatogenesisBlocks.TIER_4_BLOOD_ALTAR_ITEM.get())) return this.defaultBlockState().setValue(TIER, Tier.BONE);
        if (blockPlaceContext.getItemInHand().is(SomatogenesisBlocks.TIER_5_BLOOD_ALTAR_ITEM.get())) return this.defaultBlockState().setValue(TIER, Tier.LIVING);
        return super.getStateForPlacement(blockPlaceContext);
    }

    public enum Tier implements StringRepresentable {
        IRON,
        DIAMOND,
        FLESH,
        BONE,
        LIVING;

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase();
        }

        public static Tier fromString(String tier) {
            return switch (tier.toLowerCase()) {
                case "diamond" -> DIAMOND;
                case "flesh" -> FLESH;
                case "bone" -> BONE;
                case "living" -> LIVING;
                default -> IRON;
            };
        }
    }
}
