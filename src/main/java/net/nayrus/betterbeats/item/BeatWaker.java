package net.nayrus.betterbeats.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.nayrus.betterbeats.block.AdvancedNoteblock;
import net.nayrus.betterbeats.util.SubTickScheduler;
import org.jetbrains.annotations.NotNull;

public class BeatWaker extends Item {
    public BeatWaker(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return itemStack.copy();
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        return super.mineBlock(stack, level, state, pos, miningEntity);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        if(player==null) return InteractionResult.FAIL;
        if(!(state.getBlock() instanceof AdvancedNoteblock block)) return InteractionResult.PASS;
        if(!level.isClientSide) {

            if (stack.is(ItemRegistry.BEAT_WAKER)) {
                int val = state.getValue(AdvancedNoteblock.SUBTICK);
                player.displayClientMessage(Component.literal(val / 10f + " ticks (" + val * SubTickScheduler.SUBTICK_LENGTH + " ms)").withColor(0xB0B0B0), true);
                return InteractionResult.SUCCESS;
            }
            if (stack.is(ItemRegistry.NOTE_WAKER)) {
                int old_val = state.getValue(AdvancedNoteblock.NOTE);
                int new_val = player.isShiftKeyDown() ? (old_val + 5) % 25 : (old_val + 1) % 25;
                int _new = net.neoforged.neoforge.common.CommonHooks.onNoteChange(level, pos, state, old_val, new_val);

                if (_new == -1) return InteractionResult.FAIL;
                state = state.setValue(AdvancedNoteblock.NOTE, _new);
                level.setBlock(pos, state, 3);
                if (state.getValue(AdvancedNoteblock.INSTRUMENT).worksAboveNoteBlock() || level.getBlockState(pos.above()).isAir()) {
                    level.blockEvent(pos, block, 0, 0);
                    level.gameEvent(player, GameEvent.NOTE_BLOCK_PLAY, pos);
                }
                player.awardStat(Stats.TUNE_NOTEBLOCK);
                player.displayClientMessage(Component.literal(AdvancedNoteblock.NOTE_STRING[_new]).withColor(0xB030B0), true);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
