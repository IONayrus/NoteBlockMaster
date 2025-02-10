package net.nayrus.noteblockmaster.network.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import libs.felnull.fnnbs.Layer;
import libs.felnull.fnnbs.NBS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.block.composer.Note;

import java.util.*;

public record SongData(String title, Map<String, List<Note>> notes) implements CustomPacketPayload {

    public static final Type<SongData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "songdata"));

    public static final Codec<SongData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("title").forGetter(SongData::title),
                    Codec.unboundedMap(Codec.STRING, Codec.list(Note.CODEC)).fieldOf("notes").forGetter(SongData::notes)
            ).apply(instance, SongData::new)
    );

    public static final StreamCodec<FriendlyByteBuf, SongData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SongData::title,
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.STRING_UTF8,
                    ByteBufCodecs.collection(
                            ArrayList::new,
                            Note.STREAM_CODEC,
                            32)), SongData::notes,
            SongData::new
    );

    @Override
    public Type<SongData> type() {
        return TYPE;
    }

    public static SongData of(NBS nbs){
        List<Layer> layers = nbs.getLayers();
        TreeMap<String, List<Note>> notes = new TreeMap<>();
        for(int beat = 0; beat <= nbs.getSongLength(); beat++){
            List<Note> notesAtLayer = new ArrayList<>();
            int finalBeat = beat;
            layers.forEach(layer -> {
                libs.felnull.fnnbs.Note current = layer.getNote(finalBeat);
                if(current != null) notesAtLayer.add(Note.of(current));
            });
            if(notesAtLayer.size() > 32) throw new IllegalStateException("The song " + nbs.getName() + " by " + nbs.getAuthor() + " has more than 32 notes at tick " + finalBeat);
            notes.put(finalBeat+"", notesAtLayer);
        }
        return new SongData(nbs.getName(), notes);
    }
}
