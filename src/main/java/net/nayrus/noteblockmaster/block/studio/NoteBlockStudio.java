package net.nayrus.noteblockmaster.block.studio;

import libs.felnull.fnnbs.NBS;
import net.nayrus.noteblockmaster.NoteBlockMaster;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NoteBlockStudio {

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
