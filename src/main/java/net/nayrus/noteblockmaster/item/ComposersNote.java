package net.nayrus.noteblockmaster.item;

import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.network.data.ComposeData;
import net.nayrus.noteblockmaster.utils.Registry;
import org.jetbrains.annotations.NotNull;

public class ComposersNote extends Item {

    public ComposersNote() {
        super(new Item.Properties()
                .stacksTo(1));
    }

    public static @NotNull ComposeData getComposeData(ItemStack stack){
        ComposeData data = stack.get(Registry.COMPOSE_DATA);
        if(data == null) {
            data = new ComposeData(0, 0,1,600);
            stack.set(Registry.COMPOSE_DATA, data);
        }
        return data;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(!((context.getPlayer()) instanceof Player player)) return InteractionResult.FAIL;
        if(player.getOffhandItem().getItem() instanceof TunerItem item) return item.useOn(context, true);
        return InteractionResult.PASS;
    }

    public static Tuple<Integer, Integer> subtickAndPauseOnBeat(int beat, float bpm){
        float tPB = 60000 / bpm;
        int first_subtick = 0;
        int next_pauseticks = 0;
        int lastTime = 0;
        for(int i = beat; i <=(beat + 1); i++){
            int noteTimeMs = (int) (i * tPB);
            int subTickTime = noteTimeMs % 100;
            int redClockTime = noteTimeMs - subTickTime;
            int subtick = (subTickTime - (subTickTime % AdvancedNoteBlock.SUBTICK_LENGTH)) / AdvancedNoteBlock.SUBTICK_LENGTH;

            if(i == beat)
               first_subtick = subtick;
            else{
                 next_pauseticks = ((redClockTime - lastTime) / 100);
            }
            lastTime = redClockTime;
        }
        return new Tuple<>(first_subtick, next_pauseticks);
    }
}
