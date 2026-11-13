package com.example.knifeclient.mixin;

import com.example.knifeclient.ModState;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Skips drawing the head/hat/right leg of the local player when those modules are on. */
@Mixin(ModelPart.class)
public abstract class ModelPartMixin {
    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V",
            at = @At("HEAD"), cancellable = true)
    private void knifeclient$hide(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color, CallbackInfo ci) {
        if (ModState.renderingSelf && ModState.isHidden((ModelPart) (Object) this)) {
            ci.cancel();
        }
    }
}
