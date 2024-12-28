package net.nayrus.betterbeats.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.nayrus.betterbeats.item.ItemRegistry;
import net.nayrus.betterbeats.util.BetterTags;
import net.nayrus.betterbeats.util.SubTickScheduler;

public class AdvancedNoteblock extends NoteBlock
{

    public static final int MAX_SUBTICKS = (int) (100 / SubTickScheduler.SUBTICK_LENGTH);
    public static final IntegerProperty SUBTICK = IntegerProperty.create("subtick",0,MAX_SUBTICKS-1);

    public AdvancedNoteblock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(SUBTICK,0));
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if(player.getWeaponItem().is(BetterTags.Items.BEATWAKERS)){
            if(!level.isClientSide())
                attack(state, level, pos, player);
            return false;
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return player.getWeaponItem().is(BetterTags.Items.BEATWAKERS) ? 0.0f : super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!stack.is(ItemRegistry.BEAT_WAKER.asItem())) return super.useItemOn(stack,state,level,pos,player,hand,hitResult);

        if(!level.isClientSide){
            int val = state.getValue(SUBTICK);
            player.displayClientMessage(Component.literal(val/10f + " ticks ("+val * SubTickScheduler.SUBTICK_LENGTH+" ms)").withColor(0xB0B0B0), true);
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            if(!player.getWeaponItem().is(ItemRegistry.BEAT_WAKER))
                super.attack(state, level, pos, player);
            else{
                int state_val = state.getValue(SUBTICK);
                int new_val = (player.isShiftKeyDown() ? state_val + 5 : state_val + 1) % 20;

                level.setBlock(pos, state.setValue(SUBTICK, new_val), Block.UPDATE_ALL);

                player.displayClientMessage(Component.literal(new_val/10f + " ticks ("+new_val * SubTickScheduler.SUBTICK_LENGTH+" ms)").withColor(0xB0B0B0), true);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SUBTICK);
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        SubTickScheduler.delayedNoteBlockEvent(state, level, pos, id, param);
        return true;
    }
}
