package net.nayrus.noteblockmaster.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nayrus.noteblockmaster.item.TunerItem;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.utils.FinalTuple;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.neoforge.common.util.DeferredSoundType;

import java.util.ArrayList;
import java.util.List;

public class TuningCore extends TransparentBlock {

    public static final IntegerProperty VOLUME = IntegerProperty.create("volume",1,20);
    public static final IntegerProperty SUSTAIN = IntegerProperty.create("sustain",0,200);
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
                .setValue(VOLUME, 20)
                .setValue(SUSTAIN,0)
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VOLUME, SUSTAIN, POWERED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if(!(context instanceof EntityCollisionContext eCon) || eCon.equals(CollisionContext.empty())) return COLLISION;
        if(eCon.getEntity() instanceof Player player
                && FinalTuple.getHeldItems(player).contains(TunerItem.class)) return COLLISION;
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
        if(state.getValue(VOLUME) != 0) drops.add(new ItemStack(Registry.VOLUME.get()));
        if(state.getValue(SUSTAIN) != 0) drops.add(new ItemStack(Registry.SUSTAIN.get()));
        return drops;
    }

    public static boolean isSustaining(BlockState state){
        return state.getValue(SUSTAIN) != 0;
    }

    public static boolean isMuffling(BlockState state){
        return state.getValue(VOLUME) != 20;
    }

    //    SimpleSoundInstance simplesoundinstance = new SimpleSoundInstance(
//            soundEvent, source, volume, pitch, RandomSource.create(this.threadSafeRandom.nextLong()), x, y, z
//    );

    //TODO Make this a blockstate based addon for advanced noteblock instead
}
