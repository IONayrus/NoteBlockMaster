package net.nayrus.noteblockmaster.block;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nayrus.noteblockmaster.item.SpinningCore;
import net.nayrus.noteblockmaster.item.TunerItem;
import net.nayrus.noteblockmaster.screen.CoreScreen;
import net.nayrus.noteblockmaster.setup.NBMTags;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.utils.FinalTuple;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.neoforge.common.util.DeferredSoundType;

import java.util.ArrayList;
import java.util.List;

public class TuningCore extends TransparentBlock {

    public static int SUSTAIN_MAXVAL = 1;
    public static final IntegerProperty VOLUME = IntegerProperty.create("volume",0,20);
    public static IntegerProperty SUSTAIN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DeferredSoundType CORE_SOUNDS = new DeferredSoundType(1.0F,1.0F,
            () -> SoundEvents.ENDER_EYE_DEATH , () -> SoundEvents.WOOD_STEP, () -> SoundEvents.ENDER_EYE_LAUNCH, () -> SoundEvents.WOOL_HIT, () -> SoundEvents.WOOD_FALL);
    public static final VoxelShape COLLISION = Block.box(3.0,0.0,3.0,13.0,10.0,13.0);

    public TuningCore() {
        super(BlockBehaviour.Properties.of()
                .instabreak()
                .noCollission()
                .noOcclusion()
                .sound(CORE_SOUNDS)
                .isValidSpawn(Blocks::never)
                .isSuffocating((a,b,c) -> false)
                .isViewBlocking((a,b,c) -> false)
                .dynamicShape());

        this.registerDefaultState(this.getStateDefinition()
                .any()
                .setValue(VOLUME, 0)
                .setValue(SUSTAIN,0)
                .setValue(POWERED, false));
    }

    public static void loadSustainProperty(){
        SUSTAIN = IntegerProperty.create("sustain",0, SUSTAIN_MAXVAL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VOLUME, SUSTAIN, POWERED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if(!(context instanceof EntityCollisionContext eCon) || eCon.equals(CollisionContext.empty())) return COLLISION;
        if(eCon.getEntity() instanceof Player player
                && FinalTuple.getHeldItems(player).contains(TunerItem.class, SpinningCore.class)) return COLLISION;
        return Shapes.empty();
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        if(level.getChunk(pos).getLevel() instanceof ServerLevel l)
            Utils.scheduleTick(l, pos, this, 1);
        super.onNeighborChange(state, level, pos, neighbor);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(!level.getBlockState(pos.below()).is(Registry.ADVANCED_NOTEBLOCK))
            level.destroyBlock(pos, true);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = new ArrayList<>();
        if(isMixing(state)) drops.add(new ItemStack(Registry.VOLUME.get()));
        if(isSustaining(state)) drops.add(new ItemStack(Registry.SUSTAIN.get()));
        return drops;
    }

    public static boolean isSustaining(BlockState state){
        return getSustain(state) != 0;
    }

    public static boolean isMixing(BlockState state){
        return getVolume(state) != 0;
    }

    public static int getSustain(BlockState state){
        return state.getValue(SUSTAIN);
    }

    public static int getVolume(BlockState state){
        return state.getValue(VOLUME);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!(stack.is(NBMTags.Items.TUNERS) || (stack.is(NBMTags.Items.CORES)))) return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        BlockState anb = level.getBlockState(pos.below());
        if(!anb.is(Registry.ADVANCED_NOTEBLOCK)){
            if(!level.isClientSide()) level.scheduleTick(pos, state.getBlock(), 0);
            return ItemInteractionResult.SUCCESS;
        }
        if(level.isClientSide()){
            Minecraft.getInstance().setScreen(new CoreScreen(state, pos, anb.getValue(AdvancedNoteBlock.INSTRUMENT),
                    AdvancedNoteBlock.getPitchFromNote(AdvancedNoteBlock.getNoteValue(anb))));
        }
        return ItemInteractionResult.SUCCESS;
    }
}
