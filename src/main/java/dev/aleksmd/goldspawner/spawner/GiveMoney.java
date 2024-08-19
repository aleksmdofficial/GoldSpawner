package dev.aleksmd.goldspawner.spawner;

import dev.aleksmd.goldspawner.Main;
import dev.aleksmd.goldspawner.items.GoldSpawner;
import dev.aleksmd.goldspawner.manager.ConfigManager;
import dev.aleksmd.goldspawner.utils.HexUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class GiveMoney {

    private static Economy econ = Main.getEconomy();

    public static void giveMoneyToAllPlayers() {
        // Получаем значения из конфигурации
        int radius = ConfigManager.getItemsConfig().getInt("spawner.settings.radius");
        int minMoney = ConfigManager.getItemsConfig().getInt("spawner.settings.min");
        int maxMoney = ConfigManager.getItemsConfig().getInt("spawner.settings.max");

        // Проверяем игроков в радиусе спавнеров
        Set<Player> players = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Location loc : GoldSpawner.getSpawnerLocations().keySet()) {
                double distance = player.getLocation().distance(loc);
                if (distance <= radius) {
                    players.add(player);
                }
            }
        }

        if (players.isEmpty()) {
            return;
        }

        // Рассчитываем общую сумму денег для раздачи
        int totalMoney = 0;
        for (Location loc : GoldSpawner.getSpawnerLocations().keySet()) {
            int randomAmount = (int) (Math.random() * (maxMoney - minMoney + 1) + minMoney);
            totalMoney += randomAmount;
        }

        // Раздача денег игрокам
        for (Player player : players) {
            econ.depositPlayer((OfflinePlayer) player, totalMoney);
            for (String line : ConfigManager.getMessagesConfig().getStringList("messages.spawner.give-money")) {
                player.sendMessage(HexUtils.translate(line.replace("%money%", String.valueOf(totalMoney))));
            }
        }
    }
}
