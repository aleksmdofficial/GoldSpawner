package dev.aleksmd.goldspawner.spawner;

import dev.aleksmd.goldspawner.Main;
import dev.aleksmd.goldspawner.items.GoldSpawner;
import dev.aleksmd.goldspawner.manager.ConfigManager;
import dev.aleksmd.goldspawner.utils.HexUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class GoldenCompassSpawner {

    private static final Random random = new Random();

    /**
     * Выдает координаты случайного золотого спавнера игроку.
     *
     * @param player игрок, который активировал компас.
     */
    public static void giveSpawnerCoordinates(Player player) {
        // Получаем список спавнеров из карты, которая хранится в памяти
        Map<Location, Boolean> spawnerLocations = GoldSpawner.getSpawnerLocations();

        // Проверка, есть ли спавнеры в памяти
        if (spawnerLocations.isEmpty()) {
            String noSpawnerMsg = ConfigManager.getMessagesConfig().getString("messages.compass.no-spawner", "&cЗолотой спавнер не найден!");
            player.sendMessage(HexUtils.translate(noSpawnerMsg));
            return;
        }

        // Преобразуем ключи карты (локации спавнеров) в список для работы с ними
        List<Location> locationsList = spawnerLocations.keySet().stream().toList();

        // Выбираем случайный спавнер
        Location spawnerLocation = locationsList.get(random.nextInt(locationsList.size()));

        // Формируем и отправляем игроку сообщение с координатами спавнера
        String coordinates = String.format("X: %d, Y: %d, Z: %d",
                spawnerLocation.getBlockX(),
                spawnerLocation.getBlockY(),
                spawnerLocation.getBlockZ());
        
        String coordsMsg = ConfigManager.getMessagesConfig().getString("messages.compass.coordinates", "&eКоординаты золотого спавнера: &f%coordinates%");
        player.sendMessage(HexUtils.translate(coordsMsg.replace("%coordinates%", coordinates)));
    }
}
