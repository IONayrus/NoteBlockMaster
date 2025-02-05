package net.nayrus.noteblockmaster.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.screen.base.BaseTunerScreen;
import net.nayrus.noteblockmaster.screen.widget.NoteEditBox;
import net.nayrus.noteblockmaster.screen.widget.ValueSlider;
import net.nayrus.noteblockmaster.utils.Utils;

public class NoteTunerScreen extends BaseTunerScreen implements Button.OnPress{

    public NoteTunerScreen(ItemStack item, boolean offhand) {
        super(item, AdvancedNoteBlock.TOTAL_NOTES, offhand);
    }

    @Override
    protected void init() {
        super.init();
        int ext = getExtension();
        input = new NoteEditBox(Minecraft.getInstance().font, getRelX() + 100 + ext/2, getRelY() + 16, 27,20, this.maxValue);

        addRenderableWidget(input);
        setFocused(input);
        input.setFocused(false);
        input.setMaxLength(3);
        input.setValue(setmode ? Utils.NOTE_STRING[this.value+ AdvancedNoteBlock.MIN_NOTE_VAL] : Integer.toString(value));
        input.setResponder(s -> {
            if(!s.isEmpty()) {
                if(!setmode){
                    int _new = Integer.parseInt(s);
                    this.value = _new;
                    this.slider.setValue((_new) / (double) this.maxValue);
                }else{
                    try{
                        int _new = AdvancedNoteBlock.noteStringAsInt(s) - AdvancedNoteBlock.MIN_NOTE_VAL;
                        this.value = _new;
                        this.slider.setValue((_new) / (double) this.maxValue);
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        });
        input.setFilter(s -> {
            try{
                if(s.isEmpty()) return true;
                Integer.parseInt(s);
                return true;
            } catch (NumberFormatException e) {
                return Utils.isPartOfNoteString(s) && setmode;
            }
        });

        add = new Button.Builder(Component.literal("Add"), this).pos(getRelX() + 135 + ext/2, getRelY() + 8).size(30,17).build();
        set = new Button.Builder(Component.literal("Set"), this).pos(getRelX() + 135 + ext/2, getRelY() + 28).size(30,17).build();

        addRenderableWidget(add);
        addRenderableWidget(set);

        slider = new ValueSlider(getRelX() + 10 - ext/2, getRelY() + 16, 80 + ext, 20, value /(double)this.maxValue, val -> {
            this.value = (int)(val * AdvancedNoteBlock.TOTAL_NOTES);
            input.setValue(setmode ? Utils.NOTE_STRING[this.value + AdvancedNoteBlock.MIN_NOTE_VAL] : Integer.toString(value));
        });
        addRenderableWidget(slider);

        updateButton();
    }

    @Override
    protected void updateButton(){
        super.updateButton();
        this.input.setValue(setmode ? Utils.NOTE_STRING[this.value+ AdvancedNoteBlock.MIN_NOTE_VAL] : Integer.toString(value));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Options op = Minecraft.getInstance().options;
        if((op.keyUp.matches(keyCode, scanCode)
                || op.keyDown.matches(keyCode, scanCode)
                || op.keyRight.matches(keyCode, scanCode)
                || op.keyLeft.matches(keyCode, scanCode)
                || op.keyJump.matches(keyCode, scanCode))
                && !(Utils.isIntInRange(keyCode, 'A', 'G') && setmode))
            this.onClose();
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(this.slider.isMouseOver(mouseX, mouseY) || this.input.isMouseOver(mouseX, mouseY))
            changeValue((int)(this.value + scrollY * getScrollFactor(false).floatValue()));
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public void changeValue(int new_val) {
        this.value = Math.min(Math.max(0, new_val), this.maxValue);
        this.input.setValue(setmode ? Utils.NOTE_STRING[this.value + AdvancedNoteBlock.MIN_NOTE_VAL] : Integer.toString(value));
        this.slider.setValue(this.value / (double) this.maxValue);
    }
}
