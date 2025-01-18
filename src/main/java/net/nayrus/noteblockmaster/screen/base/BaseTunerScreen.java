package net.nayrus.noteblockmaster.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.item.TunerItem;
import net.nayrus.noteblockmaster.network.data.TunerData;
import net.nayrus.noteblockmaster.screen.widget.IntegerEditBox;
import net.nayrus.noteblockmaster.screen.widget.ValueSlider;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.neoforge.network.PacketDistributor;

public class BaseTunerScreen extends BaseScreen implements Button.OnPress{

    protected IntegerEditBox input;
    protected Button add;
    protected Button set;
    protected ValueSlider slider;
    protected final ItemStack tuner;
    public int value;
    protected boolean setmode;
    protected final int maxValue;

    protected BaseTunerScreen(ItemStack item, int maxValue) {
        super(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "textures/gui/tunerscreen.png"), 176, 53);
        this.tuner = item;
        this.maxValue = maxValue;
        TunerData data = TunerItem.getTunerData(item);
        value = data.value();
        setmode = data.setmode();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderTexture(0, GUI);
        int extend = getExtension();
        guiGraphics.blit(GUI, getRelX() - extend/2, getRelY(), 0, 0, this.guiWidth /2, this.guiHeight);
        guiGraphics.blit(GUI, getRelX() + this.guiWidth /2 - extend/2, getRelY(), 5, 0, extend > (this.guiWidth - 5) ? extend/2 : extend, this.guiHeight);
        if(extend > this.guiWidth - 5)
            guiGraphics.blit(GUI, getRelX() + this.guiWidth /2, getRelY(), 5, 0, extend/2, this.guiHeight);
        guiGraphics.blit(GUI, getRelX() + this.guiWidth /2 + extend/2, getRelY(), this.guiWidth /2, 0, this.guiWidth /2, this.guiHeight);
    }

    public int getExtension(){
        return Math.max(0, this.maxValue - 20) * 4;
    }

    @Override
    public void onPress(Button button) {
        button.setFocused(false);
        setmode = !setmode;
        updateButton();
    }

    protected void updateButton(){
        add.active = setmode;
        set.active = !setmode;
    }

    @Override
    public void onClose() {
        TunerData _new = new TunerData(value, setmode);
        tuner.set(Registry.TUNER_DATA, _new);
        PacketDistributor.sendToServer(_new);
        super.onClose();
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        int extend2 = getExtension() / 2;
        if(!Utils.isIntInRange((int)mouseX, getRelX() - extend2, getRelX() + this.guiWidth + extend2)
                || !Utils.isIntInRange((int)mouseY, getRelY(), getRelY() + this.guiHeight))
            onClose();
    }
}
