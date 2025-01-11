package net.nayrus.noteblockmaster.item;

import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;

public class ComposersNote extends Item {

    public ComposersNote() {
        super(new Item.Properties()
                .stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(!((context.getPlayer()) instanceof Player player)) return InteractionResult.FAIL;
        if(player.getOffhandItem().getItem() instanceof TunerItem item) return item.useOn(context, true);
        return InteractionResult.PASS;
    }

    public static Tuple<Integer, Integer> subtickAndPauseOnBeat(int beat, float bpm){
        float tPB = 60000 / bpm;
        int current_subtick = 0;
        int pre_delay = 0;
        int lastTime = 0;
        for(int i = (beat - 1); i <= beat; i++){
            int noteTimeMs = (int) (i * tPB);
            int subTickTime = noteTimeMs % 100;
            int redClockTime = noteTimeMs - subTickTime;
            int subtick = (subTickTime - (subTickTime % AdvancedNoteBlock.SUBTICK_LENGTH)) / AdvancedNoteBlock.SUBTICK_LENGTH;

            if(i == beat){
               current_subtick = subtick;
               pre_delay = ((redClockTime - lastTime) / 100);
            }
            lastTime = redClockTime;
        }
        return new Tuple<>(current_subtick, pre_delay);
    }
}
