package net.nayrus.noteblockmaster.setup;

import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;

public interface SpriteAccessor {
    SpriteSet getRegisteredSprite(ParticleType<?> type);
}
