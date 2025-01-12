package net.nayrus.noteblockmaster.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;

public class FloatEditBox extends EditBox {

    protected boolean first;
    private final float maxVal;

    public FloatEditBox(Font font, int x, int y, int width, int height, float maxVal, boolean resetValueOnFirstInteraction) {
        super(font, x, y, width, height, Component.literal("1.0"));
        this.maxVal = maxVal;
        this.setCursorPosition(0);
        this.setFilter(s -> {
            try{
                if(s.isEmpty()) return true;
                Float.parseFloat(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
        this.first = resetValueOnFirstInteraction;
    }

    public FloatEditBox(Font font, int x, int y, int width, int height, float maxVal) {
        this(font, x, y, width, height, maxVal, false);
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
                this.setValue(Float.toString(this.maxVal));
            return true;
        } else {
            return false;
        }
    }

    public float getNumber(){
        String input = this.getValue();
        if(input.isEmpty()) return 0;
        return Float.parseFloat(input);
    }

    public float getMaxVal(){
        return this.maxVal;
    }
}
