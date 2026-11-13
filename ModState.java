package com.example.knifeclient;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

/** Tracks which model parts to hide while the local player is being drawn. */
public final class ModState {
    public static boolean nextIsSelf = false;
    public static boolean renderingSelf = false;
    private static ModelPart head, hat, rightLeg, rightPants;

    public static void begin(PlayerEntityModel model) {
        renderingSelf = nextIsSelf;
        head = Config.headless ? model.head : null;
        hat = Config.headless ? model.hat : null;
        rightLeg = Config.korblox ? model.rightLeg : null;
        rightPants = Config.korblox ? model.rightPants : null;
    }

    public static void end() {
        renderingSelf = false;
        head = hat = rightLeg = rightPants = null;
    }

    public static boolean isHidden(ModelPart part) {
        return part == head || part == hat || part == rightLeg || part == rightPants;
    }
}
