package net.nayrus.noteblockmaster.block.composer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.setup.Registry;
import org.jetbrains.annotations.Nullable;

public class ComposerBlockEntity extends BaseContainerBlockEntity {

    private ItemStack item = ItemStack.EMPTY;

    public ComposerBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registry.COMPOSER_BE.get(), pos, blockState);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.noteblockmaster.composer");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return NonNullList.of(item);
    }

    public ItemStack getItem(){
        return this.item;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.item = items.getFirst();
        this.setChanged();
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new ComposerContainer(containerId, inventory);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        if(!this.item.isEmpty()) tag.put("item", this.item.save(registries));
        saveAdditional(tag, registries);
        return tag;
    }
}
