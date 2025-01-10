package net.nayrus.noteblockmaster.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.item.TunerItem;
import net.nayrus.noteblockmaster.network.payload.ActionPing;
import net.nayrus.noteblockmaster.utils.Registry;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

public class CommonEvents {

    @SubscribeEvent
    public static void useItemOnBlockEvent(UseItemOnBlockEvent event){
        UseOnContext context = event.getUseOnContext();
        Level level =  context.getLevel();
        if(!level.isClientSide())
            if(event.getItemStack().getItem() instanceof TunerItem && context.getPlayer() instanceof Player player){
                BlockPos pos = context.getClickedPos();
                BlockState state = level.getBlockState(pos);
                if((state.getBlock() instanceof NoteBlock)){
                    Inventory inv = player.getInventory();
                    if(inv.countItem(Items.GOLD_NUGGET) >= 3){
                        level.setBlock(pos, Registry.ADVANCED_NOTEBLOCK.get().defaultBlockState()
                                .setValue(AdvancedNoteBlock.NOTE, state.getValue(NoteBlock.NOTE) + AdvancedNoteBlock.DEFAULT_NOTE), NoteBlock.UPDATE_ALL);
                        level.playSound(null, pos, Registry.SMITHING.get(), SoundSource.BLOCKS, 0.5F, NoteBlock.getPitchFromNote(14) + RandomSource.create().nextFloat()/10.0F);
                        ActionPing.sendActionPing((ServerPlayer) player, ActionPing.Action.GOLD_BREAK);
                        if(!player.isCreative()) Utils.removeItemsFromInventory(inv, Items.GOLD_NUGGET, 3);
                        event.cancelWithResult(ItemInteractionResult.SUCCESS);
                    }
                }
            }
    }

}
