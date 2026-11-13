package com.example.knifeclient.mixin;

import com.example.knifeclient.ModState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Marks when the local player is being drawn so Headless/Korblox only affect you. */
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    @Inject(method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V",
            at = @At("TAIL"))
    private void knifeclient$mark(AbstractClientPlayerEntity player, PlayerEntityRenderState state, float tickProgress, CallbackInfo ci) {
        ModState.nextIsSelf = player == MinecraftClient.getInstance().player;
        ModState.begin(((PlayerEntityRenderer) (Object) this).getModel());
    }
}
