package net.nayrus.noteblockmaster.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.NoteBlockMaster;

public record ActionPing(byte action) implements CustomPacketPayload {

    public static final Type<ActionPing> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "actionping"));

    public enum Action {SAVE_STARTUP_CONFIG}

    public static byte toByte(Action action){
        return (byte) action.ordinal();
    }

    @Override
    public Type<ActionPing> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, ActionPing> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, ActionPing::action,
            ActionPing::new
    );
}
