package net.nayrus.noteblockmaster.composer;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.setup.Registry;

import javax.annotation.Nullable;

public class ComposerContainer extends AbstractContainerMenu {

    private ComposerBlockEntity entity;

    public ComposerContainer(int containerId, Inventory playerInventory) {
        super(Registry.COMPOSER_MENU.get(), containerId);
    }

    public ComposerContainer(int containerId, Inventory playerInventory, ComposerBlockEntity entity){
        this(containerId, playerInventory);
        this.entity = entity;
    }

    public @Nullable ComposerBlockEntity getEntity() {
        return this.entity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

}
