package net.nayrus.noteblockmaster.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.item.TunerItem;
import net.nayrus.noteblockmaster.network.payload.TunerData;
import net.nayrus.noteblockmaster.screen.widget.TunerEditBox;
import net.nayrus.noteblockmaster.screen.widget.ValueSlider;
import net.nayrus.noteblockmaster.utils.Registry;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class ValueTunerScreen extends Screen implements Button.OnPress{
    protected final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "textures/gui/tunerscreen.png");
    public final int imageWidth = 176;
    public final int imageHeight = 53;
    protected TunerEditBox input;
    protected Button add;
    protected Button set;
    protected ValueSlider slider;
    protected final ItemStack tuner;
    public int value;
    protected boolean setmode;
    protected final int maxValue;

    public ValueTunerScreen(ItemStack item, int maxValue) {
        super(Component.literal(""));
        this.tuner = item;
        this.maxValue = maxValue;
        TunerData data = TunerItem.getTunerData(item);
        value = data.value();
        setmode = data.setmode();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderTexture(0, GUI);
        int extend = getExtension();
        guiGraphics.blit(GUI, getRelX() - extend/2, getRelY(), 0, 0, this.imageWidth/2, this.imageHeight);
        guiGraphics.blit(GUI, getRelX() + this.imageWidth/2 - extend/2, getRelY(), 5, 0,
                extend > this.imageWidth ? extend/2 : extend, this.imageHeight);
        if(extend > this.imageWidth)
            guiGraphics.blit(GUI, getRelX() + this.imageWidth/2, getRelY(), 5, 0,
                    extend/2, this.imageHeight);
        guiGraphics.blit(GUI, getRelX() + this.imageWidth/2 + extend/2, getRelY(), this.imageWidth/2, 0, this.imageWidth/2, this.imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    public int getRelX(){
        return (this.width - this.imageWidth) / 2;
    }

    public int getRelY(){
        return (this.height - this.imageHeight) / 2;
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
}
