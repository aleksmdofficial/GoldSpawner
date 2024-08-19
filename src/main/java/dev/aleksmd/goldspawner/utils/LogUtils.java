package dev.aleksmd.goldspawner.utils;

public class LogUtils {
    public static void dev(String text) {
        System.out.println(HexUtils.translate("&8[&#FB7A08Holy&fGoldSpawner&8] " + text));
    }

    public static void warning(String text) {
        System.out.println(HexUtils.translate("&8[&#FF5900Внимание&8] " + text));
    }
}
