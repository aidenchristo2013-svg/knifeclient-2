package com.example.knifeclient.mixin;

import com.example.knifeclient.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Glow Skin: makes only YOUR player show the glowing outline, on your screen only. */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "isGlowing", at = @At("RETURN"), cancellable = true)
    private void knifeclient$glow(CallbackInfoReturnable<Boolean> cir) {
        if (Config.glow && (Object) this == MinecraftClient.getInstance().player) {
            cir.setReturnValue(true);
        }
    }
}
