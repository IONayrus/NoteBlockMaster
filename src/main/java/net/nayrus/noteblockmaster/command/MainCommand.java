package net.nayrus.noteblockmaster.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.nayrus.noteblockmaster.Config;
import net.nayrus.noteblockmaster.network.payload.ActionPing;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.awt.*;

public class MainCommand {

    public static void saveConfigCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("nbm")
                .then(Commands.literal("saveconfig")
                        .executes(context -> {
                            if(FMLEnvironment.dist != Dist.DEDICATED_SERVER){
                                Config.updateStartUpAndSave();
                                context.getSource().sendSuccess(()->Component.literal("Updated local configs. Restart your client to apply.")
                                        .withColor(Color.GREEN.darker().getRGB()), true);
                            }else{
                                ActionPing.sendActionPing(context.getSource().getPlayer(), ActionPing.Action.SAVE_STARTUP_CONFIG);
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                ).then(Commands.literal("debug")
                        .executes(context -> {
                            //ActionPing.sendActionPing(context.getSource().getPlayer(), ActionPing.Action.RENDER);
                            return Command.SINGLE_SUCCESS;
                        })
                ).executes(context -> -1));
    }

}
