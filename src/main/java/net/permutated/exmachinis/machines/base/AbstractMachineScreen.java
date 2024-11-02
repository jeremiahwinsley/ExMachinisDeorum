package net.permutated.exmachinis.machines.base;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.permutated.exmachinis.util.ResourceUtil;
import net.permutated.exmachinis.util.TextureHolder;
import net.permutated.exmachinis.util.WorkStatus;

import java.util.List;

import static net.permutated.exmachinis.util.TranslationKey.translateTooltip;

public class AbstractMachineScreen<T extends AbstractMachineMenu> extends AbstractContainerScreen<T> {
    protected final ResourceLocation gui;

    protected AbstractMachineScreen(T container, Inventory inv, Component name, String machine) {
        super(container, inv, name);
        this.gui = ResourceUtil.gui(machine);
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(gui, relX, relY, 0, 0, this.imageWidth, this.imageHeight);


        int statusOffsetX = this.menu.enableComparatorSlot ? 134 : 116;
        // render work status texture
        if (this.menu.getWorkStatus() == WorkStatus.WORKING) {
            graphics.blit(gui, relX + statusOffsetX, relY + 36, 224, 0, 16, 16);
        } else if(this.menu.getWorkStatus() == WorkStatus.REDSTONE_DISABLED) {
            graphics.blit(gui, relX + statusOffsetX, relY + 36, 240, 0, 16, 16);
        } else {
            graphics.blit(gui, relX + statusOffsetX, relY + 36, 208, 0, 16, 16);
        }

        // texture offset - addon texture
        if (!this.menu.enableComparatorSlot) {
            float energyFraction = this.menu.dataHolder.getEnergyFraction();
            var energyHolder = new TextureHolder(152, 18, 176, 0, 16, 52);
            graphics.blit(gui,
                relX + energyHolder.progressOffsetX(),
                relY + energyHolder.progressHeightOffset(energyFraction),
                energyHolder.textureOffsetX(),
                energyHolder.textureOffsetY(),
                energyHolder.textureWidth(),
                energyHolder.getHeightFraction(energyFraction)
            );
        }

        // work progress
        float workFraction = this.menu.dataHolder.getWorkFraction();
        int progressOffsetX = this.menu.enableComparatorSlot ? 152 : 134;
        var progressHolder = new TextureHolder(progressOffsetX, 18, 192, 0, 16, 52);
        graphics.blit(gui,
            relX + progressHolder.progressOffsetX(),
            relY + progressHolder.progressHeightOffset(workFraction),
            progressHolder.textureOffsetX(),
            progressHolder.textureOffsetY(),
            progressHolder.textureWidth(),
            progressHolder.getHeightFraction(workFraction)
        );
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int x, int y) {
        super.renderTooltip(graphics, x, y);

        int upgradeOffsetX = this.menu.enableComparatorSlot ? 134 : 116;
        int progressOffsetX = this.menu.enableComparatorSlot ? 152 : 134;

        if (this.isHovering(upgradeOffsetX, 36, 16, 16, x, y)) {
            graphics.renderComponentTooltip(this.font, List.of(this.menu.getWorkStatus().getTranslation()), x, y);
        }

        if (this.isHovering(152, 18, 16, 52, x, y) && !this.menu.enableComparatorSlot) {
            graphics.renderComponentTooltip(this.font, List.of(
                translateTooltip("fluxBar"),
                translateTooltip("fluxData", this.menu.dataHolder.getEnergy(), this.menu.dataHolder.getMaxEnergy())
            ), x, y);
        }

        if (this.isHovering(progressOffsetX, 18, 16, 52, x, y)) {
            graphics.renderComponentTooltip(this.font, List.of(
                translateTooltip("workBar"),
                translateTooltip("workData", this.menu.dataHolder.getWork(), this.menu.dataHolder.getMaxWork())
            ), x, y);
        }

        if (this.isHovering(upgradeOffsetX, 54, 16, 16, x, y) && shouldShowSlotTooltip()) {
            graphics.renderComponentTooltip(this.font, List.of(translateTooltip("upgradeSlot")), x, y);
        }

        if (this.isHovering(upgradeOffsetX, 18, 16, 16, x, y) && this.menu.enableComparatorSlot && shouldShowSlotTooltip()) {
            graphics.renderComponentTooltip(this.font, List.of(translateTooltip("comparatorSlot")), x, y);
        }

        if (this.isHovering(80, 36, 16, 16, x, y) && this.menu.enableMeshSlot && shouldShowSlotTooltip()) {
            graphics.renderComponentTooltip(this.font, List.of(translateTooltip("meshSlot")), x, y);
        }
    }

    private boolean shouldShowSlotTooltip() {
        return this.menu.getCarried().isEmpty() && this.hoveredSlot != null && !this.hoveredSlot.hasItem();
    }
}
