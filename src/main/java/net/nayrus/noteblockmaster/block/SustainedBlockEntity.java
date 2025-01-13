package net.nayrus.noteblockmaster.block;

import com.mojang.datafixers.types.Type;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nayrus.noteblockmaster.network.data.SustainData;
import net.nayrus.noteblockmaster.setup.Registry;

public class SustainedBlockEntity extends BlockEntity {

    public static final Type<?> TYPE = Util.fetchChoiceType(References.BLOCK_ENTITY, "sustained_noteblock_entity");

    private byte durationTicks;

    public SustainedBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registry.SUSTAINED_NOTEBLOCK_ENTITY.get(), pos, blockState);
        PatchedDataComponentMap map = new PatchedDataComponentMap(this.components());
        map.set(Registry.SUSTAIN_DATA.get(), new SustainData((byte) 100));
        this.applyComponents(this.components(), map.asPatch());
    }



//    @Override
//    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
//        super.loadAdditional(tag, registries);
//        this.durationTicks = tag.getByte("duration");
//    }

//    @Override
//    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
//        super.saveAdditional(tag, registries);
//        tag.putByte("duration", this.durationTicks);
//    }

    public byte getDurationTicks() {
        return this.durationTicks;
    }

    public void setDurationTicks(byte durationTicks) {
        this.durationTicks = durationTicks;
    }
}
