package net.nayrus.noteblockmaster.network.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.setup.Registry;

public record ComposeData(int beat, int subtick, int postDelay, float bpm, int placed) implements CustomPacketPayload {

    public static final Type<ComposeData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "composedata"));

    public static final Codec<ComposeData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("beat").forGetter(ComposeData::beat),
                    Codec.INT.fieldOf("subtick").forGetter(ComposeData::subtick),
                    Codec.INT.fieldOf("delay").forGetter(ComposeData::postDelay),
                    Codec.FLOAT.fieldOf("bpm").forGetter(ComposeData::bpm),
                    Codec.INT.fieldOf("placed").forGetter(ComposeData::placed)
            ).apply(instance, ComposeData::new)

    );

    public static final StreamCodec<FriendlyByteBuf, ComposeData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ComposeData::beat,
            ByteBufCodecs.INT, ComposeData::subtick,
            ByteBufCodecs.INT, ComposeData::postDelay,
            ByteBufCodecs.FLOAT, ComposeData::bpm,
            ByteBufCodecs.INT, ComposeData::placed,
            ComposeData::new
    );

    @Override
    public Type<ComposeData> type() {
        return TYPE;
    }

    public static ComposeData getComposeData(ItemStack stack){
        ComposeData data = stack.get(Registry.COMPOSE_DATA);
        if(data == null) {
            data = new ComposeData(0, 0,1,600, 0);
            stack.set(Registry.COMPOSE_DATA, data);
        }
        return data;
    }

    public boolean hasPlaced(int index){
        if(index > 31 || index < 0) throw new IllegalArgumentException("Note index "+index+" for beat ["+ this.beat() + "] is not allowed");
        return ((this.placed >> index) & 1) == 1;
    }

    public int nextNoteIndex(){
        int index = 0;
        while(hasPlaced(index)) index++;
        return index;
    }
}
