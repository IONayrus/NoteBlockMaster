package net.nayrus.noteblockmaster;

import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.util.Util;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue ADDITIONAL_OCTAVES = BUILDER
            .comment(" Amount of octaves the key range of an Advanced Note Block is extended by")
            .defineInRange("addOctaves", 1, 0, 2);

    public static final ModConfigSpec.ConfigValue<String> LOWER_NOTE_LIMIT = BUILDER
            .comment(" Lowest key possible - overwrites octave limit if higher (minimum 'C1' or '0')")
            .define("minNote", "F#2", Config::isNoteValue);

    public static final ModConfigSpec.ConfigValue<String> HIGHER_NOTE_LIMIT = BUILDER
            .comment(" Highest key possible - overwrites octave limit if lower (maximum 'A#7' or '86')")
            .define("maxNote", "F#6", Config::isNoteValue);

    public static final ModConfigSpec.IntValue SUBTICK_LENGTH = BUILDER
            .comment(" Time in ms per subtick. Amount of subticks equals 2 ticks (100ms) / subtick length")
            .defineInRange("subtickDelay", 5, 5, 25);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean isNoteValue(Object str){
        if(!(str instanceof String configStr)){
            if(str instanceof Integer val) return Util.isIntInRange(val, 0, 86);
            else return false;
        }
        try{ AdvancedNoteBlock.noteStringAsInt(configStr); }
        catch (IllegalArgumentException e) {return false;}
        return true;
    }
}
