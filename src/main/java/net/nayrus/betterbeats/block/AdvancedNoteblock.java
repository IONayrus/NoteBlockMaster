package net.nayrus.betterbeats.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.nayrus.betterbeats.item.ItemRegistry;
import net.nayrus.betterbeats.util.SubTickScheduler;

public class AdvancedNoteblock extends NoteBlock
{

    public static final IntegerProperty SUBTICK = IntegerProperty.create("subtick",0,9);

    public AdvancedNoteblock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(SUBTICK,0));
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!stack.is(ItemRegistry.BEATWAKER.asItem())) return super.useItemOn(stack,state,level,pos,player,hand,hitResult);

        if(!level.isClientSide){
            player.sendSystemMessage(Component.literal(state.getValue(SUBTICK).toString()));
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            if(!player.getWeaponItem().is(ItemRegistry.BEATWAKER))
                super.attack(state, level, pos, player);
            else{
                level.setBlock(pos, state.cycle(SUBTICK), Block.UPDATE_ALL);
                player.sendSystemMessage(Component.literal(String.valueOf((state.getValue(SUBTICK) + 1))));
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
        if(state.getValue(SUBTICK) == 0) return super.triggerEvent(state, level, pos, id, param);
        SubTickScheduler.delayedNoteBlockEvent(state, level, pos, id, param);
        return true;
    }
}
