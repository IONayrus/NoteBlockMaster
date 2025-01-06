package net.nayrus.noteblockmaster;

import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config
{
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<Object> LOWER_NOTE_LIMIT = BUILDER
            .comment(" Lowest key possible.\n Range: \"C1\", \"C#1\", ..., \"F#3\" or 0 ~ 30")
            .define("minNote", "C3", obj -> isNoteValue(obj, false));

    public static final ModConfigSpec.ConfigValue<Object> HIGHER_NOTE_LIMIT = BUILDER
            .comment(" Highest key possible.\n Range: \"F#5\", \"G5\", ..., \"A#7\" or 54 ~ 82")
            .define("maxNote", "C6", obj -> isNoteValue(obj, true));

    public static final ModConfigSpec.IntValue SUBTICK_LENGTH = BUILDER
            .comment(" Time in ms per subtick. Amount of subticks equals 2 ticks (100ms) / subtick length")
            .defineInRange("subtickDelay", 5, 5, 25);

    public static final ModConfigSpec START_UP = BUILDER.build();

    public static boolean isNoteValue(Object str, boolean high){
        if(!(str instanceof String configStr)){
            if(str instanceof Integer val) return Utils.isIntInRange(val, high ? 54: 0, high ? 82 : 30);
            else return false;
        }
        try{
            return high ? AdvancedNoteBlock.noteStringAsInt(configStr) >= 54 : AdvancedNoteBlock.noteStringAsInt(configStr) <= 30;
        }
        catch (IllegalArgumentException e) {return false;}
    }

    public static boolean UPDATED = false;
    public static void updateStartUpAndSave(){
        Config.LOWER_NOTE_LIMIT.set(AdvancedNoteBlock.MIN_NOTE_VAL);
        Config.HIGHER_NOTE_LIMIT.set(AdvancedNoteBlock.MAX_NOTE_VAL);
        Config.SUBTICK_LENGTH.set(AdvancedNoteBlock.SUBTICK_LENGTH);
        Config.START_UP.save();
        UPDATED = true;
    }
}
