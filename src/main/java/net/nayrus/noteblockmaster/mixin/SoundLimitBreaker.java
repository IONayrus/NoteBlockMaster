package net.nayrus.noteblockmaster.mixin;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public abstract class SoundLimitBreaker {
    @Inject(method = "calculatePitch", at = @At("HEAD"), cancellable = true)
    private void breakSoundLimit(SoundInstance sound, CallbackInfoReturnable<Float> cir) {
        float limit = sound.getSource() == SoundSource.RECORDS ? 8 : 1;
        cir.setReturnValue(Mth.clamp(sound.getPitch(), 0.5F / limit, 2.0F * limit)); // Override the return value
    }
}
