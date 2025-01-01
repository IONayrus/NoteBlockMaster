package net.nayrus.noteblockmaster.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.nayrus.noteblockmaster.Config;
import net.nayrus.noteblockmaster.render.ANBInfoRender;
import net.nayrus.noteblockmaster.utils.Registry;
import net.nayrus.noteblockmaster.utils.NBMTags;
import net.nayrus.noteblockmaster.utils.SubTickScheduler;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.neoforge.registries.NewRegistryEvent;

import javax.annotation.Nullable;
import java.awt.*;

public class AdvancedNoteBlock extends Block
{

    public static int MAX_SUBTICKS;
    public static int SUBTICK_LENGTH;
    public static IntegerProperty SUBTICK;
    public static IntegerProperty NOTE;
    public static int MIN_NOTE_VAL;
    public static int MAX_NOTE_VAL;
    public static int TOTAL_NOTES;

    private final int defaultNoteValue;

    public AdvancedNoteBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition
                        .any()
                        .setValue(NoteBlock.INSTRUMENT, NoteBlockInstrument.HARP)
                        .setValue(NoteBlock.POWERED,false)
                        .setValue(SUBTICK, 0)
                        .setValue(NOTE, noteStringAsInt("F#3"))
        );
        defaultNoteValue = getNoteValue(this.defaultBlockState());
    }

    public static void loadPropertiesFromConfig(final NewRegistryEvent event){
        SUBTICK_LENGTH = Config.SUBTICK_LENGTH.get();
        MAX_SUBTICKS = (int) (100 / SUBTICK_LENGTH);
        SUBTICK = IntegerProperty.create("subtick",0,MAX_SUBTICKS - 1);
        MIN_NOTE_VAL = Config.LOWER_NOTE_LIMIT.get() instanceof String ? noteStringAsInt((String) Config.LOWER_NOTE_LIMIT.get()) : (int) Config.LOWER_NOTE_LIMIT.get();
        MAX_NOTE_VAL = Config.HIGHER_NOTE_LIMIT.get() instanceof String ? noteStringAsInt((String) Config.HIGHER_NOTE_LIMIT.get()) : (int) Config.HIGHER_NOTE_LIMIT.get();
        NOTE = IntegerProperty.create("note", MIN_NOTE_VAL, MAX_NOTE_VAL);

        TOTAL_NOTES = MAX_NOTE_VAL - MIN_NOTE_VAL;
    }

    private BlockState setInstrument(LevelAccessor level, BlockPos pos, BlockState state) {
        NoteBlockInstrument noteblockinstrument = level.getBlockState(pos.above()).instrument();
        if (noteblockinstrument.worksAboveNoteBlock()) {
            return state.setValue(NoteBlock.INSTRUMENT, noteblockinstrument);
        } else {
            NoteBlockInstrument noteblockinstrument1 = level.getBlockState(pos.below()).instrument();
            NoteBlockInstrument noteblockinstrument2 = noteblockinstrument1.worksAboveNoteBlock() ? NoteBlockInstrument.HARP : noteblockinstrument1;
            return state.setValue(NoteBlock.INSTRUMENT, noteblockinstrument2);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if(player.getWeaponItem().is(NBMTags.Items.TUNERS)){
            if(!level.isClientSide())
                attack(state, level, pos, player);
            return false;
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return player.getWeaponItem().is(NBMTags.Items.TUNERS) ? 0.0f : super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!stack.is(NBMTags.Items.TUNERS)){
            return stack.is(ItemTags.NOTE_BLOCK_TOP_INSTRUMENTS) && hitResult.getDirection() == Direction.UP
                    ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
                    : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        InteractionResult result = stack.getItem().useOn(new UseOnContext(level, player, hand, stack, hitResult));
        switch(result){
            case SUCCESS -> { return ItemInteractionResult.SUCCESS;}
            case PASS -> { return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;}
            default -> { return ItemInteractionResult.FAIL;}
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            int val = getNoteValue(state);
            int try_new = ((val+1) >= (defaultNoteValue + 25) || (val+1) < defaultNoteValue) ? defaultNoteValue : val+1;
            int _new = net.neoforged.neoforge.common.CommonHooks.onNoteChange(level, pos, state, val, try_new);
            if (_new == -1) return InteractionResult.FAIL;
            state = setNoteValue(state, _new);
            level.setBlock(pos, state, 3);
            this.playNote(player, state, level, pos);
            player.awardStat(Stats.TUNE_NOTEBLOCK);
            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        boolean flag = level.hasNeighborSignal(pos);
        if (flag != state.getValue(NoteBlock.POWERED)) {
            if (flag) {
                this.playNote(null, state, level, pos);
            }

            level.setBlock(pos, state.setValue(NoteBlock.POWERED, Boolean.valueOf(flag)), 3);
        }
    }

    public void playNote(@Nullable Entity entity, BlockState state, Level level, BlockPos pos) {
        if (state.getValue(NoteBlock.INSTRUMENT).worksAboveNoteBlock() || level.getBlockState(pos.above()).isAir()) {
            level.blockEvent(pos, this, 0, 0);
            level.gameEvent(entity, GameEvent.NOTE_BLOCK_PLAY, pos);
        }
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            ItemStack item = player.getWeaponItem();
            if(!item.is(NBMTags.Items.TUNERS)) {
                this.playNote(player, state, level, pos);
                player.awardStat(Stats.PLAY_NOTEBLOCK);
            }
            else{
                if(item.is(Registry.TEMPOTUNER)){
                    int state_val = state.getValue(SUBTICK);
                    int new_val = (player.isShiftKeyDown() ? state_val + 5 : state_val + 1) % MAX_SUBTICKS;

                    level.setBlock(pos, state.setValue(SUBTICK, new_val), Block.UPDATE_ALL);

                    player.displayClientMessage(Component.literal(new_val/10f + " ticks ("+new_val * SUBTICK_LENGTH+" ms)")
                            .withColor(AdvancedNoteBlock.getColor(state, ANBInfoRender.PROPERTY.TEMPO).getRGB()), true);
                } else
                if(item.is(Registry.NOTETUNER)){
                    player.displayClientMessage(Component.literal(Utils.NOTE_STRING[getNoteValue(state)])
                            .withColor(AdvancedNoteBlock.getColor(state, ANBInfoRender.PROPERTY.NOTE).getRGB()), true);
                    this.playNote(player, state, level, pos);
                    player.awardStat(Stats.PLAY_NOTEBLOCK);
                }
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.setInstrument(context.getLevel(), context.getClickedPos(), this.defaultBlockState());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        boolean flag = facing.getAxis() == Direction.Axis.Y;
        return flag ? this.setInstrument(level, currentPos, state) : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NoteBlock.INSTRUMENT, NoteBlock.POWERED, SUBTICK, NOTE);
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        net.neoforged.neoforge.event.level.NoteBlockEvent.Play e = new net.neoforged.neoforge.event.level.NoteBlockEvent.Play(level, pos, state, getNoteValue(state), state.getValue(NoteBlock.INSTRUMENT));
        if (net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(e).isCanceled()) return false;
        SubTickScheduler.delayedNoteBlockEvent(state, level, pos);
        return true;
    }

    public static int noteStringAsInt(String note){
        try{
            return Integer.parseInt(note);
        } catch (NumberFormatException e) {
            note = note.toUpperCase();
            if(note.length() > 3 || note.length() < 2) throw new IllegalArgumentException();
            int val = note.length() == 3 ? 1 : 0;
            switch(note.charAt(0)){
                case 'B': val ++;
                case 'A': val += 2;
                case 'G': val += 2;
                case 'F': val ++;
                case 'E': val += 2;
                case 'D': val += 2;
                case 'C': break;
                default: throw new IllegalArgumentException();
            }
            val += ((Character.getNumericValue(note.charAt(note.length()-1)) - 1) * 12);
            if(val < 0 || val >= Utils.NOTE_STRING.length) throw new IllegalArgumentException();
            return val;
        }
    }

    public static int getNoteValue(BlockState state){
        return state.getValue(NOTE);
    }

    public BlockState setNoteValue(BlockState state, int value){
        value %= Utils.NOTE_STRING.length;
        return state.setValue(NOTE, value);
    }

    public static float getPitchFromNote(int note) {
        return (float)Math.pow(2.0, (double)(note - 42) / 12.0);
    }

    public static Color getColor(BlockState state, ANBInfoRender.PROPERTY info){
        float rgbVal = switch (info){
            case NOTE -> (AdvancedNoteBlock.getNoteValue(state) - 2) / 29.0F ;
            case TEMPO -> state.getValue(AdvancedNoteBlock.SUBTICK) / (AdvancedNoteBlock.MAX_SUBTICKS - 1.0F);
        };
        float rCol = Math.max(0.0F, Mth.sin((rgbVal + 0.0F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);
        float gCol = Math.max(0.0F, Mth.sin((rgbVal + 0.33333334F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);
        float bCol = Math.max(0.0F, Mth.sin((rgbVal + 0.6666667F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);
        return new Color(rCol, gCol, bCol);
    }

    public int changeNoteValueBy(int note, int value){
        int _new = (note + value);
        if(_new >= note) return (_new <= MAX_NOTE_VAL) ? _new : ((_new % MAX_NOTE_VAL) - 1 + MIN_NOTE_VAL);
        else return (_new >= MIN_NOTE_VAL) ? _new : (MAX_NOTE_VAL - (MIN_NOTE_VAL % _new) + 1);
    }

}
