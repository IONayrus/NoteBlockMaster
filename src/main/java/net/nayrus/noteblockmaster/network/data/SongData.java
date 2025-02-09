package net.nayrus.noteblockmaster.network.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.block.composer.Note;

import java.util.Map;

public record SongData(String title, Map<Integer, Note> notes) implements CustomPacketPayload {

    public static final Type<SongData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "songdata"));

    public static final Codec<SongData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("title").forGetter(SongData::title),
                    Codec.unboundedMap(Codec.INT, Note.CODEC).fieldOf("notes").forGetter(SongData::notes)
            ).apply(instance, SongData::new)
    );

    @Override
    public Type<SongData> type() {
        return TYPE;
    }
}
