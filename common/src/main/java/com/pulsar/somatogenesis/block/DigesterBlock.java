package com.pulsar.somatogenesis.block;

import com.pulsar.somatogenesis.item.BloodContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DigesterBlock extends BaseEntityBlock {
    public DigesterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return super.getStateForPlacement(blockPlaceContext).setValue(BlockStateProperties.HORIZONTAL_FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public void destroy(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        if (levelAccessor.getBlockEntity(blockPos) instanceof DigesterBlockEntity digester) {
            digester.onBreak();
        }
        super.destroy(levelAccessor, blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return (level1, blockPos, blockState1, blockEntity) -> {
            if (blockEntity instanceof DigesterBlockEntity digester) {
                if (level1.isClientSide) {
                    digester.clientTick();
                } else {
                    digester.serverTick();
                }
            }
        };
    }

    @Override
    public @NotNull InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!level.isClientSide) {
            if (level.getBlockEntity(blockPos) instanceof DigesterBlockEntity digester) {
                if (digester.getOwner() == null) {
                    digester.setOwner(player);
                }
            }
            if (!player.getItemInHand(interactionHand).isEmpty()) {
                ItemStack stack = player.getItemInHand(interactionHand);
                if (level.getBlockEntity(blockPos) instanceof DigesterBlockEntity digester) {
                    if (stack.getItem() instanceof BloodContainer && !player.isCrouching() && digester.getMaxBlood() != 0) {
                        digester.transferFrom(stack);
                        return InteractionResult.SUCCESS;
                    }
                    ItemStack result = digester.addItem(stack);
                    player.setItemInHand(interactionHand, result);
                    return InteractionResult.SUCCESS;
                }
            } else {
                if (level.getBlockEntity(blockPos) instanceof DigesterBlockEntity digester) {
                    player.addItem(digester.takeItem());
                    return InteractionResult.SUCCESS;
                }
            }
        } else {
            return InteractionResult.CONSUME;
        }
        return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }
}
