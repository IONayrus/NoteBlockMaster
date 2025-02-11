package net.nayrus.noteblockmaster.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.block.AdvancedNoteBlock;
import net.nayrus.noteblockmaster.composer.SongCache;
import net.nayrus.noteblockmaster.composer.SongData;
import net.nayrus.noteblockmaster.network.data.ComposeData;
import net.nayrus.noteblockmaster.network.data.SongID;
import net.nayrus.noteblockmaster.screen.CompositionScreen;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.List;
import java.util.UUID;

public class ComposersNote extends Item {

    public ComposersNote(ResourceLocation key) {
        super(new Item.Properties()
                .stacksTo(1)
                .setId(ResourceKey.create(Registries.ITEM, key)));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(!((context.getPlayer()) instanceof Player player)) return InteractionResult.FAIL;
        if(player.getOffhandItem().getItem() instanceof TunerItem item){
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = level.getBlockState(pos);
            if(state.getBlock() instanceof AdvancedNoteBlock) return item.useOn(context, true);
            ItemStack tuner = player.getOffhandItem();
            Inventory inv = player.getInventory();
            if(player.isShiftKeyDown()) return InteractionResult.PASS;
            if(tuner.is(Registry.TEMPOTUNER) && inv.contains(stack -> stack.is(Items.REPEATER))){
                return placeRepeater(context, player, level, pos, inv);
            }
        }
        return InteractionResult.PASS;
    }

    private static InteractionResult placeRepeater(UseOnContext context, Player player, Level level, BlockPos pos, Inventory inv) {
        ItemStack composer = context.getItemInHand();
        ComposeData cData = ComposeData.getComposeData(composer);
        int target = cData.postDelay();
        int set = Math.min(target, 4);
        if(target>0) {
            target -= set;
            if(!level.isClientSide()){
                level.setBlock(pos.above(), Blocks.REPEATER.defaultBlockState()
                        .setValue(RepeaterBlock.DELAY, set).setValue(RepeaterBlock.FACING, context.getHorizontalDirection().getOpposite()), Block.UPDATE_ALL);
                composer.set(Registry.COMPOSE_DATA, new ComposeData(cData.beat(), cData.subtick(), target, cData.bpm(), cData.placed()));

                level.playSound(null, pos, SoundType.STONE.getPlaceSound(), SoundSource.BLOCKS, 1.0F, 0.8F);
                if (!player.isCreative()) Utils.removeItemsFromInventory(inv, Items.REPEATER, 1);
            }
            return InteractionResult.SUCCESS;
        }else{
            if(level.isClientSide()) Utils.playFailUse(level, player, pos);
            return InteractionResult.FAIL;
        }
    }

    public static Tuple<Integer, Integer> subtickAndPauseOnBeat(int beat, float bpm){
        float tPB = 60000 / bpm;
        int current_subtick = 0;
        int post_delay = 0;
        int lastTime = 0;
        for(int i = beat; i <= beat + 1; i++){
            int noteTimeMs = (int) (i * tPB);
            int subTickTime = noteTimeMs % 100;
            int redClockTime = noteTimeMs - subTickTime;
            int subtick = (subTickTime - (subTickTime % AdvancedNoteBlock.SUBTICK_LENGTH)) / AdvancedNoteBlock.SUBTICK_LENGTH;

            if(i == beat){
               current_subtick = subtick;
            }else{
                post_delay = ((redClockTime - lastTime) / 100);
            }
            lastTime = redClockTime;
        }
        return new Tuple<>(current_subtick, post_delay);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        ItemStack item = player.getItemInHand(usedHand);
        if(level.isClientSide()) openComposer(item);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(stack.has(Registry.SONG_ID)){
            if(!(stack.get(Registry.SONG_ID) instanceof SongID(UUID songID))) return;

            SongData data = SongCache.getSong(songID, stack);
            Component unknown = Component.literal("Unkown Song").withColor(Color.RED.getRGB());
            tooltipComponents.add(data!= null ? Component.literal(data.title()).withStyle(ChatFormatting.ITALIC) : unknown);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void openComposer(ItemStack item){
        Minecraft.getInstance().setScreen(new CompositionScreen(item));
    }
}
