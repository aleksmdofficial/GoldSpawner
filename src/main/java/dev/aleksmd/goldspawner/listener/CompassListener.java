package dev.aleksmd.goldspawner.listener;

import dev.aleksmd.goldspawner.items.GoldenCompass;
import dev.aleksmd.goldspawner.spawner.GoldenCompassSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CompassListener implements Listener {

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Проверка, если игрок использует золотой компас
        if (GoldenCompass.isGoldenCompass(item)) {
            event.setCancelled(true); // Предотвращаем дальнейшую обработку события

            // Выдаем координаты спавнера игроку
            GoldenCompassSpawner.giveSpawnerCoordinates(player);

            // Компас может быть одноразовым
            player.getInventory().remove(item);
        }
    }
}
