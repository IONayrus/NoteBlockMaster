package net.nayrus.noteblockmaster.item;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
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
import net.nayrus.noteblockmaster.network.data.ComposeData;
import net.nayrus.noteblockmaster.network.data.TunerData;
import net.nayrus.noteblockmaster.network.payload.ActionPing;
import net.nayrus.noteblockmaster.screen.NoteTunerScreen;
import net.nayrus.noteblockmaster.screen.TempoTunerScreen;
import net.nayrus.noteblockmaster.setup.Registry;
import org.jetbrains.annotations.NotNull;

public class TunerItem extends Item {
    public TunerItem() {
        super(new Item.Properties().stacksTo(1));
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

    public @NotNull InteractionResult useOn(UseOnContext context, boolean doOffHandSwing) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        ItemStack tuner = context.getItemInHand();
        if(player==null) return InteractionResult.FAIL;
        ItemStack composer = player.getOffhandItem();
        if(!tuner.is(this)){
            tuner = composer;
            composer = context.getItemInHand();
        }
        if(!(state.getBlock() instanceof AdvancedNoteBlock block)) return InteractionResult.PASS;
        if(!level.isClientSide && !player.isShiftKeyDown()) {
            TunerData data = getTunerData(tuner);
            if (tuner.is(Registry.TEMPOTUNER)) {
                int new_val;
                if(!composer.is(Registry.COMPOSER))
                    new_val = (data.setmode() ? data.value() : state.getValue(AdvancedNoteBlock.SUBTICK) + data.value()) % AdvancedNoteBlock.SUBTICKS;
                else{
                    ComposeData cData = ComposeData.getComposeData(composer);
                    new_val= cData.subtick();

                    Tuple<Integer, Integer> next = ComposersNote.subtickAndPauseOnBeat(cData.beat() + 1, cData.bpm());
                    composer.set(Registry.COMPOSE_DATA, new ComposeData(cData.beat() + 1, next.getA(), next.getB(), cData.bpm()));
                }
                if(!doOffHandSwing)
                    return block.onSubtickChange(level, player, state, pos, new_val, true);
                else{
                    ActionPing.sendActionPing((ServerPlayer) player, ActionPing.Action.SWING_OFFHAND);
                    block.onSubtickChange(level, player, state, pos, new_val, true);
                    return InteractionResult.CONSUME;
                }
            }
            if (tuner.is(Registry.NOTETUNER)) {
                int new_val = data.setmode() ? data.value() + AdvancedNoteBlock.MIN_NOTE_VAL : block.changeNoteValueBy(state, data.value());
                return block.onNoteChange(level, player, state, pos, new_val);
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return useOn(context, false);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        if(level.isClientSide()){
            if(itemstack.is(Registry.TEMPOTUNER))
                Minecraft.getInstance().setScreen(new TempoTunerScreen(itemstack, player.getItemInHand(InteractionHand.values()[(usedHand.ordinal() + 1) % 2])));
            if(itemstack.is(Registry.NOTETUNER))
                Minecraft.getInstance().setScreen(new NoteTunerScreen(itemstack));
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
    }
}
