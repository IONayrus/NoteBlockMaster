package net.nayrus.noteblockmaster.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.util.StringUtil;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.utils.Utils;

public class NoteEditBox extends TunerEditBox{

    public NoteEditBox(Font font, int x, int y, int width, int height, int maxVal) {
        super(font, x, y, width, height, maxVal);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (StringUtil.isAllowedChatCharacter(codePoint)) {
            if(first){
                this.setValue("");
                first = false;
            }
            this.insertText(Character.toString(codePoint));
            try {
                if (AdvancedNoteBlock.noteStringAsInt(this.getValue(),false) >= this.maxVal)
                    this.setValue(Utils.NOTE_STRING[AdvancedNoteBlock.MAX_NOTE_VAL]);
            }catch(IllegalArgumentException ignored){}
            return true;
        } else {
            return false;
        }
    }
}