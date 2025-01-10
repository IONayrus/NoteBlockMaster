package net.nayrus.noteblockmaster.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

public record ActionPing(byte action) implements CustomPacketPayload {

    public static final Type<ActionPing> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "actionping"));

    public enum Action {SAVE_STARTUP_CONFIG, GOLD_BREAK, SWING_OFFHAND}

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

    public static void sendActionPing(ServerPlayer player, Action action){
        PacketDistributor.sendToPlayer(Objects.requireNonNull(player), new ActionPing(ActionPing.toByte(action)));
    }
}
