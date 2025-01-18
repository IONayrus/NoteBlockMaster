package net.nayrus.noteblockmaster.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.screen.base.BaseCoreScreen;
import net.nayrus.noteblockmaster.screen.widget.IntegerEditBox;
import net.nayrus.noteblockmaster.screen.widget.ValueSlider;

public class CoreScreen extends BaseCoreScreen {

    public CoreScreen(BlockState state, BlockPos pos) {
        super(state);
    }

    @Override
    protected void init() {
        super.init();

        Font font = Minecraft.getInstance().font;
        boolean isMuffling = this.volume != -1;
        boolean isSustaining = this.sustain != -1;

        this.volBox = new IntegerEditBox(font, getRelX() + 100, getRelY() + 5, 27, 20, 19, true);
        this.volBox.setEditable(isMuffling);
        this.volBox.setValue(isMuffling ? Integer.toString(this.volume * 5) : "100");
        this.volBox.setMaxLength(2);

        this.sustainBox = new IntegerEditBox(font, getRelX() + 100, getRelY() + 30, 27, 20, 200,true);
        this.sustainBox.setEditable(isSustaining);
        this.sustainBox.setValue(isSustaining ? Integer.toString(this.sustain) : "10");
        this.sustainBox.setMaxLength(3);

        if(!(isMuffling && isSustaining)){
            setFocused(isMuffling ? this.volBox : this.sustainBox);
        }
        this.volBox.setFocused(false);
        this.sustainBox.setFocused(false);

        this.volSlider = new ValueSlider(getRelX() + 10, getRelY() + 5, 80, 20, isMuffling ? (this.volume - 1) / 18.0 : 1.0, !isMuffling ? null : (val)->{
            this.volume = (int) (val * 18 + 1);
            this.volBox.setValue(Integer.toString(this.volume * 5));
        });
        this.volSlider.active = isMuffling;
        this.volSlider.setTooltip(Tooltip.create(Component.literal("Volume")));

        this.sustainSlider = new ValueSlider(getRelX() + 10, getRelY() + 30, 80, 20, isSustaining ? this.sustain / 200.0 : 1.0, !isSustaining ? null : (val)->{
            this.sustain = (int) (val * 199 + 1);
            this.sustainBox.setValue(Integer.toString(this.sustain));
        });
        this.sustainSlider.active = isSustaining;
        this.sustainSlider.setTooltip(Tooltip.create(Component.literal("Sustain")));

        addRenderableWidget(this.volBox);
        addRenderableWidget(this.volSlider);
        addRenderableWidget(this.sustainBox);
        addRenderableWidget(this.sustainSlider);
    }
}
