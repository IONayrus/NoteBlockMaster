package net.nayrus.noteblockmaster.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.item.TunerItem;
import net.nayrus.noteblockmaster.network.payload.TunerData;
import net.nayrus.noteblockmaster.screen.widget.TunerEditBox;
import net.nayrus.noteblockmaster.screen.widget.ValueSlider;
import net.nayrus.noteblockmaster.utils.Registry;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class TempoTunerScreen extends Screen implements Button.OnPress {

    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "textures/gui/tunerscreen.png");
    public final int imageWidth = 176;
    public final int imageHeight = 53;
    public TunerEditBox input;
    private Button add;
    private Button set;
    private ValueSlider slider;
    private final ItemStack tuner;
    public int subtick_val;
    private boolean setmode;

    public TempoTunerScreen(ItemStack item) {
        super(Component.literal(""));
        this.tuner = item;
        TunerData data = TunerItem.getTunerData(item);
        subtick_val = data.value();
        setmode = data.setmode();
    }

    @Override
    protected void init() {
        super.init();

        input = new TunerEditBox(Minecraft.getInstance().font, getRelX() + 100, getRelY() + 16, 27,20, AdvancedNoteBlock.SUBTICKS);

        addRenderableWidget(input);
        setFocused(input);
        input.setFocused(false);
        input.setValue(Integer.toString(subtick_val));
        input.setResponder(s -> {
            if(!s.isEmpty()) {
                int _new = Integer.parseInt(s);
                this.subtick_val = _new;
                this.slider.setValue(_new/(AdvancedNoteBlock.SUBTICKS - 1.0));
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

        slider = new ValueSlider(getRelX() + 10, getRelY() + 16, 80, 20, subtick_val/(AdvancedNoteBlock.SUBTICKS - 1.0), val -> {
            this.subtick_val = (int)(val * (AdvancedNoteBlock.SUBTICKS -1));
            input.setValue(Integer.toString(this.subtick_val));
        });
        addRenderableWidget(slider);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderTexture(0, GUI);
        guiGraphics.blit(GUI, getRelX(), getRelY(), 0, 0, this.imageWidth, this.imageHeight);
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

    @Override
    public void onPress(Button button) {
        button.setFocused(false);
        setmode = !setmode;
        updateButton();
    }

    private void updateButton(){
        add.active = setmode;
        set.active = !setmode;
    }

    @Override
    public void onClose() {
        TunerData _new = new TunerData(subtick_val, setmode);
        tuner.set(Registry.TUNER_DATA, _new);
        PacketDistributor.sendToServer(_new);
        super.onClose();
    }

}
