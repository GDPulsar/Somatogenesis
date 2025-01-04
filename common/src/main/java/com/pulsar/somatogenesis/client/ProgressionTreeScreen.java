package com.pulsar.somatogenesis.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.accessor.ProgressionAccessor;
import com.pulsar.somatogenesis.progression.ProgressionData;
import com.pulsar.somatogenesis.progression.ProgressionUnlock;
import com.pulsar.somatogenesis.progression.requirements.ProgressionRequirement;
import com.pulsar.somatogenesis.registry.SomatogenesisNetworking;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Arrays;

public class ProgressionTreeScreen extends Screen {
    public static final ResourceLocation CONNECTOR_TEXTURE = Somatogenesis.reloc("textures/gui/connector.png");
    public static final ResourceLocation UNLOCK_BACKGROUND_TEXTURE = Somatogenesis.reloc("textures/gui/unlock_background.png");
    public static final ResourceLocation INCOMPLETE_TEXTURE = Somatogenesis.reloc("textures/gui/incomplete.png");
    public static final ResourceLocation COMPLETE_TEXTURE = Somatogenesis.reloc("textures/gui/complete.png");
    public static final ResourceLocation INCOMPLETE_BALL_TEXTURE = Somatogenesis.reloc("textures/gui/incomplete_ball.png");
    public static final ResourceLocation COMPLETE_BALL_TEXTURE = Somatogenesis.reloc("textures/gui/complete_ball.png");

    public ProgressionTreeScreen(Component component) {
        super(component);
    }

