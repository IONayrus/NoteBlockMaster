package net.nayrus.noteblockmaster.block.composer;

import libs.felnull.fnnbs.NBS;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.nayrus.noteblockmaster.NoteBlockMaster;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ComposerBlock extends Block {

    public ComposerBlock(ResourceLocation key) {
        super(Block.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, key)));
    }

    public static NBS loadNBSFile(String name){
        Path filePath = NoteBlockMaster.SONG_DIR.resolve(name+ ".nbs");
        if (Files.exists(filePath)) {
            try {
                return NBS.load(Files.newInputStream(filePath));
            } catch (IOException e) {
                NoteBlockMaster.LOGGER.error(e.getLocalizedMessage());
            }
        } else {
            NoteBlockMaster.LOGGER.warn("File not found: {}", filePath.toAbsolutePath());
        }
        return null;
    }
}
