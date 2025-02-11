package net.nayrus.noteblockmaster.composer;

import libs.felnull.fnnbs.NBS;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.nayrus.noteblockmaster.network.data.SongID;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

public class ComposerScreen extends AbstractContainerScreen<ComposerContainer> {

    public ComposerScreen(ComposerContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

    }

    @Override
    public void onClose() {
        NBS nbs = ComposerBlock.loadNBSFile("FreedomDive");
        if(nbs!= null){
            SongData data = SongData.of(nbs);
            UUID ID = data.getID();
            SongCache.cacheSong(ID, data);
            PacketDistributor.sendToServer(new SongID(ID));
        }
        super.onClose();
    }
}