    double scroll = 0;
    double scrollAmount = 0;
    boolean wasClicked = false;
    ProgressionUnlock selected = null;
    ProgressionUnlock selecting = null;
    boolean deselecting = false;
    final int tooltipAreaSize = 25;
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float f) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            guiGraphics.fill(0, 0, guiGraphics.guiWidth(), guiGraphics.guiWidth(), 0x33000000);
            int rightTabWidth = (int)(rightTabOpen * guiGraphics.guiWidth() / 3f);
            if (wasClicked) {
                if (mouseX < guiGraphics.guiWidth() - rightTabWidth) {
                    if (selected != null) deselecting = true;
                }
            }
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(-rightTabWidth / 3f, 0f, 0f);
            int realMouseX = mouseX + rightTabWidth / 3;
            scroll += scrollAmount * Minecraft.getInstance().getDeltaFrameTime();
            scrollAmount *= 1f - Minecraft.getInstance().getDeltaFrameTime();
            int middleX = guiGraphics.guiWidth() / 2;
            int middleY = guiGraphics.guiHeight() / 4;
            ProgressionData progression = ((ProgressionAccessor)player).somatogenesis$getProgression();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            for (ProgressionUnlock unlock : progression.getUnlocks()) {
                int drawX = middleX + unlock.x();
                int drawY = (int) (middleY + unlock.y() + scroll);
                int alpha = unlock.accessible(player) ? 255 : 0;
                RenderSystem.enableBlend();
                RenderSystem.setShaderTexture(0, CONNECTOR_TEXTURE);
                RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
                RenderSystem.setShaderColor(1f, 1f, 1f,1f);
                for (ProgressionUnlock parent : Arrays.stream(unlock.parents()).map(progression::getUnlock).toList()) {
                    if (parent == null) continue;
                    if (!parent.accessible(player)) continue;
                    int parentX = middleX + parent.x();
                    int parentY = (int)(middleY + parent.y() + scroll);
                    float length = Mth.sqrt(new Vec2(drawX, drawY).distanceToSqr(new Vec2(parentX, parentY)));
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(drawX, drawY, 0);
                    guiGraphics.pose().mulPose(Axis.ZP.rotation((float)Mth.atan2(parentY - drawY, parentX - drawX)));
                    Matrix4f m = guiGraphics.pose().last().pose();
                    buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
                    buffer.vertex(m, 0, -5, 0).color(255, 255, 255, alpha).uv(length / 10f, 0).endVertex();
                    buffer.vertex(m, 0, 5, 0).color(255, 255, 255, alpha).uv(length / 10f, 1).endVertex();
                    buffer.vertex(m, length, 5, 0).color(255, 255, 255, 255).uv(0, 1).endVertex();
                    buffer.vertex(m, length, -5, 0).color(255, 255, 255, 255).uv(0, 0).endVertex();
                    tesselator.end();
                    guiGraphics.pose().popPose();
                }
                RenderSystem.disableBlend();
                if (unlock.accessible(player)) {
                    guiGraphics.blit(UNLOCK_BACKGROUND_TEXTURE, drawX - 12, drawY - 12, 0, 0, 24, 24, 24, 24);
                    guiGraphics.drawCenteredString(Minecraft.getInstance().font, unlock.getTitle(), drawX, drawY - 25, 0xFFFFFF);
                    guiGraphics.renderItem(new ItemStack(unlock.icon()), drawX - 8, drawY - 8);
                    if (realMouseX >= drawX - tooltipAreaSize && realMouseX <= drawX + tooltipAreaSize && mouseY >= drawY - tooltipAreaSize && mouseY <= drawY + tooltipAreaSize) {
                        guiGraphics.renderTooltip(Minecraft.getInstance().font, unlock.getTooltip(), mouseX, mouseY);
                        if (wasClicked) {
                            if (selected == null) {
                                selected = unlock;
                            } else if (unlock != selected) {
                                selecting = unlock;
                                deselecting = false;
                            }
                        }
                    }
                }
            }
            guiGraphics.pose().popPose();

            if (selected != null) {
                if (!deselecting && selecting == null) rightTabOpen = Mth.lerp(Minecraft.getInstance().getDeltaFrameTime() / 4f, rightTabOpen, 1f);
                drawTab(guiGraphics, selected, player, mouseX, mouseY);
            }
            if (selecting != null || deselecting || selected == null) {
                rightTabOpen = Mth.lerp(Minecraft.getInstance().getDeltaFrameTime() / 4f, rightTabOpen, 0f);
                if (rightTabOpen <= 0.1) {
                    selected = null;
                    deselecting = false;
                    if (selecting != null) {
                        selected = selecting;
                        selecting = null;
                    }
                }
            }

            wasClicked = false;
        }
    }

    float rightTabOpen = 0f;
    private void drawTab(GuiGraphics guiGraphics, ProgressionUnlock unlock, Player player, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        int tabWidth = (int)(rightTabOpen * guiGraphics.guiWidth() / 3f);
        int maxTabWidth = guiGraphics.guiWidth() / 3;
        int left = guiGraphics.guiWidth() - tabWidth;
        int textTop = guiGraphics.guiHeight() / 3;

        /// drawing tab
        guiGraphics.fill(left, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), 0xFF330000);
        guiGraphics.vLine(left, 0, guiGraphics.guiHeight(), 0xFFFFFFFF);
        guiGraphics.vLine(left + 1, 0, guiGraphics.guiHeight(), 0xFFFFFFFF);

        // draw title
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(left + 25, textTop, 0f);
        guiGraphics.pose().scale(1.5f, 1.5f, 1f);
        guiGraphics.drawString(Minecraft.getInstance().font, unlock.getTitle(), 0, 0, 0xFFFFFFFF);
        guiGraphics.pose().popPose();

        // draw tooltip and description
        guiGraphics.drawString(Minecraft.getInstance().font, unlock.getTooltip(), left + 25, textTop + 35, 0xFF999999);
        MutableComponent description = unlock.getDescription();
        if (!unlock.completed(player)) description.setStyle(Style.EMPTY.withObfuscated(true));
        guiGraphics.drawWordWrap(Minecraft.getInstance().font, description, left + 25, textTop + 50, maxTabWidth - 50, 0xFFFFFFFF);

        // draw requirements
        int descriptionSize = Minecraft.getInstance().font.wordWrapHeight(unlock.getDescription(), maxTabWidth - 50);
        int current = textTop + 100 + descriptionSize;
        int ballTop = textTop + 70 + descriptionSize;
        int i = 0;
        for (ProgressionRequirement requirement : unlock.requirements()) {
            int ballMiddle = (int)(left + ((float)i / unlock.requirements().length) * maxTabWidth + maxTabWidth / 2f);
            guiGraphics.blit(requirement.completed(player) ? COMPLETE_BALL_TEXTURE : INCOMPLETE_BALL_TEXTURE, ballMiddle - 10, ballTop, 0, 0, 20, 20, 20, 20);
            i++;
            guiGraphics.blit(requirement.completed(player) ? COMPLETE_TEXTURE : INCOMPLETE_TEXTURE, left + 25, current - 5, 0, 0, 20, 20, 20, 20);
            guiGraphics.drawString(Minecraft.getInstance().font, requirement.getText(), left + 55, current, 0xFFFFFF);
            if (mouseX >= left + 15 && mouseX <= left + maxTabWidth - 30 && mouseY >= current - 5 && mouseY <= current + 10) {
                guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("progression_requirement.submit"), mouseX, mouseY);
                if (wasClicked) {
                    NetworkManager.sendToServer(SomatogenesisNetworking.SUBMIT_UNLOCK, new FriendlyByteBuf(Unpooled.buffer()).writeResourceLocation(selected.id()));
                }
            }
            current += 15;
        }

        RenderSystem.disableBlend();
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        wasClicked = true;
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        scrollAmount += f * 10.0;
        return super.mouseScrolled(d, e, f);
    }
}
