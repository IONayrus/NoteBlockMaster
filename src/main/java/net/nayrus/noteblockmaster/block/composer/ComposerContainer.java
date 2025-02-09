package net.nayrus.noteblockmaster.block.composer;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.setup.Registry;

public class ComposerContainer extends AbstractContainerMenu {

    public ComposerContainer(int containerId, Inventory playerInventory) {
        super(Registry.COMPOSER_MENU.get(), containerId);
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
