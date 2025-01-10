package net.nayrus.noteblockmaster.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.screen.widget.NoteEditBox;
import net.nayrus.noteblockmaster.screen.widget.ValueSlider;
import net.nayrus.noteblockmaster.utils.Utils;

public class NoteTunerScreen extends ValueTunerScreen implements Button.OnPress{

    public NoteTunerScreen(ItemStack item) {
        super(item, AdvancedNoteBlock.TOTAL_NOTES);
    }

    @Override
    protected void init() {
        super.init();
        int ext = getExtension();
        input = new NoteEditBox(Minecraft.getInstance().font, getRelX() + 100 + ext/2, getRelY() + 16, 27,20, this.maxValue);

        addRenderableWidget(input);
        setFocused(input);
        input.setFocused(false);
        input.setValue(setmode ? Utils.NOTE_STRING[this.value+ AdvancedNoteBlock.MIN_NOTE_VAL] : Integer.toString(value));
        input.setResponder(s -> {
            if(s.length() == 3) {
                int _new = AdvancedNoteBlock.noteStringAsInt(s)-AdvancedNoteBlock.MIN_NOTE_VAL;
                this.value = _new;
                this.slider.setValue(_new/(double)this.maxValue);
            }
        });
        input.setFilter(s -> {
            if(s.isEmpty()) return true;
            if(s.length()==1 && Utils.isPartOfNoteString(s)) return true;
            try{
                AdvancedNoteBlock.noteStringAsInt(s);
                return true;
            } catch (IllegalArgumentException e) {
                if(!setmode)
                    try{
                        return Integer.parseInt(s) < this.maxValue;
                    } catch (NumberFormatException ex) {
                        return false;
                    }
                return false;
            }
        });

        add = new Button.Builder(Component.literal("Add"), this).pos(getRelX() + 135 + ext/2, getRelY() + 8).size(30,17).build();
        set = new Button.Builder(Component.literal("Set"), this).pos(getRelX() + 135 + ext/2, getRelY() + 28).size(30,17).build();

        addRenderableWidget(add);
        addRenderableWidget(set);

        slider = new ValueSlider(getRelX() + 10 - ext/2, getRelY() + 16, 80 + ext, 20, value /(double)this.maxValue, val -> {
            this.value = (int)(val * AdvancedNoteBlock.TOTAL_NOTES);
            input.setValue(setmode ? Utils.NOTE_STRING[this.value+ AdvancedNoteBlock.MIN_NOTE_VAL] : Integer.toString(value));
        });
        addRenderableWidget(slider);

        updateButton();
    }

    @Override
    protected void updateButton(){
        super.updateButton();
        this.input.setValue(setmode ? Utils.NOTE_STRING[this.value+ AdvancedNoteBlock.MIN_NOTE_VAL] : Integer.toString(value));
    }


}
