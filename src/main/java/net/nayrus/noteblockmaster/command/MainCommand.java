package net.nayrus.noteblockmaster.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.nayrus.noteblockmaster.composer.SongCache;
import net.nayrus.noteblockmaster.network.data.SongID;
import net.nayrus.noteblockmaster.network.payload.ActionPing;
import net.nayrus.noteblockmaster.network.payload.LoadSong;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.setup.config.ClientConfig;
import net.nayrus.noteblockmaster.setup.config.StartupConfig;
import net.nayrus.noteblockmaster.utils.FinalTuple;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;

import java.awt.*;
import java.util.UUID;

public class MainCommand {

    public static void mainCommand(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("nbm")
                .then(saveConfigCommand())
                .then(lowResolutionCommand())
                .then(songCommands())
                .then(Commands.literal("debug")
                        .executes(context -> {
                            SongCache.SERVER_CACHE.saveAndClearCache();


                            return Command.SINGLE_SUCCESS;
                        }))
                .executes(context -> -1));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> saveConfigCommand(){
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

    private static LiteralArgumentBuilder<CommandSourceStack> lowResolutionCommand(){
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

    private static LiteralArgumentBuilder<CommandSourceStack> songCommands(){
        return Commands.literal("songs")
                .then(loadSongCommand())
                .then(pushSongCommand())
                .then(dropSongCommand())
                .then(applySongCommand())
                .then(listAvailableSongsCommand())
                .then(listCachedSongsCommand());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> loadSongCommand(){
        return Commands.literal("load").then(Commands.argument("name", StringArgumentType.greedyString())
                .executes(context -> {
                    if(context.getSource().isPlayer()){
                        String songName = StringArgumentType.getString(context, "name");
                        PacketDistributor.sendToPlayer(context.getSource().getPlayerOrException(), new LoadSong(songName));
                        return Command.SINGLE_SUCCESS;
                    }else{

                        //Should you be able to load songs from Server? Admin can just use his client tho

                        return -1;
                    }
                }));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> pushSongCommand(){
        return Commands.literal("push").then(Commands.argument("UUID", UuidArgument.uuid()).executes(context -> {
            if(!context.getSource().isPlayer()) return 1;
            ServerPlayer player = context.getSource().getPlayerOrException();
            UUID id = UuidArgument.getUuid(context, "UUID");
            //TODO Permission Check
            PacketDistributor.sendToPlayer(player, new SongCache.PushRequest(id));
            return Command.SINGLE_SUCCESS;
        }));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> dropSongCommand() {
        //TODO Permissions
        return Commands.literal("delete").then(Commands.argument("UUID", UuidArgument.uuid()).executes(context -> {
            if(!context.getSource().isPlayer()) return 1;
            ServerPlayer player = context.getSource().getPlayerOrException();
            UUID id = UuidArgument.getUuid(context, "UUID");
            PacketDistributor.sendToPlayer(player, new SongCache.DropRequest(id));
            return Command.SINGLE_SUCCESS;
        })).then(Commands.literal("fromNetwork").then(Commands.argument("UUID", UuidArgument.uuid()).executes(context -> {
            UUID id = UuidArgument.getUuid(context, "UUID");
            SongCache.SERVER_CACHE.dropSong(id);
            PacketDistributor.sendToAllPlayers(new SongCache.DropRequest(id));
            return Command.SINGLE_SUCCESS;
        }))).then(Commands.literal("fromServer").then(Commands.argument("UUID", UuidArgument.uuid()).executes(context -> {
            UUID id = UuidArgument.getUuid(context, "UUID");
            SongCache.SERVER_CACHE.dropSong(id);
            return Command.SINGLE_SUCCESS;
        })));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> applySongCommand(){
        return Commands.literal("apply").then(Commands.argument("UUID", UuidArgument.uuid()).executes(context -> {
                    if(context.getSource().isPlayer()){
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        ItemStack composition = FinalTuple.getHeldItems(player).getFirst(Registry.COMPOSITION.get());
                        if(composition.isEmpty()){
                            context.getSource().sendFailure(Component.literal("Unable to write Song ID onto invalid ItemStack")); return Command.SINGLE_SUCCESS;
                        }
                        SongID songID = new SongID(UuidArgument.getUuid(context, "UUID"));
                        composition.set(Registry.SONG_ID, songID);
                        PacketDistributor.sendToPlayer(player, songID);

                        return Command.SINGLE_SUCCESS;
                    }
                    return -1;
                }));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> listCachedSongsCommand(){
        return Commands.literal("listCached").executes(context -> {
            if(FMLEnvironment.dist == Dist.DEDICATED_SERVER){
                if(context.getSource().isPlayer()){
                    ActionPing.sendActionPing(context.getSource().getPlayerOrException(), ActionPing.Action.LIST_CLIENT_CACHE);
                }else{
                    for(String songInfo : SongCache.SERVER_CACHE.getCachedSongInfo()){
                        context.getSource().sendSystemMessage(Component.literal(songInfo));
                    }
                }
                return Command.SINGLE_SUCCESS;
            }

            context.getSource().sendSystemMessage(Component.literal("SERVER CACHE:"));
            for(String songInfo : SongCache.SERVER_CACHE.getCachedSongInfo()){
                context.getSource().sendSystemMessage(Component.literal("- " + songInfo.substring(40)).withStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, songInfo.substring(0, 36)))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy UUID (" + songInfo.substring(0, 36) + ")")))));
            }
            context.getSource().sendSystemMessage(Component.literal("CLIENT CACHE:"));
            ActionPing.sendActionPing(context.getSource().getPlayerOrException(), ActionPing.Action.LIST_CLIENT_CACHE);
            return Command.SINGLE_SUCCESS;
        });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> listAvailableSongsCommand(){
        return Commands.literal("listAvailable").executes(context -> {
            context.getSource().sendSystemMessage(Component.literal("Available song IDs:"));
            for(String songID : SongCache.SERVER_CACHE.getRegisteredSongIDs()){
                context.getSource().sendSystemMessage(Component.literal(songID));
            }
            return Command.SINGLE_SUCCESS;
        });
    }

}
