package com.example.knifeclient;

import net.fabricmc.loader.api.FabricLoader;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class Config {
    public static boolean glow = false;
    public static boolean headless = false;
    public static boolean korblox = false;
    public static boolean knives = true;
    public static int knife = 0;

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("knifeclient.properties");
    }

    public static void load() {
        try {
            if (!Files.exists(path())) return;
            Properties p = new Properties();
            try (InputStream in = Files.newInputStream(path())) { p.load(in); }
            glow = Boolean.parseBoolean(p.getProperty("glow", "false"));
            headless = Boolean.parseBoolean(p.getProperty("headless", "false"));
            korblox = Boolean.parseBoolean(p.getProperty("korblox", "false"));
            knives = Boolean.parseBoolean(p.getProperty("knives", "true"));
            knife = Integer.parseInt(p.getProperty("knife", "0"));
        } catch (Exception ignored) { }
    }

    public static void save() {
        try {
            Properties p = new Properties();
            p.setProperty("glow", String.valueOf(glow));
            p.setProperty("headless", String.valueOf(headless));
            p.setProperty("korblox", String.valueOf(korblox));
            p.setProperty("knives", String.valueOf(knives));
            p.setProperty("knife", String.valueOf(knife));
            try (OutputStream out = Files.newOutputStream(path())) { p.store(out, "Knife Client"); }
        } catch (Exception ignored) { }
    }
}
