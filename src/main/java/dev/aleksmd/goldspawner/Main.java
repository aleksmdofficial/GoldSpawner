package dev.aleksmd.goldspawner;

import dev.aleksmd.goldspawner.commands.GoldAxeCMD;
import dev.aleksmd.goldspawner.commands.GoldSpawnerCMD;
import dev.aleksmd.goldspawner.commands.GoldenCompassCMD;
import dev.aleksmd.goldspawner.listener.CompassListener;
import dev.aleksmd.goldspawner.spawner.GoldenCompassSpawner;
import dev.aleksmd.goldspawner.items.GoldSpawner;
import dev.aleksmd.goldspawner.listener.BukkitListener;
import dev.aleksmd.goldspawner.manager.ConfigManager;
import dev.aleksmd.goldspawner.spawner.Crafting;  // Импортируем класс Crafting
import dev.aleksmd.goldspawner.spawner.GiveMoney;

import java.util.List;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public final class Main extends JavaPlugin {
    private static Main instance;
    private static Economy econ;
    private static List<Location> spawnerLocations;  // Список для хранения координат спавнеров


    @Override
    public void onEnable() {
        // Загрузка конфигурации
        ConfigManager.loadConfigs(this);

        instance = this;
        // Загрузка спавнеров из файла
        new GoldSpawner().loadSpawnersFromFile();
        // Настройка экономики
        if (!setupEconomy()) {
            Logger.getLogger("Minecraft").warning("Failed to setup economy!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Регистрация команды и обработчиков событий
        GoldSpawnerCMD commandExecutor = new GoldSpawnerCMD();
        getCommand("goldspawner").setExecutor(commandExecutor);
        getServer().getPluginManager().registerEvents(new GoldSpawner(), this);
        this.getServer().getPluginManager().registerEvents(new BukkitListener(), this);
        // Регистрация команды
        this.getCommand("goldencompass").setExecutor(new GoldenCompassCMD());
        // Регистрация слушателя событий
        getServer().getPluginManager().registerEvents(new CompassListener(), this);
        getCommand("goldaxepick").setExecutor(new GoldAxeCMD(this));
        this.saveDefaultConfig();

        // Регистрация рецепта крафта
        Crafting.registerRecipes(this);

        // Запуск глобальной задачи
        startGlobalTask();
    }

    @Override
    public void onDisable() {
        stopGlobalTask();
        // Сохранение спавнеров в файл перед завершением работы
        new GoldSpawner().saveSpawnersToFile();
    }


    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private void startGlobalTask() {
        int interval = ConfigManager.getItemsConfig().getInt("spawner.settings.cooldown") * 20;
        new BukkitRunnable() {
            public void run() {
                GiveMoney.giveMoneyToAllPlayers();
            }
        }.runTaskTimer(this, interval, interval);
    }
    public static List<Location> getSpawnerLocations() {
        return spawnerLocations;
    }
    private void stopGlobalTask() {
        getServer().getScheduler().cancelTasks(this);
    }

    public static Main getInstance() {
        return instance;
    }

    public static Economy getEconomy() {
        return econ;
    }
}
