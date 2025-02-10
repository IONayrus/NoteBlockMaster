package net.nayrus.noteblockmaster.block.composer;

import libs.felnull.fnnbs.NBS;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.nayrus.noteblockmaster.network.data.SongData;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class ComposerScreen extends AbstractContainerScreen<ComposerContainer> {

    protected List<MelodicLayer> layers = new ArrayList<>();

    public ComposerScreen(ComposerContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

    }

    @Override
    public void onClose() {
        NBS nbs = ComposerBlock.loadNBSFile("FDSpaced");
        if(nbs!= null){
            SongData data = SongData.of(nbs);
            PacketDistributor.sendToServer(data);
        }
        super.onClose();
    }
}
