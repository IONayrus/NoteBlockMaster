package net.nayrus.noteblockmaster.render.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;

public class SustainedNoteParticle extends TextureSheetParticle {

    protected SustainedNoteParticle(ClientLevel level, double x, double y, double z, double color, int lifetimeTicks) {
        super(level, x, y, z, 0.0, 0.05, 0.0);
        this.friction = Math.min(0.9F + (float)(0.1F/ (2000.0/(Math.pow(lifetimeTicks, 1.5)))), 1.0F);
        this.speedUpWhenYMotionIsBlocked = true;
        this.xd *= 0.005F;
        this.yd *= 0.01F;
        this.zd *= 0.005F;
        this.yd += 0.05 / (lifetimeTicks > 20 ? (lifetimeTicks / 16.0) : (lifetimeTicks / (10.0 + 10.0 * lifetimeTicks / 16)));
        this.rCol = Math.max(0.0F, Mth.sin(((float)color + 0.0F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);
        this.gCol = Math.max(0.0F, Mth.sin(((float)color + 0.33333334F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);
        this.bCol = Math.max(0.0F, Mth.sin(((float)color + 0.6666667F) * (float) (Math.PI * 2)) * 0.65F + 0.35F);
        this.quadSize *= 1.5F;
        this.lifetime = lifetimeTicks;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        return this.quadSize * Mth.clamp(((float)this.age + scaleFactor) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    public static class Provider  implements ParticleProvider<SustainedNoteOptions> {

        private final SpriteSet sprite;

        public Provider(SpriteSet sprites) {
            this.sprite = sprites;
        }

        @Override
        public Particle createParticle(SustainedNoteOptions type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SustainedNoteParticle noteparticle = new SustainedNoteParticle(level, x, y, z, xSpeed, type.duration() / 50);
            noteparticle.pickSprite(this.sprite);
            return noteparticle;
        }
    }
}
