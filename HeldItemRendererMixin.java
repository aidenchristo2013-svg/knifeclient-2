package com.example.knifeclient.mixin;

import com.example.knifeclient.Config;
import com.example.knifeclient.Knives;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** First person: CS:GO-style knife viewmodel, pull-out animations, and sprint sway. */
@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Shadow
    protected abstract void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickProgress, float pitch,
                                                  Hand hand, float swingProgress, ItemStack item, float equipProgress,
                                                  MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

    @Unique
    private static boolean knifeclient$recursing = false;
    @Unique
    private static int knifeclient$lastSlot = -1;
    @Unique
    private static int knifeclient$lastKnife = -1;
    @Unique
    private static long knifeclient$drawStartMs = 0L;
    @Unique
    private static float knifeclient$sprintLerp = 0.0f;

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
    private void knifeclient$knife(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand,
                                   float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
                                   VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (knifeclient$recursing) return;

        if (Config.knives && hand == Hand.MAIN_HAND && item.isEmpty() && !player.isInvisible()) {
            long now = System.currentTimeMillis();
            int currentSlot = player.getInventory().selectedSlot;
            int currentKnife = Math.floorMod(Config.knife, Knives.IDS.length);

            // Detect hotbar switch to empty hand or knife change in menu -> trigger CS:GO draw animation
            if (currentSlot != knifeclient$lastSlot || currentKnife != knifeclient$lastKnife) {
                knifeclient$lastSlot = currentSlot;
                knifeclient$lastKnife = currentKnife;
                knifeclient$drawStartMs = now;
            }

            // Smooth sprint transition
            float targetSprint = player.isSprinting() ? 1.0f : 0.0f;
            knifeclient$sprintLerp = MathHelper.lerp(0.12f, knifeclient$sprintLerp, targetSprint);

            matrices.push();

            // 1. CS:GO Tactical Viewmodel Position
            matrices.translate(0.06f, -0.05f, -0.08f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-10.0f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(12.0f));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-8.0f));

            // 2. CS:GO Running / Sprint Tilt & Side-to-Side Sway
            if (knifeclient$sprintLerp > 0.001f) {
                float runCycle = (now % 650L) / 650.0f * MathHelper.TAU;
                float swayX = MathHelper.sin(runCycle) * 0.045f * knifeclient$sprintLerp;
                float swayY = Math.abs(MathHelper.cos(runCycle)) * -0.03f * knifeclient$sprintLerp;

                matrices.translate(-0.08f * knifeclient$sprintLerp + swayX, -0.06f * knifeclient$sprintLerp + swayY, -0.05f * knifeclient$sprintLerp);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-28.0f * knifeclient$sprintLerp));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(22.0f * knifeclient$sprintLerp));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(18.0f * knifeclient$sprintLerp));
            }

            // 3. Unique CS:GO Pull-Out Animation per Knife (700ms duration)
            float drawElapsed = (now - knifeclient$drawStartMs) / 700.0f;
            if (drawElapsed >= 0.0f && drawElapsed < 1.0f) {
                float ease = 1.0f - (float) Math.pow(1.0f - drawElapsed, 3.0);
                float inv = 1.0f - ease;

                switch (currentKnife) {
                    case 0 -> { // Karambit: 360-degree finger-ring spin into reverse grip
                        matrices.translate(0.0f, -0.18f * inv, 0.08f * inv);
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-360.0f * inv));
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(35.0f * inv));
                    }
                    case 1 -> { // Bayonet: CS:GO wrist toss & catch flip
                        float arc = MathHelper.sin(drawElapsed * MathHelper.PI);
                        matrices.translate(-0.05f * arc, 0.14f * arc - 0.15f * inv, 0.0f);
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-360.0f * inv));
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(25.0f * arc));
                    }
                    case 2 -> { // Butterfly Knife: rapid double handle flip-open
                        float flip = MathHelper.sin(drawElapsed * MathHelper.PI * 2.0f);
                        matrices.translate(0.06f * flip, -0.12f * inv, 0.0f);
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(360.0f * inv));
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45.0f * flip));
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-30.0f * inv));
                    }
                    case 3 -> { // Huntsman Knife: heavy upward flick & wrist snap
                        float snap = MathHelper.sin(drawElapsed * MathHelper.PI);
                        matrices.translate(0.0f, -0.22f * inv, 0.10f * inv);
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(75.0f * inv - 15.0f * snap));
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-40.0f * inv));
                    }
                }
            }

            // Pass equipProgress = 0.0f so vanilla bob-up doesn't fight our custom CS:GO draw animation
            knifeclient$recursing = true;
            try {
                this.renderFirstPersonItem(player, tickProgress, pitch, hand, swingProgress, Knives.current(),
                        0.0f, matrices, vertexConsumers, light);
            } finally {
                knifeclient$recursing = false;
            }

            matrices.pop();
            ci.cancel();
        } else if (hand == Hand.MAIN_HAND) {
            // Reset slot tracker when holding a normal item so switching back triggers the pull-out again
            knifeclient$lastSlot = -1;
        }
    }
}
