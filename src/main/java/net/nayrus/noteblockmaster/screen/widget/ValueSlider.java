package net.nayrus.noteblockmaster.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class ValueSlider extends AbstractSliderButton {

    private final Consumer<Double> callback;

    public ValueSlider(int x, int y, int width, int height, double value, Consumer<Double> callback) {
        super(x, y, width, height, Component.literal(""), value);
        this.callback = callback;
    }
    //TODO Try to actually get the text render below the handle, methods below do nothing :(
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        guiGraphics.blitSprite(this.getSprite(), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        int i = this.active ? 16777215 : 10526880;
        this.renderScrollingString(guiGraphics, minecraft.font, 2, i & 0x00FFFFFF);
        guiGraphics.blitSprite(this.getHandleSprite(), this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 8, this.getHeight());
    }

    @Override
    protected void renderScrollingString(GuiGraphics guiGraphics, Font font, int width, int color) {
        int i = this.getX() + width;
        int j = this.getX() + this.getWidth() - width;
        renderScrollingString(guiGraphics, font, this.getMessage(), i, this.getY(), j, this.getY() + this.getHeight(), color);
    }

    public static void renderScrollingString(
            GuiGraphics guiGraphics, Font font, Component text, int minX, int minY, int maxX, int maxY, int color
    ) {
        int centerX = (minX + maxX) / 2;
        int i = font.width(text);
        int j = (minY + maxY - 9) / 2 + 1;
        int k = maxX - minX;
        if (i > k) {
            int l = i - k;
            double d0 = (double) Util.getMillis() / 1000.0;
            double d1 = Math.max((double)l * 0.5, 3.0);
            double d2 = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d0 / d1)) / 2.0 + 0.5;
            double d3 = Mth.lerp(d2, 0.0, l);
            guiGraphics.enableScissor(minX, minY, maxX, maxY);
            guiGraphics.drawString(font, text, minX - (int)d3, j, color);
            guiGraphics.disableScissor();
        } else {
            int i1 = Mth.clamp(centerX, minX + i / 2, maxX - i / 2);
            guiGraphics.drawCenteredString(font, text, i1, j, color);
        }
    }

    @Override
    protected void updateMessage() {}

    @Override
    protected void applyValue() {
        callback.accept(this.value);
    }

    public void setValue(double val){
        this.value = val;
    }
}
