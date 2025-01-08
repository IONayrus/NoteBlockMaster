package net.nayrus.noteblockmaster.screen.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public class ValueSlider extends AbstractSliderButton {

    public ValueSlider(int x, int y, int width, int height, double value) {
        super(x, y, width, height, Component.literal(""), value);
    }

    @Override
    protected void updateMessage() {

    }

    @Override
    protected void applyValue() {

    }
}
