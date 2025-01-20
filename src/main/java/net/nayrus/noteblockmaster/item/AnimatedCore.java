package net.nayrus.noteblockmaster.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.block.TuningCore;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.utils.Utils;

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
        BlockState state = level.getBlockState(pos.above());
        ItemStack stack = player.getItemInHand(hand);
        if(state.is(Registry.TUNINGCORE)) return addCoreToTuningCore(level, player, pos.above(), state, stack, hand);
        if(!state.isAir()) return InteractionResult.PASS;
        if(!level.isClientSide()){
            state = Registry.TUNINGCORE.get().defaultBlockState();
            level.setBlockAndUpdate(pos.above(), stack.is(Registry.SUSTAIN) ? state.setValue(TuningCore.SUSTAIN, TuningCore.SUSTAIN_MAXVAL) : state.setValue(TuningCore.VOLUME, 20));
            stack.shrink(1);
            level.playSound(null, pos, TuningCore.CORE_SOUNDS.getPlaceSound(), SoundSource.BLOCKS);
            if(hand.equals(InteractionHand.MAIN_HAND)) return InteractionResult.SUCCESS;
            return InteractionResult.CONSUME;
        }
        if(hand.equals(InteractionHand.OFF_HAND)) player.swing(hand);
        return InteractionResult.PASS;
    }

    public InteractionResult addCoreToTuningCore(Level level, Player player, BlockPos pos, BlockState state, ItemStack stack, InteractionHand hand){
        if(stack.is(Registry.SUSTAIN) ){
            if(TuningCore.isSustaining(state)) return InteractionResult.PASS;
            if(level.isClientSide()) return Utils.swingHelper(player, hand, true);
            level.setBlockAndUpdate(pos, state.setValue(TuningCore.SUSTAIN, TuningCore.SUSTAIN_MAXVAL));
        }else{
            if(TuningCore.isMixing(state)) return InteractionResult.PASS;
            if(level.isClientSide()) return Utils.swingHelper(player, hand, true);
            level.setBlockAndUpdate(pos, state.setValue(TuningCore.VOLUME, 20));
        }
        stack.shrink(1);
        level.playSound(null, pos, TuningCore.CORE_SOUNDS.getPlaceSound(), SoundSource.BLOCKS);
        return Utils.swingHelper(player, hand, false);
    }


}
