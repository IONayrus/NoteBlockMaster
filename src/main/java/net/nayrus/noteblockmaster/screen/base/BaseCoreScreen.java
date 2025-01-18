package net.nayrus.noteblockmaster.screen.base;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.block.TuningCore;
import net.nayrus.noteblockmaster.screen.widget.IntegerEditBox;
import net.nayrus.noteblockmaster.screen.widget.ValueSlider;

public class BaseCoreScreen extends BaseScreen{

    protected IntegerEditBox volBox;
    protected IntegerEditBox sustainBox;
    protected ValueSlider volSlider;
    protected ValueSlider sustainSlider;
    protected int volume = -1;
    protected int sustain = -1;

    protected BaseCoreScreen(BlockState state) {
        super(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "textures/gui/tunerscreen.png"), 176, 53);
        if(TuningCore.isMuffling(state)) this.volume = TuningCore.getVolume(state);
        if(TuningCore.isSustaining(state)) this.sustain = TuningCore.getSustain(state);
    }

}
