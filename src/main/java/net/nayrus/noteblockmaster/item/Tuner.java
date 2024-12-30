package net.nayrus.noteblockmaster.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.util.Registry;
import net.nayrus.noteblockmaster.util.SubTickScheduler;
import org.jetbrains.annotations.NotNull;

import static net.nayrus.noteblockmaster.block.AdvancedNoteBlock.NOTE_STRING;

public class Tuner extends Item {
    public Tuner(Properties properties) {
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
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        if(player==null) return InteractionResult.FAIL;
        if(!(state.getBlock() instanceof AdvancedNoteBlock block)) return InteractionResult.PASS;
        if(!level.isClientSide) {

            if (stack.is(Registry.TEMPOTUNER)) {
                int val = state.getValue(AdvancedNoteBlock.SUBTICK);
                player.displayClientMessage(Component.literal(val / 10f + " ticks (" + val * SubTickScheduler.SUBTICK_LENGTH + " ms)").withColor(0xB0B0B0), true);
                return InteractionResult.SUCCESS;
            }
            if (stack.is(Registry.NOTETUNER)) {
                int old_val = AdvancedNoteBlock.getNoteValue(state);
                int new_val = player.isShiftKeyDown() ? block.changeNoteValueBy(old_val, 6) : block.changeNoteValueBy(old_val, 1);
                int _new = net.neoforged.neoforge.common.CommonHooks.onNoteChange(level, pos, state, old_val, new_val);

                if (_new == -1) return InteractionResult.FAIL;
                state = block.setNoteValue(state, _new);
                level.setBlock(pos, state, 3);
                block.playNote(player, state, level, pos);
                player.awardStat(Stats.TUNE_NOTEBLOCK);
                player.displayClientMessage(Component.literal(NOTE_STRING[_new]).withColor(0xB030B0), true);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
