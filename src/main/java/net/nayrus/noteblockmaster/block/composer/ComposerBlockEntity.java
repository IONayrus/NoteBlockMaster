package net.nayrus.noteblockmaster.block.composer;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.setup.Registry;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public class ComposerBlockEntity extends BaseContainerBlockEntity {

    private ItemStack item = ItemStack.EMPTY;
    private float rotation = 0.0F;

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

    public void setItem(ItemStack stack){
        this.setItems(NonNullList.of(stack, stack));
    }

    public void clearItem() {
        this.setItem(ItemStack.EMPTY);
        this.setRotation(0);
    }


    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.item = items.getFirst();
        this.setChanged();
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new ComposerContainer(containerId, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public static void handleClientItemUpdate(final ClientItemUpdate packet, final IPayloadContext context){
        Player player = context.player();
        ClientLevel level = (ClientLevel) player.level();
        if(!(level.getBlockEntity(packet.pos()) instanceof ComposerBlockEntity BE)) return;
        if(packet.item().isEmpty()) {
            BE.clearItem();
            player.level().playSound(player, packet.pos(), SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS);
        }
        else {
            BE.setItem(packet.item().get());
            if(packet.rotation().isPresent()) BE.setRotation(packet.rotation().get());
            player.level().playSound(player, packet.pos(), SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS);
        }
    }

    public record ClientItemUpdate(BlockPos pos, Optional<ItemStack> item, Optional<Float> rotation) implements CustomPacketPayload {

        public ClientItemUpdate(BlockPos pos){
            this(pos, Optional.empty(), Optional.empty());
        }

        public static final Type<ClientItemUpdate> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "itempayload"));

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientItemUpdate> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, ClientItemUpdate::pos,
                ItemStack.STREAM_CODEC.apply(ByteBufCodecs::optional), ClientItemUpdate::item,
                ByteBufCodecs.FLOAT.apply(ByteBufCodecs::optional), ClientItemUpdate::rotation,
                ClientItemUpdate::new
        );

        @Override
        public Type<ClientItemUpdate> type() {
            return TYPE;
        }
    }
}
