package net.nayrus.noteblockmaster.composer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import libs.felnull.fnnbs.Layer;
import libs.felnull.fnnbs.NBS;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.utils.Utils;

import java.util.*;

public record SongData(String title, String author, Map<String, List<Note>> notes) implements CustomPacketPayload {

    public static final Type<SongData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NoteBlockMaster.MOD_ID, "songdata"));

    public static final Codec<SongData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("title").forGetter(SongData::title),
                    Codec.STRING.fieldOf("author").forGetter(SongData::author),
                    Codec.unboundedMap(Codec.STRING, Codec.list(Note.CODEC)).fieldOf("notes").forGetter(SongData::notes)
            ).apply(instance, SongData::new)
    );

    public static final StreamCodec<FriendlyByteBuf, SongData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SongData::title,
            ByteBufCodecs.STRING_UTF8, SongData::author,
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
        return new SongData(nbs.getName(), nbs.getAuthor(), notes);
    }

    public static SongData load(CompoundTag tag) {
        Map<String, List<Note>> notesMap = new HashMap<>();
        if (tag.contains("Notes", Tag.TAG_COMPOUND)) {
            CompoundTag notesTag = tag.getCompound("Notes");

            for (String beat : notesTag.getAllKeys()) {
                int[] packedNotes = notesTag.getIntArray(beat);

                List<Note> notesAtBeat = new ArrayList<>();
                for (int packedNote : packedNotes) {
                    notesAtBeat.add(new Note(packedNote));
                }
                notesMap.put(beat, notesAtBeat);
            }
        }
        return new SongData(tag.getString("Title"), tag.getString("Author"), notesMap);
    }

    public CompoundTag save(CompoundTag tag) {
        CompoundTag notesTag = new CompoundTag();

        for (Map.Entry<String, List<Note>> entry : this.notes.entrySet()) {
            List<Integer> packedNoteList = new ArrayList<>();
            entry.getValue().forEach(note -> packedNoteList.add(note.packNote()));
            notesTag.put(entry.getKey(), new IntArrayTag(packedNoteList));
        }

        tag.putString("Title", this.title());
        tag.putString("Author", this.author());
        tag.put("Notes", notesTag);
        return tag;
    }

    public UUID getID(){
        return Utils.generateUUIDFromString(this.toString());
    }
}
