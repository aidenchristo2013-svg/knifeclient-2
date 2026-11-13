package com.example.knifeclient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ModuleScreen extends Screen {
    public ModuleScreen() {
        super(Text.literal("Knife Client"));
    }

    @Override
    protected void init() {
        int w = 200;
        int x = this.width / 2 - w / 2;
        int y = this.height / 4;

        addDrawableChild(toggle(x, y, w, "Glow Skin", () -> Config.glow, v -> Config.glow = v));
        addDrawableChild(toggle(x, y + 24, w, "Headless", () -> Config.headless, v -> Config.headless = v));
        addDrawableChild(toggle(x, y + 48, w, "Korblox Leg", () -> Config.korblox, v -> Config.korblox = v));
        addDrawableChild(toggle(x, y + 72, w, "Knives Instead of Fists", () -> Config.knives, v -> Config.knives = v));

        addDrawableChild(ButtonWidget.builder(knifeLabel(), b -> {
            Config.knife = (Config.knife + 1) % Knives.IDS.length;
            b.setMessage(knifeLabel());
            Config.save();
        }).dimensions(x, y + 96, w, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> this.close())
                .dimensions(x, y + 130, w, 20).build());
    }

    private static Text knifeLabel() {
        return Text.literal("Knife: " + Knives.NAMES[Math.floorMod(Config.knife, Knives.IDS.length)]);
    }

    private static Text label(String name, boolean on) {
        return Text.literal(name + ": " + (on ? "ON" : "OFF"));
    }

    private static ButtonWidget toggle(int x, int y, int w, String name, BooleanSupplier get, Consumer<Boolean> set) {
        return ButtonWidget.builder(label(name, get.getAsBoolean()), b -> {
            set.accept(!get.getAsBoolean());
            b.setMessage(label(name, get.getAsBoolean()));
            Config.save();
        }).dimensions(x, y, w, 20).build();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 4 - 20, 0xFFFFFFFF);
    }
}
