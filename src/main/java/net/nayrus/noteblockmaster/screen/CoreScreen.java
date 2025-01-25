package net.nayrus.noteblockmaster.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.screen.base.BaseCoreScreen;
import net.nayrus.noteblockmaster.screen.widget.IntegerEditBox;
import net.nayrus.noteblockmaster.screen.widget.ValueSlider;
import net.nayrus.noteblockmaster.sound.AdvancedInstrument;

import java.awt.*;
import java.util.Locale;

public class CoreScreen extends BaseCoreScreen {

    protected final AdvancedInstrument instrument;
    protected final int maxSustain;
    protected final float scale;

    public CoreScreen(BlockState state, BlockPos pos, AdvancedInstrument instrument, float pitch) {
        super(state, pos);
        this.instrument = instrument;
        this.maxSustain = instrument.getSustains();
        this.scale = 1/pitch;
    }

    private static final int COMP_HEIGHT = 19;
    @Override
    protected void init() {
        super.init();

        boolean isMixing = this.volume != -1;
        boolean isSustaining = this.sustain != -1 && maxSustain > 1;

        this.volBox = new IntegerEditBox(this.font, getRelX() + 138, getRelY() + 6, 27, COMP_HEIGHT, 100, true);
        this.volBox.setEditable(isMixing);
        this.volBox.setValue(isMixing ? Integer.toString(this.volume * 5) : "100");
        this.volBox.setMaxLength(3);
        this.volBox.setResponder(s -> {
            if(!s.isEmpty()) {
                int _new = Integer.parseInt(s) / 5;
                if(_new < 1) _new = 1;
                this.volume = _new;
                this.volSlider.setValue((_new - 1)/ 19.0);
            }
        });

        this.sustainBox = new IntegerEditBox(this.font, getRelX() + 138, getRelY() + 28, 27, COMP_HEIGHT, maxSustain,true);
        this.sustainBox.setEditable(isSustaining);
        this.sustainBox.setValue(isSustaining ? Integer.toString(this.sustain) : Integer.toString(maxSustain));
        this.sustainBox.setMaxLength(3);
        this.sustainBox.setResponder(s -> {
            if(!s.isEmpty()) {
                int _new = Integer.parseInt(s);
                if(_new < 1) _new = 1;
                this.sustain = _new;
                this.sustainSlider.setValue((_new - 1)/ (maxSustain > 1 ? (maxSustain - 1.0F) : 1.0F));
                this.sustainSlider.setTooltip(getTooltip());
            }
        });

        if(!(isMixing && isSustaining)){
            setFocused(isMixing ? this.volBox : this.sustainBox);
        }
        this.volBox.setFocused(false);
        this.sustainBox.setFocused(false);

        this.volSlider = new ValueSlider(getRelX() + 10, getRelY() + 6, 118, COMP_HEIGHT,
                isMixing ? (this.volume - 1) / 19.0 : 1.0, !isMixing ? null : (val)->{
            this.volume = (int) (val * 19 + 1);
            this.volBox.setValue(Integer.toString(this.volume * 5));
        });
        this.volSlider.active = isMixing;
        this.volSlider.setMessage(Component.literal("Volume"));

        this.sustainSlider = new ValueSlider(getRelX() + 10, getRelY() + 28, 118, COMP_HEIGHT,
                isSustaining ? (this.sustain - 1) / (maxSustain - 1.0) : 1.0, !isSustaining ? null : (val) -> {
            this.sustain = (int) (val * (maxSustain - 1) + 1);
            this.sustainBox.setValue(Integer.toString(this.sustain));
        });
        this.sustainSlider.active = isSustaining;
        this.sustainSlider.setMessage(Component.literal("Sustain"));
        this.sustainSlider.setTooltip(getTooltip());

        addRenderableWidget(this.volBox);
        addRenderableWidget(this.volSlider);
        addRenderableWidget(this.sustainBox);
        addRenderableWidget(this.sustainSlider);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Options op = Minecraft.getInstance().options;
        if(op.keyUp.matches(keyCode, scanCode)
                || op.keyDown.matches(keyCode, scanCode)
                || op.keyRight.matches(keyCode, scanCode)
                || op.keyLeft.matches(keyCode, scanCode)
                || op.keyShift.matches(keyCode, scanCode)
                || op.keyJump.matches(keyCode, scanCode))
            this.onClose();
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public Tooltip getTooltip(){
        return Tooltip.create(Component.literal(String.format(Locale.US,"%.2f",
                this.instrument.getSustainTime(this.sustain != -1 ? this.sustain : 1) * this.scale) + " ms").withColor(Color.LIGHT_GRAY.getRGB()));
    }
}
