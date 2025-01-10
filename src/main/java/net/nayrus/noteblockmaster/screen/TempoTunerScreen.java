package net.nayrus.noteblockmaster.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.screen.widget.TunerEditBox;
import net.nayrus.noteblockmaster.screen.widget.ValueSlider;

public class TempoTunerScreen extends ValueTunerScreen implements Button.OnPress {
    
    public TempoTunerScreen(ItemStack item) {
        super(item, AdvancedNoteBlock.SUBTICKS);
    }

    @Override
    protected void init() {
        super.init();

        input = new TunerEditBox(Minecraft.getInstance().font, getRelX() + 100, getRelY() + 16, 27,20, this.maxValue);

        addRenderableWidget(input);
        setFocused(input);
        input.setFocused(false);
        input.setValue(Integer.toString(this.value));
        input.setResponder(s -> {
            if(!s.isEmpty()) {
                int _new = Integer.parseInt(s);
                this.value = _new;
                this.slider.setValue(_new/(this.maxValue - 1.0));
            }
        });
        input.setFilter(s -> {
            try{
                if(s.isEmpty()) return true;
                Integer.parseInt(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
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
        addRenderableWidget(slider);
    }

}
