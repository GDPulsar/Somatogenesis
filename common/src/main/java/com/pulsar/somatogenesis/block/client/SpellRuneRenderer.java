package com.pulsar.somatogenesis.block.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.block.BloodAltarBlockEntity;
import com.pulsar.somatogenesis.block.SpellRuneBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2i;

public class SpellRuneRenderer implements BlockEntityRenderer<SpellRuneBlockEntity> {
    private static final ResourceLocation BLOOD_PAINT = Somatogenesis.reloc("textures/other/blood_paint.png");

    public SpellRuneRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(SpellRuneBlockEntity entity, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {
        poseStack.pushPose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
        RenderSystem.setShaderTexture(0, BLOOD_PAINT);
        Matrix4f m = poseStack.last().pose();
        for (Vector2i drawn : entity.getDrawn()) {
            Vec3 a = new Vec3(drawn.x / 16f + 0.5f, 0f, drawn.y / 16f + 0.5f);
            Vec3 b = new Vec3((drawn.x + 1) / 16f + 0.5f, 0f, (drawn.y + 1) / 16f + 0.5f);
            boolean north = entity.getDrawn().contains(new Vector2i(drawn.x, drawn.y + 1));
            boolean east = entity.getDrawn().contains(new Vector2i(drawn.x + 1, drawn.y));
            boolean south = entity.getDrawn().contains(new Vector2i(drawn.x, drawn.y - 1));
            boolean west = entity.getDrawn().contains(new Vector2i(drawn.x - 1, drawn.y));
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
            buffer.vertex(m, (float)a.x, 0.01f, (float)a.z).color(0xFFFFFFFF).uv(west ? 0.25f : 0f, south ? 0.25f : 0f).uv2(light).endVertex();
            buffer.vertex(m, (float)a.x, 0.01f, (float)b.z).color(0xFFFFFFFF).uv(west ? 0.25f : 0f, north ? 0.75f : 1f).uv2(light).endVertex();
            buffer.vertex(m, (float)b.x, 0.01f, (float)b.z).color(0xFFFFFFFF).uv(east ? 0.75f : 1f, north ? 0.75f : 1f).uv2(light).endVertex();
            buffer.vertex(m, (float)b.x, 0.01f, (float)a.z).color(0xFFFFFFFF).uv(east ? 0.75f : 1f, south ? 0.25f : 0f).uv2(light).endVertex();
            tesselator.end();
        }
        if (entity.getHighlight() != null) {
            for (Vector2i drawn : entity.getHighlight().positions()) {
                Vec3 a = new Vec3(drawn.x / 16f + 0.5f, 0f, drawn.y / 16f + 0.5f);
                Vec3 b = new Vec3((drawn.x + 1) / 16f + 0.5f, 0f, (drawn.y + 1) / 16f + 0.5f);
                boolean north = entity.getHighlight().positions().contains(new Vector2i(drawn.x, drawn.y + 1));
                boolean east = entity.getHighlight().positions().contains(new Vector2i(drawn.x + 1, drawn.y));
                boolean south = entity.getHighlight().positions().contains(new Vector2i(drawn.x, drawn.y - 1));
                boolean west = entity.getHighlight().positions().contains(new Vector2i(drawn.x - 1, drawn.y));
                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
                buffer.vertex(m, (float)a.x, 0.01f, (float)a.z).color(0x33FFFFFF).uv(west ? 0.25f : 0f, south ? 0.25f : 0f).uv2(light).endVertex();
                buffer.vertex(m, (float)a.x, 0.01f, (float)b.z).color(0x33FFFFFF).uv(west ? 0.25f : 0f, north ? 0.75f : 1f).uv2(light).endVertex();
                buffer.vertex(m, (float)b.x, 0.01f, (float)b.z).color(0x33FFFFFF).uv(east ? 0.75f : 1f, north ? 0.75f : 1f).uv2(light).endVertex();
                buffer.vertex(m, (float)b.x, 0.01f, (float)a.z).color(0x33FFFFFF).uv(east ? 0.75f : 1f, south ? 0.25f : 0f).uv2(light).endVertex();
                tesselator.end();
            }
        }
        RenderSystem.disableBlend();
        poseStack.popPose();
    }
}
