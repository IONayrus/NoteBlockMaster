package net.nayrus.noteblockmaster.screen.base;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.utils.Utils;

public class BaseScreen extends Screen {

    protected final ResourceLocation GUI;
    protected final int guiWidth;
    protected final int guiHeight;
    protected final int guiFileWidth;
    protected final int guiFileHeight;

    protected BaseScreen(ResourceLocation background, int guiWidth, int guiHeight, int guiFileWidth, int guiFileHeight) {
        super(Component.literal(""));
        this.GUI = background;
        this.guiWidth = guiWidth;
        this.guiHeight = guiHeight;
        this.guiFileWidth = guiFileWidth;
        this.guiFileHeight = guiFileHeight;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(RenderType::guiTextured, GUI, getRelX() , getRelY(), 0, 0, this.guiWidth, this.guiHeight, guiFileWidth, guiFileHeight);
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
}
