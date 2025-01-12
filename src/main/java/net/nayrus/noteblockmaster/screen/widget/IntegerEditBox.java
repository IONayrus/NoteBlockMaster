package net.nayrus.noteblockmaster.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;

public class IntegerEditBox extends EditBox {

    protected boolean first;
    private final int maxVal;

    public IntegerEditBox(Font font, int x, int y, int width, int height, int maxVal, boolean resetValueOnFirstInteraction) {
        super(font, x, y, width, height, Component.literal("0"));
        this.maxVal = maxVal;
        this.setCursorPosition(0);
        this.setFilter(s -> {
            try{
                if(s.isEmpty()) return true;
                Integer.parseInt(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
        this.first = resetValueOnFirstInteraction;
    }

    public IntegerEditBox(Font font, int x, int y, int width, int height, int maxVal) {
        this(font, x, y, width, height, maxVal,false);
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

    public int getNumber(){
        if(this.getValue().isEmpty()) return 0;
        return Integer.parseInt(this.getValue());
    }

    public int getMaxVal() {
        return this.maxVal;
    }
}
