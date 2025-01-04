package net.nayrus.noteblockmaster.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.utils.Registry;
import net.nayrus.noteblockmaster.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class TunerItem extends Item {
    public TunerItem(Properties properties) {
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
                int state_val = state.getValue(AdvancedNoteBlock.SUBTICK);
                int new_val = (player.isShiftKeyDown() ? state_val + 5 : state_val + 1) % AdvancedNoteBlock.MAX_SUBTICKS;

                level.setBlock(pos, state.setValue(AdvancedNoteBlock.SUBTICK, new_val), Block.UPDATE_ALL);
                player.displayClientMessage(Component.literal( "("+new_val * AdvancedNoteBlock.SUBTICK_LENGTH+" ms)")
                        .withColor(block.getColor(state, Utils.PROPERTY.TEMPO).getRGB()), true);
                return InteractionResult.SUCCESS;
            }
            if (stack.is(Registry.NOTETUNER)) {
                int new_val = player.isShiftKeyDown() ? block.changeNoteValueBy(state, 6) : block.changeNoteValueBy(state, 1);
                return block.onNoteChange(level, player, state, pos, new_val);
            }
        }
        return InteractionResult.PASS;
    }
}
