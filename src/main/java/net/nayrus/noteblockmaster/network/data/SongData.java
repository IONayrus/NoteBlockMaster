package net.nayrus.noteblockmaster.network.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import libs.felnull.fnnbs.Layer;
import libs.felnull.fnnbs.NBS;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.block.composer.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public record SongData(String title, Map<Integer, List<Note>> notes) implements CustomPacketPayload {

    public static final Type<SongData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "songdata"));

    public static final Codec<SongData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("title").forGetter(SongData::title),
                    Codec.unboundedMap(Codec.INT, Codec.list(Note.CODEC)).fieldOf("notes").forGetter(SongData::notes)
            ).apply(instance, SongData::new)
    );

    @Override
    public Type<SongData> type() {
        return TYPE;
    }

    public static SongData of(NBS nbs){
        List<Layer> layers = nbs.getLayers();
        TreeMap<Integer, List<Note>> notes = new TreeMap<>();
        for(int beat = 0; beat <= nbs.getSongLength(); beat++){
            List<Note> notesAtLayer = new ArrayList<>();
            int finalBeat = beat;
            layers.forEach(layer -> {
                libs.felnull.fnnbs.Note current = layer.getNote(finalBeat);
                if(current != null) notesAtLayer.add(Note.of(current));
            });
            notes.put(finalBeat, notesAtLayer);
        }
        return new SongData(nbs.getName(), notes);
    }
}
