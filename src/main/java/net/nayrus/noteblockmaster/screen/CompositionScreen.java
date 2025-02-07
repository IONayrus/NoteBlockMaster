package net.nayrus.noteblockmaster.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.screen.base.BaseCompositionScreen;
import net.nayrus.noteblockmaster.screen.widget.FloatEditBox;
import net.nayrus.noteblockmaster.screen.widget.IntegerEditBox;

public class CompositionScreen extends BaseCompositionScreen {

    public CompositionScreen(ItemStack composer) {
        super(composer);
    }

    @Override
    protected void init() {
        super.init();
        int lowerRow = 22;

        this.beat = new IntegerEditBox(Minecraft.getInstance().font, getRelX() + 10 , getRelY() + lowerRow, 65,20, 99999999);
        this.beat.setMaxLength(9);
        this.beat.setValue(Integer.toString(this.beat_val));
        this.beat.setResponder(s -> {
            if(!s.isEmpty()) this.beat_val = Integer.parseInt(s);
        });

        this.decrease = new Button.Builder(Component.literal("<-"), this).pos(getRelX() + guiWidth/2 - 10, getRelY() + lowerRow).size(19,20).build();

        this.bpm = new FloatEditBox(Minecraft.getInstance().font, getRelX() + 100 , getRelY() + lowerRow, 65,20, 12000.0F);
        this.bpm.setMaxLength(9);
        this.bpm.setValue(Float.toString(this.bpm_val));
        this.bpm.setResponder(s -> {
            if(!s.isEmpty()) this.bpm_val = Float.parseFloat(s);
        });

        addRenderableWidget(this.beat);
        addRenderableWidget(this.bpm);
        addRenderableWidget(this.decrease);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, Component.literal("Beat"), getRelX() + 11, getRelY() + 10, 0,false);
        guiGraphics.drawString(font, Component.literal("BPM"), getRelX() + 101, getRelY() + 10, 0,false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Options op = Minecraft.getInstance().options;
        if(op.keyUp.matches(keyCode, scanCode)
                || op.keyDown.matches(keyCode, scanCode)
                || op.keyRight.matches(keyCode, scanCode)
                || op.keyLeft.matches(keyCode, scanCode)
                || op.keyJump.matches(keyCode, scanCode)
                || keyCode == 257)
            this.onClose();
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
