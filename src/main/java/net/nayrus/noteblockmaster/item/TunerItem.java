package net.nayrus.noteblockmaster.item;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.network.payload.TunerData;
import net.nayrus.noteblockmaster.screen.NoteTunerScreen;
import net.nayrus.noteblockmaster.screen.TempoTunerScreen;
import net.nayrus.noteblockmaster.utils.Registry;
import org.jetbrains.annotations.NotNull;

public class TunerItem extends Item {
    public TunerItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCraftingRemainingItem(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return itemStack.copy();
    }

    public static @NotNull TunerData getTunerData(ItemStack stack){
        TunerData data = stack.get(Registry.TUNER_DATA);
        if(data == null) {
            data = new TunerData(1, false);
            stack.set(Registry.TUNER_DATA, data);
        }
        return data;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        if(player==null) return InteractionResult.FAIL;
        if(!(state.getBlock() instanceof AdvancedNoteBlock block)) return InteractionResult.PASS;
        if(!level.isClientSide && !player.isShiftKeyDown()) {
            TunerData data = getTunerData(stack);
            if (stack.is(Registry.TEMPOTUNER)) {
                int new_val = (data.setmode() ? data.value() : state.getValue(AdvancedNoteBlock.SUBTICK) + data.value()) % AdvancedNoteBlock.SUBTICKS;
                return block.onSubtickChange(level, player, state, pos, new_val, true);
            }
            if (stack.is(Registry.NOTETUNER)) {
                int new_val = data.setmode() ? data.value() + AdvancedNoteBlock.MIN_NOTE_VAL : block.changeNoteValueBy(state, data.value());
                return block.onNoteChange(level, player, state, pos, new_val);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        if(level.isClientSide()){
            if(itemstack.is(Registry.TEMPOTUNER))
                Minecraft.getInstance().setScreen(new TempoTunerScreen(itemstack));
            if(itemstack.is(Registry.NOTETUNER))
                Minecraft.getInstance().setScreen(new NoteTunerScreen(itemstack));
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
    }
}
