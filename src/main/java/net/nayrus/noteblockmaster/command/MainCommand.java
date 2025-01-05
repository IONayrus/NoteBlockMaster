package net.nayrus.noteblockmaster.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.nayrus.noteblockmaster.Config;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.network.payload.ActionPing;
import net.nayrus.noteblockmaster.network.payload.ConfigCheck;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;

import java.awt.*;
import java.util.Objects;

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
                                PacketDistributor.sendToPlayer(Objects.requireNonNull(context.getSource().getPlayer()), new ActionPing(ActionPing.toByte(ActionPing.Action.SAVE_STARTUP_CONFIG)));
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                ).then(Commands.literal("debug")
                        .executes(context -> {
                            ServerPlayer player = (context.getSource().getPlayer());
                            if(player == null) {
                                context.getSource().sendFailure(Component.literal("Nope"));
                                return -1;
                            }
                            PacketDistributor.sendToPlayer(player, new ConfigCheck(
                                    (byte) 0,
                                    (byte) 82,
                                    (byte) AdvancedNoteBlock.SUBTICK_LENGTH
                            ));
                            return Command.SINGLE_SUCCESS;
                        })
                ).executes(context -> -1));
    }

}
