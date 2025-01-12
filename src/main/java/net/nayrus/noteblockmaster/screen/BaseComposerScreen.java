package net.nayrus.noteblockmaster.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.item.ComposersNote;
import net.nayrus.noteblockmaster.network.data.ComposeData;
import net.nayrus.noteblockmaster.screen.widget.FloatEditBox;
import net.nayrus.noteblockmaster.screen.widget.IntegerEditBox;
import net.nayrus.noteblockmaster.setup.Registry;
import net.neoforged.neoforge.network.PacketDistributor;

public class BaseComposerScreen extends BaseScreen implements Button.OnPress{

    protected IntegerEditBox beat;
    protected FloatEditBox bpm;
    protected Button decrease;
    protected final ItemStack composer;
    public float bpm_val;
    public int beat_val;

    protected BaseComposerScreen(ItemStack composer) {
        super(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "textures/gui/tunerscreen.png"), 176, 53);
        this.composer = composer;
        ComposeData data = ComposeData.getComposeData(composer);
        bpm_val = data.bpm();
        beat_val = data.beat();
    }

    @Override
    public void onPress(Button button) {
        if(button.equals(decrease)) changeBeatVal(this.beat_val - 1);
    }

    @Override
    public void onClose() {
        Tuple<Integer, Integer> calc = ComposersNote.subtickAndPauseOnBeat(this.beat_val, this.bpm_val);
        ComposeData _new = new ComposeData(this.beat_val, calc.getA(), calc.getB(), this.bpm_val);
        composer.set(Registry.COMPOSE_DATA, _new);
        PacketDistributor.sendToServer(_new);
        super.onClose();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(this.bpm.isMouseOver(mouseX, mouseY))
            changeBPMVal(this.bpm_val + (float) scrollY);
        if(this.beat.isMouseOver(mouseX, mouseY))
            changeBeatVal((int)(this.beat_val + scrollY));
        else if(this.decrease.isMouseOver(mouseX, mouseY)) {
            changeBeatVal((int)(this.beat_val + scrollY));
            this.decrease.playDownSound(Minecraft.getInstance().getSoundManager());
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public void changeBeatVal(int new_val){
        this.beat_val = Math.min(Math.max(0, new_val), this.beat.getMaxVal());
        this.beat.setValue(Integer.toString(this.beat_val));
    }

    public void changeBPMVal(float new_val){
        this.bpm_val = Math.min(Math.max(0, new_val), this.bpm.getMaxVal());
        this.bpm.setValue(Float.toString(this.bpm_val));
    }
}
