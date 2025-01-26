package net.nayrus.noteblockmaster.setup.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue LOW_RESOLUTION_RENDER = BUILDER
            .comment(" Renders square rings & cones instead. Significantly less impact on FPS")
            .define("lowResolutionRender", false);

    public static final ModConfigSpec CLIENT = BUILDER.build();

}
