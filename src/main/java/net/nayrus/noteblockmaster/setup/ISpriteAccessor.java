package net.nayrus.noteblockmaster.setup;

import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;

public interface ISpriteAccessor {
    SpriteSet nbm$getRegisteredSprite(ParticleType<?> type);
}
