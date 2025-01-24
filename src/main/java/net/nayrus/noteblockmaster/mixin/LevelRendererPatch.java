package net.nayrus.noteblockmaster.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.ParticleOptions;
import net.nayrus.noteblockmaster.render.particle.IInternalParticleAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererPatch implements IInternalParticleAccessor {


    @Shadow @Nullable protected abstract Particle addParticleInternal(ParticleOptions options, boolean force, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);

    @Override
    public Particle nbm$addInternalParticle(ParticleOptions options, boolean force, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return this.addParticleInternal(options, force, x, y, z, xSpeed, ySpeed, zSpeed);
    }
}
