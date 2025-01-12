package net.nayrus.noteblockmaster.item;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.network.data.ComposeData;
import net.nayrus.noteblockmaster.screen.ComposerScreen;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class ComposersNote extends Item {

    public ComposersNote() {
        super(new Item.Properties()
                .stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if(!((context.getPlayer()) instanceof Player player)) return InteractionResult.FAIL;
        if(player.getOffhandItem().getItem() instanceof TunerItem item){
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = level.getBlockState(pos);
            if(state.getBlock() instanceof AdvancedNoteBlock) return item.useOn(context, true);
            ItemStack tuner = player.getOffhandItem();
            Inventory inv = player.getInventory();
            if(tuner.is(Registry.TEMPOTUNER) && inv.contains(stack -> stack.is(Items.REPEATER))){
                ItemStack composer = context.getItemInHand();
                ComposeData cData = ComposeData.getComposeData(composer);
                int target = cData.preDelay();
                int set = Math.min(target, 4);
                if(target>0) {
                    target -= set;
                    if(!level.isClientSide()){
                        level.setBlock(pos.above(), Blocks.REPEATER.defaultBlockState()
                                .setValue(RepeaterBlock.DELAY, set).setValue(RepeaterBlock.FACING, context.getHorizontalDirection().getOpposite()), Block.UPDATE_ALL);
                        composer.set(Registry.COMPOSE_DATA, new ComposeData(cData.beat(), cData.subtick(), target, cData.bpm()));

                        level.playSound(null, pos, SoundType.STONE.getPlaceSound(), SoundSource.BLOCKS, 1.0F, 0.8F);
                        if (!player.isCreative()) Utils.removeItemsFromInventory(inv, Items.REPEATER, 1);
                    }
                    return InteractionResult.SUCCESS;
                }else{
                    if(level.isClientSide()) Utils.playFailUse(level, player, pos);
                    return InteractionResult.FAIL;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public static Tuple<Integer, Integer> subtickAndPauseOnBeat(int beat, float bpm){
        float tPB = 60000 / bpm;
        int current_subtick = 0;
        int pre_delay = 0;
        int lastTime = 0;
        for(int i = (beat - 1); i <= beat; i++){
            int noteTimeMs = (int) (i * tPB);
            int subTickTime = noteTimeMs % 100;
            int redClockTime = noteTimeMs - subTickTime;
            int subtick = (subTickTime - (subTickTime % AdvancedNoteBlock.SUBTICK_LENGTH)) / AdvancedNoteBlock.SUBTICK_LENGTH;

            if(i == beat){
               current_subtick = subtick;
               pre_delay = ((redClockTime - lastTime) / 100);
            }
            lastTime = redClockTime;
        }
        return new Tuple<>(current_subtick, pre_delay);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack item = player.getItemInHand(usedHand);
        if(level.isClientSide()){
            Minecraft.getInstance().setScreen(new ComposerScreen(item));
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, item);
    }
}
