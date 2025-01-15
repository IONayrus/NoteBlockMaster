package net.nayrus.noteblockmaster.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.block.TuningCore;
import net.nayrus.noteblockmaster.setup.Registry;

public class AnimatedCore extends Item {

    public AnimatedCore() {
        super(new Item.Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if(player== null) return InteractionResult.PASS;
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if(state.is(Registry.ADVANCED_NOTEBLOCK)) return addCoreToAdvancedNoteBlock(level, player, pos, context.getHand());
        return InteractionResult.PASS;
    }

    public InteractionResult addCoreToAdvancedNoteBlock(Level level, Player player, BlockPos pos, InteractionHand hand) {
        if(!level.getBlockState(pos.above()).isAir()) return InteractionResult.PASS;
        if(!level.isClientSide()){
            level.setBlockAndUpdate(pos.above(), Registry.TUNINGCORE.get().defaultBlockState());
            player.getItemInHand(hand).shrink(1);
            level.playSound(null, pos, TuningCore.CORE_SOUNDS.getPlaceSound(), SoundSource.BLOCKS);
            if(hand.equals(InteractionHand.MAIN_HAND)) return InteractionResult.SUCCESS;
            return InteractionResult.CONSUME;
        }
        if(hand.equals(InteractionHand.OFF_HAND)) player.swing(hand);
        return InteractionResult.PASS;
    }
}
