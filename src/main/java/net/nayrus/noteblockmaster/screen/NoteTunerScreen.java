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
import net.nayrus.noteblockmaster.screen.widget.NoteEditBox;
import net.nayrus.noteblockmaster.screen.widget.TunerEditBox;
import net.nayrus.noteblockmaster.screen.widget.ValueSlider;
import net.nayrus.noteblockmaster.utils.Registry;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class NoteTunerScreen extends Screen implements Button.OnPress{

    private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "textures/gui/tunerscreen.png");
    public final int imageWidth = 176;
    public final int imageHeight = 53;
    public TunerEditBox input;
    private Button add;
    private Button set;
    private ValueSlider slider;
    private final ItemStack tuner;
    public int note_val;
    private boolean setmode;

    public NoteTunerScreen(ItemStack item) {
        super(Component.literal(""));
        this.tuner = item;
        TunerData data = TunerItem.getTunerData(item);
        note_val = data.value();
        setmode = data.setmode();
    }

    @Override
    protected void init() {
        super.init();

        input = new NoteEditBox(Minecraft.getInstance().font, getRelX() + 100, getRelY() + 16, 27,20, AdvancedNoteBlock.MAX_NOTE_VAL);

        addRenderableWidget(input);
        setFocused(input);
        input.setFocused(false);
        input.setValue(setmode ? Utils.NOTE_STRING[this.note_val+ AdvancedNoteBlock.MIN_NOTE_VAL] : Integer.toString(note_val));
        input.setResponder(s -> {
            if(s.length() == 3) {
                int _new = AdvancedNoteBlock.noteStringAsInt(s)-AdvancedNoteBlock.MIN_NOTE_VAL;
                this.note_val = _new;
                this.slider.setValue(_new/(double)AdvancedNoteBlock.TOTAL_NOTES);
            }
        });
        input.setFilter(s -> {
            if(s.isEmpty()) return true;
            if(s.length()==1 && Utils.isPartOfNoteString(s)) return true;
            try{
                AdvancedNoteBlock.noteStringAsInt(s);
                return true;
            } catch (IllegalArgumentException e) {
                if(!setmode)
                    try{
                        return Integer.parseInt(s) < AdvancedNoteBlock.TOTAL_NOTES;
                    } catch (NumberFormatException ex) {
                        return false;
                    }
                return false;
            }
        });

        add = new Button.Builder(Component.literal("Add"), this).pos(getRelX() + 135, getRelY() + 8).size(30,17).build();
        set = new Button.Builder(Component.literal("Set"), this).pos(getRelX() + 135, getRelY() + 28).size(30,17).build();

        addRenderableWidget(add);
        addRenderableWidget(set);

        slider = new ValueSlider(getRelX() + 10, getRelY() + 16, 80, 20, note_val /(double)AdvancedNoteBlock.TOTAL_NOTES, val -> {
            this.note_val = (int)(val * AdvancedNoteBlock.TOTAL_NOTES);
            input.setValue(setmode ? Utils.NOTE_STRING[this.note_val+ AdvancedNoteBlock.MIN_NOTE_VAL] : Integer.toString(note_val));
        });
        addRenderableWidget(slider);

        updateButton();
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
        this.input.setValue(setmode ? Utils.NOTE_STRING[this.note_val+ AdvancedNoteBlock.MIN_NOTE_VAL] : Integer.toString(note_val));
    }

    @Override
    public void onClose() {
        TunerData _new = new TunerData(note_val, setmode);
        tuner.set(Registry.TUNER_DATA, _new);
        PacketDistributor.sendToServer(_new);
        super.onClose();
    }

}
