package net.nayrus.noteblockmaster.network.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.setup.Registry;
import org.jetbrains.annotations.NotNull;

public record ComposeData(int beat, int subtick, int preDelay, float bpm) {

    public static final Codec<ComposeData> TUNER_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("beat").forGetter(ComposeData::beat),
                    Codec.INT.fieldOf("subtick").forGetter(ComposeData::subtick),
                    Codec.INT.fieldOf("repeater").forGetter(ComposeData::preDelay),
                    Codec.FLOAT.fieldOf("bpm").forGetter(ComposeData::bpm)
            ).apply(instance, ComposeData::new)

    );

    public static @NotNull ComposeData getComposeData(ItemStack stack){
        ComposeData data = stack.get(Registry.COMPOSE_DATA);
        if(data == null) {
            data = new ComposeData(0, 0,1,600);
            stack.set(Registry.COMPOSE_DATA, data);
        }
        return data;
    }
}
