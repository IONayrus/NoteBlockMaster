package net.nayrus.noteblockmaster.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.utils.Utils;

import java.math.BigDecimal;

public class BaseScreen extends Screen {

    protected final ResourceLocation GUI;
    protected final int guiWidth;
    protected final int guiHeight;

    protected BaseScreen(ResourceLocation background, int guiWidth, int guiHeight) {
        super(Component.literal(""));
        this.GUI = background;
        this.guiWidth = guiWidth;
        this.guiHeight = guiHeight;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderTexture(0, GUI);
        guiGraphics.blit(GUI, getRelX() , getRelY(), 0, 0, this.guiWidth, this.guiHeight);
    }

    public int getRelX(){
        return (this.width - this.guiWidth) / 2;
    }

    public int getRelY(){
        return (this.height - this.guiHeight) / 2;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        if(!Utils.isIntInRange((int)mouseX, getRelX() , getRelX() + this.guiWidth)
                || !Utils.isIntInRange((int)mouseY, getRelY(), getRelY() + this.guiHeight))
            onClose();
    }

    public BigDecimal getScrollFactor(boolean reduceOnAlt){
        BigDecimal factor = BigDecimal.ONE;
        if (hasShiftDown()) factor = factor.multiply(BigDecimal.valueOf(2));
        if (hasControlDown()) factor = factor.multiply(BigDecimal.valueOf(5));
        if (hasAltDown()) factor = factor.multiply(BigDecimal.valueOf(reduceOnAlt ? 0.1 : 3));
        return factor;
    }

    public BigDecimal getScrollFactor(){
        return getScrollFactor(true);
    }
}
