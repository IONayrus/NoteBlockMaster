package net.nayrus.noteblockmaster.render.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;

public interface IInternalParticleAccessor {
    Particle nbm$addInternalParticle(ParticleOptions options,
                                     boolean force,
                                     double x,
                                     double y,
                                     double z,
                                     double xSpeed,
                                     double ySpeed,
                                     double zSpeed);
}
