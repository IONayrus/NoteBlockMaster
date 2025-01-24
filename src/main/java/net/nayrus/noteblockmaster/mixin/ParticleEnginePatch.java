package net.nayrus.noteblockmaster.mixin;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.nayrus.noteblockmaster.setup.SpriteAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(ParticleEngine.class)
public abstract class ParticleEnginePatch implements SpriteAccessor {

    @Shadow @Final private Map<ResourceLocation, ?> spriteSets;

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public SpriteSet getRegisteredSprite(ParticleType<?> type) {
        return (SpriteSet) this.spriteSets.get(BuiltInRegistries.PARTICLE_TYPE.getKey(type));
    }
}