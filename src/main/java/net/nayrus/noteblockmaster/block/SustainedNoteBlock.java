package net.nayrus.noteblockmaster.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SustainedNoteBlock extends AdvancedNoteBlock implements EntityBlock {

    public SustainedNoteBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SustainedBlockEntity(pos, state);
    }

//    SimpleSoundInstance simplesoundinstance = new SimpleSoundInstance(
//            soundEvent, source, volume, pitch, RandomSource.create(this.threadSafeRandom.nextLong()), x, y, z
//    );

}
