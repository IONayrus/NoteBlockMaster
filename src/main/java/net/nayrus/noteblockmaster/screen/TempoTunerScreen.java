package net.nayrus.noteblockmaster.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.network.data.ComposeData;
import net.nayrus.noteblockmaster.screen.base.BaseTunerScreen;
import net.nayrus.noteblockmaster.screen.widget.IntegerEditBox;
import net.nayrus.noteblockmaster.screen.widget.ValueSlider;
import net.nayrus.noteblockmaster.setup.Registry;

public class TempoTunerScreen extends BaseTunerScreen implements Button.OnPress {

    private boolean disableButtons;

    public TempoTunerScreen(ItemStack item, ItemStack composer, boolean offhand) {
        super(item, AdvancedNoteBlock.SUBTICKS, offhand);
        if(!composer.is(Registry.COMPOSER)) return;
        this.setmode = true;
        this.value = ComposeData.getComposeData(composer).subtick();
        this.disableButtons = true;
    }

    @Override
    protected void init() {
        super.init();

        input = new IntegerEditBox(this.font, getRelX() + 100, getRelY() + 16, 27,20, this.maxValue, true);

        addRenderableWidget(input);
        setFocused(input);
        input.setEditable(!this.disableButtons);
        input.setFocused(false);
        input.setMaxLength(3);
        input.setValue(Integer.toString(this.value));
        input.setResponder(s -> {
            if(!s.isEmpty()) {
                int _new = Integer.parseInt(s);
                this.value = _new;
                this.slider.setValue(_new/(this.maxValue - 1.0));
            }
        });

        add = new Button.Builder(Component.literal("Add"), this).pos(getRelX() + 135, getRelY() + 8).size(30,17).build();
        set = new Button.Builder(Component.literal("Set"), this).pos(getRelX() + 135, getRelY() + 28).size(30,17).build();

        updateButton();

        addRenderableWidget(add);
        addRenderableWidget(set);

        slider = new ValueSlider(getRelX() + 10, getRelY() + 16, 80, 20, this.value/(this.maxValue - 1.0), val -> {
            this.value = (int)(val * (this.maxValue - 1));
            input.setValue(Integer.toString(this.value));
        });
        slider.active = !this.disableButtons;
        addRenderableWidget(slider);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Options op = Minecraft.getInstance().options;
        if(op.keyUp.matches(keyCode, scanCode)
                || op.keyDown.matches(keyCode, scanCode)
                || op.keyRight.matches(keyCode, scanCode)
                || op.keyLeft.matches(keyCode, scanCode)
                || op.keyJump.matches(keyCode, scanCode))
            this.onClose();
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onPress(Button button) {
        if(!disableButtons) super.onPress(button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(this.slider.isMouseOver(mouseX, mouseY) || this.input.isMouseOver(mouseX, mouseY))
            changeValue((int)(this.value + scrollY * getScrollFactor(false).floatValue()));
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public void changeValue(int new_val) {
        this.value = Math.min(Math.max(0, new_val), this.maxValue - 1);
        this.input.setValue(Integer.toString(this.value));
        this.slider.setValue(this.value / (this.maxValue - 1.0));
    }}
