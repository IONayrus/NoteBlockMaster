package net.nayrus.noteblockmaster.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.nayrus.noteblockmaster.Config;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;

import java.awt.*;

public class ConfigSyncCommands {

    public static void reloadConfigCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("nbm")
                .then(Commands.literal("saveconfig")
                                .executes(context -> {
                                    Config.LOWER_NOTE_LIMIT.set(AdvancedNoteBlock.MIN_NOTE_VAL);
                                    Config.HIGHER_NOTE_LIMIT.set(AdvancedNoteBlock.MAX_NOTE_VAL);
                                    Config.SUBTICK_LENGTH.set(AdvancedNoteBlock.SUBTICK_LENGTH);
                                    //TODO Save config
                                    context.getSource().sendSuccess(()-> Component.literal("Updated local configs. Restart your client to apply.")
                                            .withColor(Color.GREEN.getRGB()), true);
                                    return Command.SINGLE_SUCCESS;
                                })
                ).executes(context -> -1));
    }

}
