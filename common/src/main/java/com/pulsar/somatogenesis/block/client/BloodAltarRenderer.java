package com.pulsar.somatogenesis.block.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.block.BloodAltarBlockEntity;
import com.pulsar.somatogenesis.registry.SomatogenesisBlocks;
import com.pulsar.somatogenesis.registry.SomatogenesisFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.Map;

public class BloodAltarRenderer implements BlockEntityRenderer<BloodAltarBlockEntity> {
    private static final ResourceLocation BLOOD_TEXTURE = Somatogenesis.reloc("textures/block/blood_still.png");

    public BloodAltarRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(BloodAltarBlockEntity entity, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {
        poseStack.pushPose();
        BlockState state = entity.getBlockState();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BakedModel bakedModel = blockRenderer.getBlockModel(state);
        VertexConsumer consumer = multiBufferSource.getBuffer(ItemBlockRenderTypes.getRenderType(state, false));
        blockRenderer.getModelRenderer().renderModel(poseStack.last(), consumer, state, bakedModel, 1f, 1f, 1f, light, overlay);
        float angleStep = 360f / entity.getItems().size();
        int lightAbove = LevelRenderer.getLightColor(entity.getLevel(), entity.getBlockPos().above());
        for (int i = 0; i < entity.getItems().size(); i++) {
            ItemStack stack = entity.getItems().get(i);
            if (stack.isEmpty()) continue;
            poseStack.pushPose();
            poseStack.translate(0.5f, 1.45f, 0.5f);
            poseStack.mulPose(new Quaternionf().rotationY(((entity.getLevel().getGameTime() + tickDelta) * 2.5f + angleStep * i) * Mth.DEG_TO_RAD));
            poseStack.translate(0f, 0f, 0.6f);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, lightAbove, overlay, poseStack, multiBufferSource, entity.getLevel(), 0);
            poseStack.popPose();
        }
        poseStack.popPose();
        if (entity.getBlood() != 0) {
            poseStack.pushPose();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            RenderSystem.setShaderTexture(0, BLOOD_TEXTURE);
            Matrix4f m = poseStack.last().pose();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
            float bottom = 5f / 16f;
            float top = 10f / 16f;
            float point = ((float) entity.getBlood() / entity.getMaxBlood()) * (top - bottom) + bottom;
            buffer.vertex(m, 0.3f, point, 0.3f).color(0xFFFFFFFF).uv(0f, 0f).uv2(light).endVertex();
            buffer.vertex(m, 0.3f, point, 0.7f).color(0xFFFFFFFF).uv(0f, 1f).uv2(light).endVertex();
            buffer.vertex(m, 0.7f, point, 0.7f).color(0xFFFFFFFF).uv(1f, 1f).uv2(light).endVertex();
            buffer.vertex(m, 0.7f, point, 0.3f).color(0xFFFFFFFF).uv(1f, 0f).uv2(light).endVertex();
            tesselator.end();
            poseStack.popPose();
        }
    }
}
