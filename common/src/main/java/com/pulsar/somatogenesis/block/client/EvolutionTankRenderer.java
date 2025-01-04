package com.pulsar.somatogenesis.block.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pulsar.somatogenesis.block.BloodAltarBlockEntity;
import com.pulsar.somatogenesis.block.EvolutionTankBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

public class EvolutionTankRenderer implements BlockEntityRenderer<EvolutionTankBlockEntity> {
    public EvolutionTankRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(EvolutionTankBlockEntity entity, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {
        poseStack.pushPose();
        BlockState state = entity.getBlockState();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BakedModel bakedModel = blockRenderer.getBlockModel(state);
        VertexConsumer consumer = multiBufferSource.getBuffer(ItemBlockRenderTypes.getRenderType(state, false));
        blockRenderer.getModelRenderer().renderModel(poseStack.last(), consumer, state, bakedModel, 1f, 1f, 1f, light, overlay);
        poseStack.popPose();
    }
}
