package net.nayrus.noteblockmaster.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;

import java.awt.*;

public class BPMInfoCommand {

    public BPMInfoCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("bpmcalc")
                .then(
                        Commands.argument("bpm", FloatArgumentType.floatArg(0, 60000.0F/AdvancedNoteBlock.SUBTICK_LENGTH))
                                .executes(context -> {
                                    float bpm = FloatArgumentType.getFloat(context, "bpm");
                                    int noteCount = 100;
                                    context.getSource().sendSuccess(()->calculateTiming(bpm, noteCount), true);
                                    return Command.SINGLE_SUCCESS;
                                })
                ).executes(context -> Command.SINGLE_SUCCESS));
    }

    private Component calculateTiming(float bpm, int noteCount){
        float tPB = 60000 / bpm;
        MutableComponent com = Component.literal("[0]").withColor(Color.MAGENTA.getRGB());

        int lastTime = 0;
        for(int i = 1; i <=noteCount; i++){
            int noteTimeMs = (int) (i * tPB);
            int subTickTime = noteTimeMs % 100;
            int redClockTime = noteTimeMs - subTickTime;
            int subtick = (subTickTime - (subTickTime % AdvancedNoteBlock.SUBTICK_LENGTH)) / AdvancedNoteBlock.SUBTICK_LENGTH;

            if(redClockTime == lastTime) com.append("-").withColor(Color.CYAN.getRGB()).append("["+subtick+"]").withColor(Color.MAGENTA.getRGB());
            else{
                com.append("~").withColor(Color.GRAY.getRGB()).append(String.valueOf((redClockTime - lastTime) / 100))
                        .withColor(Color.ORANGE.getRGB()).append("~").withColor(Color.GRAY.getRGB());
                com.append("["+subtick+"]").withColor(Color.MAGENTA.getRGB());
            }
            lastTime = redClockTime;
        }
        return com;
    }

}
