package net.nayrus.noteblockmaster.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.NoteBlockMaster;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.command.BPMInfoCommand;
import net.nayrus.noteblockmaster.command.MainCommand;
import net.nayrus.noteblockmaster.composer.SongCache;
import net.nayrus.noteblockmaster.composer.SongFileManager;
import net.nayrus.noteblockmaster.item.TunerItem;
import net.nayrus.noteblockmaster.network.data.ComposeData;
import net.nayrus.noteblockmaster.network.payload.ConfigCheck;
import net.nayrus.noteblockmaster.network.payload.RemoveBlockInfo;
import net.nayrus.noteblockmaster.network.payload.SyncBlockInfos;
import net.nayrus.noteblockmaster.render.ANBInfoRender;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.sound.SoundRegistry;
import net.nayrus.noteblockmaster.utils.FinalTuple;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.command.ConfigCommand;

import java.util.HashMap;

public class CommonEvents {

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event){
        new BPMInfoCommand(event.getDispatcher());
        MainCommand.mainCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onServerStart(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        NoteBlockMaster.LOGGER.info("Loading saved songs");
        SongCache.SERVER_CACHE = new SongCache(false).loadFromWorld(server.overworld());
        SongFileManager.validateAndLoadCache();
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        if(FMLEnvironment.dist == Dist.DEDICATED_SERVER)
            PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new ConfigCheck(
                    (byte) AdvancedNoteBlock.MIN_NOTE_VAL,
                    (byte) AdvancedNoteBlock.MAX_NOTE_VAL,
                    (byte) AdvancedNoteBlock.SUBTICK_LENGTH
            ));
        else if(ANBInfoRender.NOTE_OFF_SYNC || ANBInfoRender.SUBTICK_OFF_SYNC) AdvancedNoteBlock.resetPropertiesToLoadedValues();
    }

    @SubscribeEvent
    public static void onANBStateChange(AdvancedNoteBlockEvent.StateChange e){
        if(!(e.getLevel() instanceof ServerLevel level)) return;
        if(e instanceof  AdvancedNoteBlockEvent.Removed){
            PacketDistributor.sendToPlayersInDimension(level, new RemoveBlockInfo(e.getPos()));
            return;
        }
        HashMap<BlockPos, ANBInfoRender.BlockInfo> toSync = new HashMap<>();
        toSync.put(e.getPos(), new ANBInfoRender.BlockInfo(AdvancedNoteBlock.getNoteValue(e.getState()), e.getState().getValue(AdvancedNoteBlock.SUBTICK)));
        PacketDistributor.sendToPlayersInDimension(level, new SyncBlockInfos(toSync));
    }

    @SubscribeEvent
    public static void useItemOnBlockEvent(UseItemOnBlockEvent event){
        UseOnContext context = event.getUseOnContext();
        Level level =  context.getLevel();
        if (context.getPlayer() instanceof Player player) {
            BlockPos pos = context.getClickedPos();
            BlockState state = level.getBlockState(pos);
            FinalTuple.ItemStackTuple items = FinalTuple.getHeldItems(player);
            //Block transformation
            if (items.contains(TunerItem.class)) {
                if ((state.getBlock() instanceof NoteBlock)) {
                    Inventory inv = player.getInventory();
                    if (inv.countItem(Items.GOLD_NUGGET) >= 3) {
                        if(!level.isClientSide) {
                            level.setBlock(pos, Registry.ADVANCED_NOTEBLOCK.get().defaultBlockState()
                                    .setValue(AdvancedNoteBlock.NOTE, state.getValue(NoteBlock.NOTE) + AdvancedNoteBlock.DEFAULT_NOTE), NoteBlock.UPDATE_ALL);
                            level.playSound(null, pos, SoundRegistry.SMITHING.get(), SoundSource.BLOCKS, 0.5F, NoteBlock.getPitchFromNote(14) + RandomSource.create().nextFloat() / 10.0F);
                            if (!player.isCreative()) Utils.removeItemsFromInventory(inv, Items.GOLD_NUGGET, 3);
                        }else{
                            level.addDestroyBlockEffect(pos, Blocks.GOLD_BLOCK.defaultBlockState());
                        }
                        if (items.getA().getItem() instanceof TunerItem)
                            event.cancelWithResult(InteractionResult.SUCCESS);
                        else {
                            player.swing(InteractionHand.OFF_HAND);
                            event.cancelWithResult(InteractionResult.CONSUME);
                        }
                    }
                }
            }
            //Repeater quick set
            if(state.getBlock() instanceof RepeaterBlock && items.contains(Registry.COMPOSITION.get())){
                ItemStack composer = items.getFirst(Registry.COMPOSITION.get());
                ComposeData cData = ComposeData.getComposeData(composer);
                int target = cData.postDelay();
                int set = Math.min(target, 4);
                if(target>0){
                    target -= set;
                    if(!level.isClientSide()){
                        level.setBlock(pos, state.setValue(RepeaterBlock.DELAY, set), NoteBlock.UPDATE_ALL);
                        composer.set(Registry.COMPOSE_DATA, new ComposeData(cData.beat(), cData.subtick(), target, cData.bpm(), cData.placed()));
                    }
                    if(items.getA().is(Registry.COMPOSITION)) event.cancelWithResult(InteractionResult.SUCCESS);
                    else{
                        event.cancelWithResult(InteractionResult.CONSUME);
                        player.swing(InteractionHand.OFF_HAND);
                    }
                }
                else{
                    event.cancelWithResult(InteractionResult.FAIL);
                    if(level.isClientSide()) Utils.playFailUse(level, player, pos);
                }
            }
        }
    }

}
