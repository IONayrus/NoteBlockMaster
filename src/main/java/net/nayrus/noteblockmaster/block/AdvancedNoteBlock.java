package net.nayrus.noteblockmaster.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
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
import net.nayrus.noteblockmaster.util.Registry;
import net.nayrus.noteblockmaster.util.NBMTags;
import net.nayrus.noteblockmaster.util.SubTickScheduler;

import javax.annotation.Nullable;

public class AdvancedNoteBlock extends Block
{

    public static final int MAX_SUBTICKS = (int) (100 / SubTickScheduler.SUBTICK_LENGTH);
    public static final IntegerProperty SUBTICK = IntegerProperty.create("subtick",0,MAX_SUBTICKS-1);
    public static final IntegerProperty OCTAVE = IntegerProperty.create("octave",0,6);
    public static final IntegerProperty KEY = IntegerProperty.create("key",0,11);
    public static final String[] NOTE_STRING = {"C1", "C#1", "D1", "D#1", "E1", "F1", "F#1", "G1", "G#1", "A1", "A#1", "B1",
                                                "C2", "C#2", "D2", "D#2", "E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2",
                                                "C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3",
                                                "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4",
                                                "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5",
                                                "C6", "C#6", "D6", "D#6", "E6", "F6", "F#6", "G6", "G#6", "A6", "A#6", "B6",
                                                "C7", "C#7", "D7", "D#7", "E7", "F7", "F#7", "G7", "G#7", "A7", "A#7", "B7"};

    private final int defaultNoteValue;
    private final int minNoteVal;
    private final int maxNoteVal;

    public AdvancedNoteBlock(Properties properties, int minNote, int maxNote) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition
                        .any()
                        .setValue(NoteBlock.INSTRUMENT, NoteBlockInstrument.HARP)
                        .setValue(NoteBlock.POWERED,false)
                        .setValue(SUBTICK, 0)
                        .setValue(OCTAVE, 2)
                        .setValue(KEY, 6)
        );
        defaultNoteValue = getNoteValue(this.defaultBlockState());
        this.minNoteVal = Math.max(minNote, 0);
        this.maxNoteVal = Math.min(maxNote, NOTE_STRING.length - 2); //B7 is out of range, A#7 is out of tune but ok
    }

    public AdvancedNoteBlock(Properties properties){
        this(properties, 0, NOTE_STRING.length );
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

                    player.displayClientMessage(Component.literal(new_val/10f + " ticks ("+new_val * SubTickScheduler.SUBTICK_LENGTH+" ms)").withColor(0xB0B0B0), true);
                } else
                if(item.is(Registry.NOTETUNER)){
                    player.displayClientMessage(Component.literal(NOTE_STRING[getNoteValue(state)]).withColor(0xB030B0), true);
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
        builder.add(NoteBlock.INSTRUMENT, NoteBlock.POWERED, SUBTICK, OCTAVE, KEY);
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        SubTickScheduler.delayedNoteBlockEvent(state, level, pos, id, param);
        return true;
    }

    public static int noteStringAsInt(String note){
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
        }
        val += ((Character.getNumericValue(note.charAt(note.length()-1)) - 1) * 12);
        if(val < 0 || val >= NOTE_STRING.length) throw new IllegalArgumentException();
        return val;
    }

    public static int getNoteValue(BlockState state){
        return state.getValue(OCTAVE) * 12 + state.getValue(KEY);
    }

    public BlockState setNoteValue(BlockState state, int value){
        value %= NOTE_STRING.length;
        return state.setValue(OCTAVE, value / 12).setValue(KEY, value % 12);
    }

    public static float getPitchFromNote(int note) {
        return (float)Math.pow(2.0, (double)(note - 42) / 12.0);
    }

    public int changeNoteValueBy(int note, int value){
        int _new = (note + value);
        if(_new >= note) return (_new <= maxNoteVal) ? _new : ((_new % maxNoteVal) + minNoteVal);
        else return (_new >= minNoteVal) ? _new : (maxNoteVal - (minNoteVal % _new));
    }

}
