package net.nayrus.noteblockmaster.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class TunerEditBox extends EditBox {

    public TunerEditBox(Font font, int x, int y, int width, int height) {
        super(font, x, y, width, height, Component.literal(""));
        this.setEditable(true);
        this.setMaxLength(3);
        this.setFilter(s -> {
            try{
                if(s.isEmpty()) return true;
                Integer.parseInt(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }});
        this.setCursorPosition(0);
    }

    @Override
    public boolean canConsumeInput() {
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
