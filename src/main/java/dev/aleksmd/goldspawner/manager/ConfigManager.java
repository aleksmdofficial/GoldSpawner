package dev.aleksmd.goldspawner.manager;

import dev.aleksmd.goldspawner.Main;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
    private static FileConfiguration config;
    private static FileConfiguration craftingConfig;
    private static FileConfiguration itemsConfig;
    private static FileConfiguration messagesConfig;

    public static void loadConfigs(Main plugin) {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        craftingConfig = loadConfig(plugin, "craft.yml");
        itemsConfig = loadConfig(plugin, "items.yml");
        messagesConfig = loadConfig(plugin, "messages.yml");
    }

    private static FileConfiguration loadConfig(Main plugin, String fileName) {
        plugin.getLogger().info("Attempting to load configuration file: " + fileName);
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.getLogger().warning("File " + fileName + " does not exist. Saving default...");
            plugin.saveResource(fileName, false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config == null) {
            plugin.getLogger().severe("Failed to load configuration file: " + fileName);
        } else {
            plugin.getLogger().info("Successfully loaded configuration file: " + fileName);
        }
        return config;
    }


    public static FileConfiguration getConfig() {
        return config;
    }

    public static FileConfiguration getCraftingConfig() {
        return craftingConfig;
    }

    public static FileConfiguration getItemsConfig() {
        return itemsConfig;
    }

    public static FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public static void reloadConfigs(Main plugin) {
        plugin.reloadConfig();
        config = plugin.getConfig();
        craftingConfig = loadConfig(plugin, "craft.yml");
        itemsConfig = loadConfig(plugin, "items.yml");
        messagesConfig = loadConfig(plugin, "messages.yml");
    }

    public static void saveConfig(FileConfiguration config, String fileName, Main plugin) {
        try {
            config.save(new File(plugin.getDataFolder(), fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
