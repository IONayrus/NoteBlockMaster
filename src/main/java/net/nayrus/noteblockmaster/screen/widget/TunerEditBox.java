package net.nayrus.noteblockmaster.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;

public class TunerEditBox extends EditBox {

    protected boolean first = true;
    protected final int maxVal;

    public TunerEditBox(Font font, int x, int y, int width, int height, int maxVal) {
        super(font, x, y, width, height, Component.literal("0"));
        this.maxVal = maxVal;
        this.setEditable(true);
        this.setMaxLength(3);
        this.setCursorPosition(0);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (StringUtil.isAllowedChatCharacter(codePoint)) {
            if(first){
                this.setValue("");
                first = false;
            }
            this.insertText(Character.toString(codePoint));
            if(getNumber() >= this.maxVal)
                this.setValue(Integer.toString(this.maxVal));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public int getNumber(){
        if(this.getValue().isEmpty()) return 0;
        return Integer.parseInt(this.getValue());
    }
}
