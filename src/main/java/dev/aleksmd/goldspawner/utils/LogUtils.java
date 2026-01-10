package dev.aleksmd.goldspawner.utils;

import dev.aleksmd.goldspawner.Main;

public class LogUtils {
    public static void dev(String text) {
        Main.getInstance().getLogger().info(HexUtils.translate(text));
    }

    public static void warning(String text) {
        Main.getInstance().getLogger().warning(HexUtils.translate(text));
    }
}
