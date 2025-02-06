package net.nayrus.noteblockmaster.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.nayrus.noteblockmaster.network.payload.ActionPing;
import net.nayrus.noteblockmaster.setup.config.ClientConfig;
import net.nayrus.noteblockmaster.setup.config.StartupConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.awt.*;

public class MainCommand {

    public static void mainCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("nbm")
                .then(saveConfigCommand())
                .then(lowResolutionCommand())
                .then(Commands.literal("debug")
                        .executes(context -> {
                            return Command.SINGLE_SUCCESS;
                        })
                ).executes(context -> -1));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> saveConfigCommand(){
        return Commands.literal("saveconfig")
                .executes(context -> {
                    if(FMLEnvironment.dist != Dist.DEDICATED_SERVER){
                        if(!StartupConfig.UPDATED) {
                            StartupConfig.updateStartUpAndSave();
                            context.getSource().sendSuccess(() -> Component.translatable("text.config.updated")
                                    .withColor(Color.GREEN.darker().getRGB()), true);
                        }
                    }else{
                        if(context.getSource().getPlayer() instanceof ServerPlayer player) ActionPing.sendActionPing(player, ActionPing.Action.SAVE_STARTUP_CONFIG);
                    }
                    return Command.SINGLE_SUCCESS;
                });
    }

    public static LiteralArgumentBuilder<CommandSourceStack> lowResolutionCommand(){
        return Commands.literal("improveFPS")
                .then(Commands.argument("activate", BoolArgumentType.bool())
                        .executes(context -> {
                            if(FMLEnvironment.dist != Dist.DEDICATED_SERVER){
                                boolean activate = BoolArgumentType.getBool(context, "activate");
                                if(ClientConfig.LOW_RESOLUTION_RENDER.get() != activate) {
                                    ClientConfig.LOW_RESOLUTION_RENDER.set(activate);
                                    context.getSource().sendSuccess(() -> Component.translatable(activate ? "text.lowres.enable" : "text.lowres.disable"), true);
                                    ClientConfig.CLIENT.save();
                                }
                            }else{
                                if(context.getSource().getPlayer() instanceof ServerPlayer player)
                                    ActionPing.sendActionPing(player, BoolArgumentType.getBool(context, "activate") ? ActionPing.Action.ACTIVATE_LOW_RES_RENDER : ActionPing.Action.DEACTIVATE_LOW_RES_RENDER);
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                ).executes(context -> {
                    if(FMLEnvironment.dist != Dist.DEDICATED_SERVER){
                        if(ClientConfig.LOW_RESOLUTION_RENDER.isFalse()) {
                            ClientConfig.LOW_RESOLUTION_RENDER.set(true);
                            context.getSource().sendSuccess(() -> Component.translatable("text.lowres.enable"), true);
                            ClientConfig.CLIENT.save();
                        } else{
                            if(context.getSource().getPlayer() instanceof ServerPlayer player) ActionPing.sendActionPing(player, ActionPing.Action.ACTIVATE_LOW_RES_RENDER);
                        }
                    }
                    return Command.SINGLE_SUCCESS;
                });
    }

}
