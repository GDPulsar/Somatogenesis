package com.pulsar.somatogenesis.menu;

import com.pulsar.somatogenesis.Somatogenesis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EvolutionTankScreen extends AbstractContainerScreen<EvolutionTankMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = Somatogenesis.reloc("textures/gui/container/evolution_tank.png");
    private EvolutionTankMenu menu;

    public EvolutionTankScreen(EvolutionTankMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.menu = menu;
        this.inventoryLabelY = 2000;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        this.renderBackground(guiGraphics);
        this.renderProgress(guiGraphics, tickDelta);
        super.render(guiGraphics, mouseX, mouseY, tickDelta);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    protected void renderBg(GuiGraphics guiGraphics, float tickDelta, int mouseX, int mouseY) {
        int x = (this.width - 176) / 2;
        int y = (this.height - 166) / 2;
        guiGraphics.blit(CONTAINER_BACKGROUND, x, y, 0, 0, 176, 166, 256, 256);
    }

    protected void renderProgress(GuiGraphics guiGraphics, float tickDelta) {
        int x = (this.width - 176) / 2;
        int y = (this.height - 166) / 2;
        float progress = (menu.getCraftTick() + tickDelta) / menu.getCraftTime();
        guiGraphics.enableScissor(x + 77, y + 59 - (int)(35f * progress), x + 99, y + 59);
        guiGraphics.blit(CONTAINER_BACKGROUND, x + 77, y + 24, 176, 0, 22, 35, 22, 35);
        guiGraphics.disableScissor();
    }
}
