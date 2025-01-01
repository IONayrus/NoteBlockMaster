package net.nayrus.noteblockmaster.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;

public class BPMInfoCommand {

    public BPMInfoCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("bpmcalc")
                .then(
                        Commands.argument("bpm", FloatArgumentType.floatArg(0,10000))
                                .executes(context -> {
                                    float bpm = FloatArgumentType.getFloat(context, "bpm");
                                    int noteCount = 100;
                                    context.getSource().sendSuccess(()->Component.literal(calculateTiming(bpm, noteCount)), true);
                                    return Command.SINGLE_SUCCESS;
                                })
                ).executes(context -> Command.SINGLE_SUCCESS));
    }

    private String calculateTiming(float bpm, int noteCount){
        float tPB = (int)(60000 / bpm);
        StringBuilder sb = new StringBuilder();

        int lastTime = 0;
        sb.append("[0]");
        for(int i = 1; i <=noteCount; i++){
            int noteTimeMs = (int) (i * tPB);
            int subTickTime = noteTimeMs % 100;
            int redClockTime = noteTimeMs - subTickTime;
            int subtick = (subTickTime - (subTickTime % AdvancedNoteBlock.SUBTICK_LENGTH)) / AdvancedNoteBlock.SUBTICK_LENGTH;

            if(redClockTime == lastTime) sb.append(" - [").append(subtick).append("]");
            else{
                sb.append(" ~ ").append((redClockTime - lastTime) / 100).append(" ~ ");
                sb.append(" [").append(subtick).append("]");
            }
            lastTime = redClockTime;
        }
        return sb.toString();
    }

}
