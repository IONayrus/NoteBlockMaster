package net.nayrus.noteblockmaster.block;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nayrus.noteblockmaster.item.TunerItem;
import net.nayrus.noteblockmaster.screen.CoreScreen;
import net.nayrus.noteblockmaster.setup.NBMTags;
import net.nayrus.noteblockmaster.setup.Registry;
import net.nayrus.noteblockmaster.utils.FinalTuple;
import net.nayrus.noteblockmaster.utils.Utils;
import net.neoforged.neoforge.common.util.DeferredSoundType;

import java.util.ArrayList;
import java.util.List;

public class TuningCore extends TransparentBlock{

    public static int SUSTAIN_MAXVAL = 1;
    public static final IntegerProperty VOLUME = IntegerProperty.create("volume",0,20);
    public static IntegerProperty SUSTAIN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DeferredSoundType CORE_SOUNDS = new DeferredSoundType(1.0F,1.0F,
            () -> SoundEvents.ENDER_EYE_DEATH , () -> SoundEvents.WOOD_STEP, () -> SoundEvents.ENDER_EYE_LAUNCH, () -> SoundEvents.WOOL_HIT, () -> SoundEvents.WOOD_FALL);
    public static final VoxelShape COLLISION = Block.box(3.0,0.0,3.0,13.0,10.0,13.0);

    public TuningCore() {
        super(BlockBehaviour.Properties.of()
                .destroyTime(0.04F) //TODO maybe try this instabreak without breaking drops
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
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VOLUME, SUSTAIN, POWERED);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        ItemStack weapon = player.getWeaponItem();
        if(!weapon.is(NBMTags.Items.CORE_DESTROY)) return state;
        if(hasStackEnoughIronToDestroy(weapon, state)) return super.playerWillDestroy(level, pos, state, player);
        if(weapon.is(NBMTags.Items.TUNERS) && hasPlayerEnoughIronToDestroy(player, state)) return super.playerWillDestroy(level, pos, state, player);
        return state;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        ItemStack weapon = player.getWeaponItem();
        if(!weapon.is(NBMTags.Items.CORE_DESTROY)) return false;
        if(weapon.is(Items.IRON_NUGGET)){
            if(hasStackEnoughIronToDestroy(weapon, state)) return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
            if(player.getInventory().countItem(Items.IRON_NUGGET) > 0) {
                if (!level.isClientSide()) removeOneCore(state, level, pos, player, false);
                return false;
            }
            return false;
        }
        if(weapon.is(NBMTags.Items.TUNERS)) {
            if(hasPlayerEnoughIronToDestroy(player, state)) return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
            if(player.getInventory().countItem(Items.IRON_NUGGET) > 0){
                if(!level.isClientSide()) removeOneCore(state, level, pos, player, false);
                return false;
            }
            return false;
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    protected boolean hasPlayerEnoughIronToDestroy(Player player, BlockState state){
        return player.getInventory().contains(item ->{
                if(!item.is(Items.IRON_NUGGET)) return false;
                if(isMixing(state) && isSustaining(state)) return item.getCount() >= 2;
                return item.getCount() >= 1;
        });
    }

    protected boolean hasStackEnoughIronToDestroy(ItemStack item, BlockState state){
        if(!item.is(Items.IRON_NUGGET)) return false;
        if(item.getCount() >= 2) return true;
        return !(isMixing(state) && isSustaining(state)) && item.getCount() == 1;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if(!(context instanceof EntityCollisionContext eCon) || eCon.equals(CollisionContext.empty())) return COLLISION;
        if(eCon.getEntity() instanceof Player player) {
            FinalTuple.ItemStackTuple items = FinalTuple.getHeldItems(player);
            if (items.contains(TunerItem.class) || items.contains(Items.IRON_NUGGET)) return COLLISION;
        }
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
        if(!level.getBlockState(pos.below()).is(Registry.ADVANCED_NOTEBLOCK)){
            if(!(level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 8, true) instanceof Player player)) level.destroyBlock(pos, false);
            else level.destroyBlock(pos, true, player);
        }
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        final List<ItemStack> drops = new ArrayList<>();
        final List<ItemStack> cores = List.of(new ItemStack(Registry.VOLUME.get()), new ItemStack(Registry.SUSTAIN.get()));
        boolean mixing = isMixing(state);
        boolean sustaining = isSustaining(state);
        if(params.getOptionalParameter(LootContextParams.THIS_ENTITY) instanceof Player player){
            Inventory inv = player.getInventory();
            int nugCount = inv.countItem(Items.IRON_NUGGET);
            if((mixing && sustaining) && nugCount >= 2) drops.addAll(cores);
            else if(nugCount >= 1) drops.add(mixing ? cores.getFirst() : cores.getLast());
            Utils.removeItemsFromInventory(inv, Items.IRON_NUGGET, drops.size());
        }else {
            if (mixing) drops.add(cores.getFirst());
            if (sustaining) drops.add(cores.getLast());
        }
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
        if(stack.is(Items.IRON_NUGGET)){
            if (level.isClientSide()) return ItemInteractionResult.SUCCESS;
            if(stack.getCount() >= 2) level.destroyBlock(pos, !player.isCreative(), player);
            else if(!(isSustaining(state) && isMixing(state))) level.destroyBlock(pos, !player.isCreative(), player); //Here I know its only 1 nugget
            else removeOneCore(state, level, pos,  player, hand == InteractionHand.MAIN_HAND);
            return ItemInteractionResult.SUCCESS;
        }
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

    protected void removeOneCore(BlockState state, Level level, BlockPos pos, Player player, boolean sustainFirst){
        if(level.isClientSide()) return;
        boolean sus = isSustaining(state) && sustainFirst;
        level.setBlock(pos, state.setValue(sus ? SUSTAIN : VOLUME, 0), Block.UPDATE_ALL_IMMEDIATE);
        if(!player.isCreative()){
            Block.popResource(level, pos, sus ? new ItemStack(Registry.SUSTAIN.get()) : new ItemStack(Registry.VOLUME.get()));
            level.playSound(null, pos, CORE_SOUNDS.getBreakSound(), SoundSource.BLOCKS, 1,0.8F);
            Utils.removeItemsFromInventory(player.getInventory(), Items.IRON_NUGGET, 1);
        }
    }
}
