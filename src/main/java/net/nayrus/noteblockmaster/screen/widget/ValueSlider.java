package net.nayrus.noteblockmaster.screen.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ValueSlider extends AbstractSliderButton {

    private final Consumer<Double> callback;

    public ValueSlider(int x, int y, int width, int height, double value, Consumer<Double> callback) {
        super(x, y, width, height, Component.literal(""), value);
        this.callback = callback;
    }

    @Override
    protected void updateMessage() {}

    @Override
    protected void applyValue() {
        callback.accept(this.value);
    }

    public void setValue(double val){
        this.value = val;
    }
}
