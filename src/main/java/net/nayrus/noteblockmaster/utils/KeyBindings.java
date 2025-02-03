package net.nayrus.noteblockmaster.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static final Lazy<KeyMapping> OPEN_OFFHAND_GUI = Lazy.of(() -> new KeyMapping(
            "key.noteblockmaster.openoffhandgui",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.noteblockmaster.nbm"
    ));

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_OFFHAND_GUI.get());
    }

}
